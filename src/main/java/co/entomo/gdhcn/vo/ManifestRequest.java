package co.entomo.gdhcn.vo;

import lombok.Builder;
import lombok.Data;
/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@Builder
public class ManifestRequest {
    private String recipient;
    private String passcode;
}
