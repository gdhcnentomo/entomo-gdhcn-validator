package co.entomo.gdhcn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.compressors.CompressorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.authlete.cwt.CWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import COSE.CoseException;
import COSE.Message;
import COSE.MessageTag;
import COSE.OneKey;
import COSE.Sign1Message;
import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.hcert.GreenCertificateDecoder;
import co.entomo.gdhcn.service.GdhcnService;
import co.entomo.gdhcn.util.CertificateUtils;
import co.entomo.gdhcn.util.HttpClientUtils;
import co.entomo.gdhcn.vo.CertificatePayload;
import co.entomo.gdhcn.vo.GdhcnCertificateVO;
import co.entomo.gdhcn.vo.QrCodeRequest;
import co.entomo.gdhcn.vo.StepStatus;
import nl.minvws.encoding.Base45;

@SpringBootTest
class EntomoGdhcnMsApplicationTests {

	@Autowired
	GdhcnService gdhcnService;
	@Autowired
	HttpClientUtils httpClientUtils;
	@Autowired
	CertificateUtils certificateUtils;
	private ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	@Test
	void contextLoads() {
	}
	@Test
	void testRest() throws IOException, InterruptedException, GdhcnValidationException 
	{
		GdhcnCertificateVO vo = httpClientUtils.getGdhcnCertificate("XM","qb4L0atiX/4=");
		PublicKey pk = certificateUtils.getPublicKey(vo.getCertificate());
		System.out.println(pk.getFormat());
       
	}
	
	@Test
	void base45() {
		byte[] data = Base45.getEncoder().encode("shlink://ababcbc".getBytes(StandardCharsets.UTF_8));
		String json = new String(Base45.getDecoder().decode(data));
	}
}
