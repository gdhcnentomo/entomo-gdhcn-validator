package co.entomo.gdhcn.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.vo.GdhcnCertificateVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpClientUtils {

	@Autowired
	HttpClient httpClient;
	@Value("${gdhcn.dev.url}")
	private String gdhcnDevUrl;
	private ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	public GdhcnCertificateVO getGdhcnCertificate(String countryCode, String kid) throws GdhcnValidationException
	{
		log.info("Fetching url "+gdhcnDevUrl+"/trustList/DSC/"+countryCode);
		try {
			HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create(gdhcnDevUrl+"/trustList/DSC/"+countryCode))
			        .GET()
			        .build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			log.info("Response statuc code "+response.statusCode());
			if(response.statusCode() == HttpStatus.SC_OK)
			{
				log.info("Response Body: "+response.body());				
				List<GdhcnCertificateVO> voList = OBJECT_MAPPER.readValue(response.body(), new TypeReference<List<GdhcnCertificateVO>>() {});
				if(kid !=null){
					for (GdhcnCertificateVO gdhcnCertificateVO : voList) {
						if(gdhcnCertificateVO.getKid().contentEquals(kid)){
							log.info("Kid Identified in GDHCN Network");
							return gdhcnCertificateVO;
						}
					}
				}
				return null;
				//log.info("Kid Not found in GDHCN Network. Returning first available");
				//return voList.get(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new GdhcnValidationException(e.getLocalizedMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new GdhcnValidationException(e.getLocalizedMessage());
		}
		throw new GdhcnValidationException("Country "+countryCode+" not found");
	}
}
