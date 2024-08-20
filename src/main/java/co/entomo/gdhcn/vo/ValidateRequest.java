package co.entomo.gdhcn.vo;/**
 * @author Uday Matta
 */

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @organization Entomo Labs
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidateRequest {
 @NotBlank(message = "Qr CodeContent is mandatory")
 private String qrCodeContent;
}
