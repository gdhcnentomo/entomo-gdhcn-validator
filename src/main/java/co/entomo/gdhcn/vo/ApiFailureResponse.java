package co.entomo.gdhcn.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiFailureResponse {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    int code;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object data;

    boolean status;

    public ApiFailureResponse(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
