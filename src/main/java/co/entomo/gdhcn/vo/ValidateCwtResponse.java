package co.entomo.gdhcn.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@Builder
public class ValidateCwtResponse {
    private Map<Integer, StepStatus> validationStatus;
    private SHLinkContent shLinkContent;
}
