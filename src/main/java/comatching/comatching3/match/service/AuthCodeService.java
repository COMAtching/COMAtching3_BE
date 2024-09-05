package comatching.comatching3.match.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.match.dto.cache.CodeCheckInfo;
import comatching.comatching3.match.dto.request.CodeCheckReq;
import comatching.comatching3.match.dto.response.CodeCheckRes;
import comatching.comatching3.match.dto.response.RequestCodeRes;
import comatching.comatching3.match.enums.CheckStatus;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.RedisUtil;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthCodeService {

	private final RedisUtil redisUtil;
	private final SecurityUtil securityUtil;
	private final UsersRepository usersRepository;

	/**
	 * 코드 생성 및 캐싱 메서드
	 * @return
	 */
	public RequestCodeRes requestCode(){
		Long userId = securityUtil.getCurrentUsersEntity().getId();
		String authCode = UUIDUtil.bytesToHex(UUIDUtil.createUUID());
		CodeCheckInfo matchAuth = CodeCheckInfo.builder()
			.checkStatus(CheckStatus.REQUESTED)
			.userId(userId)
			.build();

		try{
			redisUtil.putRedisValue(authCode, matchAuth, 600);
		} catch(JsonProcessingException e ){
			throw new BusinessException(ResponseCode.MATCH_CODE_GENERATE_FAIL);
		}

		return new RequestCodeRes(authCode);
	}

	/**
	 *
	 * @param req : 코드 유효성 체크
	 * @return : 코드 체크 응답
	 */

	public CodeCheckRes checkCode(CodeCheckReq req){
		CodeCheckInfo codeCheckInfo;
		try{
			codeCheckInfo = redisUtil.getRedisValue(req.getCode(), CodeCheckInfo.class);

			if(codeCheckInfo == null){
				throw new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL);
			}

			Users applier = usersRepository.findById(codeCheckInfo.getUserId())
				.orElseThrow( () -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));

			codeCheckInfo.updateCheckStatus(CheckStatus.AUTHENTICATED);
			redisUtil.putRedisValue(req.getCode(), codeCheckInfo, 6000);

			return new CodeCheckRes(applier.getPoint());
		} catch(JsonProcessingException e){
			throw new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL);
		}
	}

	public void updateTime(String code, CodeCheckInfo info){

		try{
			redisUtil.putRedisValue(code, info, 60000);
		} catch( JsonProcessingException e){
			throw new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL);
		}
	}
}
