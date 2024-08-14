package co.entomo.gdhcn.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@JsonIgnoreProperties
public class GdhcnCertificateVO {
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date timestamp;
	private String country;
	private String thumbprint;
	private String rawData;
	private String signature;
	private String kid;
	private String certificateType;
	
}
