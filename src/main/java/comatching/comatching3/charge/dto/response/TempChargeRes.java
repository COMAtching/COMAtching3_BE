package comatching.comatching3.charge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempChargeRes {

	private String orderId;
	private String productName;
	private String username;
	private Long point;
	private Long price;
	private String requestAt;
	private String realName;
}
