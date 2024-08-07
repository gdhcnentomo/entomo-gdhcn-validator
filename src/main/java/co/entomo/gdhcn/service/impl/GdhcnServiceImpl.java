package co.entomo.gdhcn.service.impl;

import COSE.*;
import co.entomo.gdhcn.entity.QrCode;
import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.hcert.GreenCertificateDecoder;
import co.entomo.gdhcn.hcert.GreenCertificateEncoder;
import co.entomo.gdhcn.repository.QrCodeRepository;
import co.entomo.gdhcn.service.GdhcnService;
import co.entomo.gdhcn.util.AmazonClientUtil;
import co.entomo.gdhcn.util.CertificateUtils;
import co.entomo.gdhcn.util.HttpClientUtils;
import co.entomo.gdhcn.vo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.minvws.encoding.Base45;
import org.apache.commons.compress.compressors.CompressorException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class GdhcnServiceImpl implements GdhcnService {

    @Value("${gdhcn.baseUrl}")
    private String baseUrl;
    @Value("${aws.bucket.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.bucket.s3.json.folder}")
    private String jsonFolder;
    @Autowired
    private QrCodeRepository qrCodeRepository;
    @Autowired
    private CertificateUtils certificateUtils;
    @Autowired
    private HttpClientUtils httpClientUtils;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AmazonClientUtil amazonClientUtil;
    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String uploadIpsJson(QrCodeRequest qrCodeRequest) throws GdhcnValidationException {
        try {
            byte[] key = generateRandomSequence();
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + ".json";
            String jsonUrl = bucketName + File.separator + jsonFolder + File.separator + fileName;
            String shUrl = null;
            if (StringUtils.hasLength(qrCodeRequest.getPassCode())) {
                shUrl = baseUrl + "/v2/manifests/" + uuid;
            } else {
                shUrl = baseUrl + "/v2/ips-json/" + uuid;
            }
            SHLinkContent shLinkPayload = SHLinkContent.builder()
                    .url(shUrl)
                    .flag(qrCodeRequest.getPassCode() != null ? "P" : "U")
                    .label("GDHCN Validator")
                    .exp(qrCodeRequest.getExpiresOn() != null ? qrCodeRequest.getExpiresOn().getTime() : null)
                    .key(Base64.getUrlEncoder().encodeToString(key))
                    .build();

            QrCode qrCode = modelMapper.map(qrCodeRequest, QrCode.class);
            qrCode.setJsonUrl(jsonUrl);
            qrCode.setId(uuid);
            qrCode.setKey(Base64.getUrlEncoder().encodeToString(key));
            qrCode.setFlag(shLinkPayload.getFlag());
            qrCodeRepository.save(qrCode);
            amazonClientUtil.uploadFileToBucket(fileName, qrCodeRequest.getJsonContent(), jsonFolder);
            String shLinkConsent = "shlink://" + Base64.getEncoder().encodeToString(OBJECT_MAPPER.writeValueAsString(shLinkPayload).getBytes(StandardCharsets.UTF_8));

            long expiredInMillies = new Date(Long.MAX_VALUE).getTime() / 1000L;
            if (!ObjectUtils.isEmpty(shLinkPayload.getExp())) {
                expiredInMillies = shLinkPayload.getExp() / 1000L;
            }

            SmartHealthLink link = SmartHealthLink.builder().shLink(shLinkConsent).build();
            List<SmartHealthLink> list = new ArrayList<SmartHealthLink>();
            HealthCertificate hCert = HealthCertificate.builder().healthLinks(list).build();
            list.add(link);
            CertificatePayload payload = CertificatePayload.builder()
                    .iat(System.currentTimeMillis())
                    .iss(qrCodeRequest.getCountryCode())
                    .healthCertificate(hCert)
                    .exp(expiredInMillies)
                    .build();

            String payLoadJson = OBJECT_MAPPER.writeValueAsString(payload);
            PrivateKey privateKey = certificateUtils.getPrivateKey(qrCodeRequest.getPrivateKeyContent(), qrCodeRequest.getPrivateKeyContent());
            OneKey cborPrivateKey = new OneKey(null, privateKey);
            String encoded = new GreenCertificateEncoder(cborPrivateKey, qrCodeRequest.getKid()).encode(payLoadJson);
            return encoded;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (CoseException e) {
            e.printStackTrace();
        } catch (CompressorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ValidateCwtResponse validateCwt(String qrCodeEncodedContent) {
        Map<Integer, StepStatus> response = getDefaultStatus();
        SHLinkContent shLinkContent = null;
        int step = 0;
        try {
            log.info("qrCodeEncodedContent : " + qrCodeEncodedContent);
            log.info("response : " + response);
            if (!ObjectUtils.isEmpty(qrCodeEncodedContent)) {
                qrCodeEncodedContent = qrCodeEncodedContent.substring(4);
                byte[] decodedBytes = Base45.getDecoder().decode(qrCodeEncodedContent);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                byte[] coseBytes = GreenCertificateDecoder.getCoseBytes(decodedBytes);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                Sign1Message msg = (Sign1Message) Message.DecodeFromBytes(coseBytes, MessageTag.Sign1);
                log.info("Sign1Message : " + msg.toString());
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                String json = GreenCertificateDecoder.getJsonString(msg.GetContent());
                log.info("Json : " + json);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                CertificatePayload certificatePayLoad = OBJECT_MAPPER.readValue(json, CertificatePayload.class);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                log.info("Protected Header: " + msg.getProtectedAttributes());
                String kid = msg.getProtectedAttributes().get(HeaderKeys.KID.AsCBOR()).ToObject(String.class);
                GdhcnCertificateVO gdhcnCertificateVO = httpClientUtils.getGdhcnCertificate(certificatePayLoad.getIss(), kid);
                if (gdhcnCertificateVO == null)
                    throw new RuntimeException("Unable to Fetch GDHCN Certificate");
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                PublicKey publicKey = certificateUtils.getPublicKey(gdhcnCertificateVO.getRawData());
                OneKey oneKey = new OneKey(publicKey, null);
                GreenCertificateDecoder decoder = new GreenCertificateDecoder(oneKey);
                boolean status = decoder.validate(msg);
                if (status)
                    updateStatus(response, ++step, ValidationStatus.SUCCESS);
                else
                    throw new RuntimeException("Signature Validation failed");

                String shLink = certificatePayLoad.getHealthCertificate().getHealthLinks().get(0).getShLink();
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                shLink = shLink.substring("shlink://".length());
                log.info("shLink: " + shLink);
                shLink = new String(Base64.getDecoder().decode(shLink));
                log.info("shLink: " + shLink);
                shLinkContent = OBJECT_MAPPER.readValue(shLink, SHLinkContent.class);

                if(shLinkContent.getExp()!=null)
                {
                    Date expDate = new Date(shLinkContent.getExp());
                    Date currentDate = new Date(System.currentTimeMillis());
                    if(currentDate.after(expDate)){
                        shLinkContent = null;
                        throw new RuntimeException("shlink expired");
                    }
                }
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(response, ++step, ValidationStatus.FAILED);
        }
        return ValidateCwtResponse.builder()
                .validationStatus(response)
                .shLinkContent(shLinkContent)
                .build();
    }

    private byte[] generateRandomSequence() {
        byte[] randomSequence = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomSequence);
        return randomSequence;
    }

    private Map<Integer, StepStatus> getDefaultStatus() {
        Map<Integer, StepStatus> map = new HashMap<Integer, StepStatus>();
        map.put(1, new StepStatus("1", ValidationStatus.PENDING, ValidationDescription.DECODE_BASE45));
        map.put(2, new StepStatus("2", ValidationStatus.PENDING, ValidationDescription.DEFLATE_COSE_BYTES));
        map.put(3, new StepStatus("3", ValidationStatus.PENDING, ValidationDescription.CONVERT_COSE_MESSAGE));
        map.put(4, new StepStatus("4", ValidationStatus.PENDING, ValidationDescription.COSE_MESSAGE_PAYLOAD_TO_JSON));
        map.put(5, new StepStatus("5", ValidationStatus.PENDING, ValidationDescription.EXTRACT_COUNTRY_CODE));
        map.put(6, new StepStatus("6", ValidationStatus.PENDING, ValidationDescription.FETCH_PUBLIC_KEY_GDHCN));
        map.put(7, new StepStatus("7", ValidationStatus.PENDING, ValidationDescription.VALIDATE_SIGNATURE));
        map.put(8, new StepStatus("8", ValidationStatus.PENDING, ValidationDescription.EXTRACT_HCERT));
        map.put(9, new StepStatus("9", ValidationStatus.PENDING, ValidationDescription.VALIDATE_EXPIRY));
        return map;

    }

    private void updateStatus(Map<Integer, StepStatus> response, Integer step, ValidationStatus status) {
        response.get(step).setStatus(status);
        if (status == ValidationStatus.FAILED)
            response.get(step).setError(response.get(step).getCode().getErrorMessage());
    }

    @Override
    public Map<String, List<Map<String, String>>> getManifest(ManifestRequest manifestRequest, String jsonId) throws GdhcnValidationException {
        Map<String, List<Map<String, String>>> response = new HashMap<String, List<Map<String, String>>>();
        QrCode qrCode = qrCodeRepository.findByIdAndPassCode(jsonId, manifestRequest.getPasscode()).get();
        if (qrCode != null) {
            String url = baseUrl + "/v2/ips-json/" + jsonId;
            response.put("files", List.of(Map.of("contentType", "application/fhir+json", "location", url)));
            return response;
        }
        throw new GdhcnValidationException("Invalid request");
    }

    @Override
    public String downloadJson(String jsonId) {
        try {
            QrCode qrCode = qrCodeRepository.findById(jsonId).get();
            if (qrCode != null) {
                String fileName = jsonId + ".json";
                InputStream is = amazonClientUtil.getFileInputStream(fileName, jsonFolder);
                byte[] rawContent = is.readAllBytes();
                String jsonContent = new String(rawContent);
                log.info("Downloaded json: " + jsonContent);
                return jsonContent;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String downloadJWEJson(String jsonId) {
        try {
            QrCode qrCode = qrCodeRepository.findById(jsonId).get();
            if (qrCode != null) {
                String fileName = jsonId + ".json";
                InputStream is = amazonClientUtil.getFileInputStream(fileName, jsonFolder);
                byte[] rawContent = is.readAllBytes();
                String jsonContent = new String(rawContent);
                log.info("Downloaded json: " + jsonContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
