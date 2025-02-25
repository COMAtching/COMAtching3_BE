package comatching.comatching3.auth.flutter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlutterLoginRes {

	private String socialId;
	private String role;
}
