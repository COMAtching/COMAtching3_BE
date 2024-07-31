package comatching.comatching3.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
	private Integer status;
	private String code;
	private String message;
	private T data;

	public static Response<Void> ok(){
		Response<Void> response = new Response<>();
		response.status = HttpStatus.OK.value();
		response.code = ResponseCode.SUCCESS.getCode();
		return response;
	}

	public static <T> Response<T> ok(T data) {
		Response<T> response = new Response<>();
		response.status = HttpStatus.OK.value();
		response.code = ResponseCode.SUCCESS.getCode();
		response.data = data;
		return response;
	}

	public static <T> Response<T> errorResponse(ResponseCode code) {
		Response<T> response = new Response<>();
		response.status = code.getStatus();
		response.code = code.getCode();
		response.data = null;
		response.message = code.getMessage();
		return response;
	}

	public Response(ResponseCode responseCode){
		this.code = responseCode.getCode();
		this.status = responseCode.getStatus();
		this.message = responseCode.getMessage();
		this.data = null;
	}

	public String convertToJson() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}
}
