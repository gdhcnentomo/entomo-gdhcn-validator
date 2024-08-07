package co.entomo.gdhcn.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SHLinkContent {
	private String url;
    private String flag;
    private Long exp;
    private String key;
    private String label;
}
