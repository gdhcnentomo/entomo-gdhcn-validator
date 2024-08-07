package co.entomo.gdhcn.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManifestRequest {
    private String recipient;
    private String passcode;
}
