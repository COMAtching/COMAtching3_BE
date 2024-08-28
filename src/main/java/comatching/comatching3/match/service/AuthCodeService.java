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

	public CodeCheckRes checkCode(CodeCheckReq req){
		CodeCheckInfo codeCheckInfo;
		try{
			codeCheckInfo = redisUtil.getRedisValue(req.getCode(), CodeCheckInfo.class);
		} catch( JsonProcessingException e){
			throw new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL);
		}

		if(codeCheckInfo == null){
			throw new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL);
		}

		Users applier = usersRepository.findById(codeCheckInfo.getUserId())
			.orElseThrow( () -> new BusinessException(ResponseCode.MATCH_CODE_CHECK_FAIL));

		codeCheckInfo.updateCheckStatus(CheckStatus.AUTHENTICATED);

		return new CodeCheckRes(applier.getPoint());
	}

	public void updateTime(String code){
	}
}
