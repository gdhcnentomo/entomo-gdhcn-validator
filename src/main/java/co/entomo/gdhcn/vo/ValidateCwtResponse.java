package co.entomo.gdhcn.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ValidateCwtResponse {
    private Map<Integer, StepStatus> validationStatus;
    private SHLinkContent shLinkContent;
}
