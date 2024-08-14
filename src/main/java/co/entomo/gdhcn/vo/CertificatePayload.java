package co.entomo.gdhcn.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertificatePayload {

    @JsonProperty("1")
    private String iss;

    @JsonProperty("6")
    private long iat;

    @JsonProperty("4")
    private long exp;

    @JsonProperty("-260")
    private HealthCertificate healthCertificate;

}
