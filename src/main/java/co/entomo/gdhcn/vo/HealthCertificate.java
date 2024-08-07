package co.entomo.gdhcn.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthCertificate {

    @JsonProperty("5")
    private List<SmartHealthLink> healthLinks;
    
}
