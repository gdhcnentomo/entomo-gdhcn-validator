package co.entomo.gdhcn.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@AllArgsConstructor
public class StepStatus {

    private String step;
    private ValidationStatus status;
    private ValidationDescription code;
    private String description;
    private String error;

    public StepStatus(String step, ValidationStatus status, ValidationDescription code) {
        this.step = step;
        this.status = status;
        this.code = code;
        this.description = code.getDescription();
        this.error = null;
    }

}
