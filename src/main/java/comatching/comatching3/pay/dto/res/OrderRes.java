package comatching.comatching3.pay.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRes {
	private String customerKey;
	private String orderId;
	private String email;
	private String username;
}
