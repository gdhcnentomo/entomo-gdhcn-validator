package co.entomo.gdhcn.hcert;

import COSE.*;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import nl.minvws.encoding.Base45;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GreenCertificateEncoder {

    private final OneKey privateKey;
    private String kid;

    public GreenCertificateEncoder(OneKey privateKey, String kid) {
        this.privateKey = privateKey;
        this.kid = kid;
    }

    /**
     * Encodes arbitrary Json String to CBOR -> COSE -> Deflate -> BASE45
     *
     * @param json
     * @return
     * @throws CoseException
     * @throws CompressorException
     * @throws IOException
     */
    public String encode(String json) throws CoseException, CompressorException, IOException {

        byte[] cborBytes = getCborBytes(json);

        byte[] coseBytes = getCOSEBytes(cborBytes);

        byte[] deflateBytes = getDeflateBytes(coseBytes);

        return getBase45(deflateBytes);
    }

    private String getBase45(byte[] deflateBytes) {

        return "HC1:" + Base45.getEncoder().encodeToString(deflateBytes);
    }

    private byte[] getDeflateBytes(byte[] messageBytes) throws CompressorException, IOException {
        ByteArrayOutputStream deflateOutputStream = new ByteArrayOutputStream();
        try (CompressorOutputStream deflateOut = new CompressorStreamFactory()
                .createCompressorOutputStream(CompressorStreamFactory.DEFLATE, deflateOutputStream)) {

            deflateOut.write(messageBytes);
        }

        return deflateOutputStream.toByteArray();
    }

    private byte[] getCOSEBytes(byte[] cborBytes) throws CoseException {
        Sign1Message msg = new Sign1Message();
        msg.addAttribute(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR(), Attribute.PROTECTED);
        //msg.addAttribute(HeaderKeys.Algorithm, privateKey.get(KeyKeys.Algorithm), Attribute.PROTECTED);
        msg.addAttribute(HeaderKeys.KID, CBORObject.FromObject(kid), Attribute.PROTECTED);
        msg.SetContent(cborBytes);
        msg.sign(privateKey);

        return msg.EncodeToBytes();
    }

    private byte[] getCborBytes(String json) {
        CBORObject cborObject = CBORObject.FromJSONString(json);

        return cborObject.EncodeToBytes();
    }
}