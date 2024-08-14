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
public class SmartHealthLink {

	@JsonProperty("u")
	private String shLink;
}
