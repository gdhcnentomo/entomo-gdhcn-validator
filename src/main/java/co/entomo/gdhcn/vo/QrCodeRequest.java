package co.entomo.gdhcn.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@Builder
public class QrCodeRequest {

	private String passCode;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date expiresOn;
	private String jsonContent;
	private String privateKeyContent;
	private String countryCode;
	private String kid;
}
