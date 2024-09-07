package comatching.comatching3.charge.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import comatching.comatching3.history.enums.PointHistoryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comatching.comatching3.charge.dto.request.ChargeApprovalReq;
import comatching.comatching3.charge.dto.request.ChargeCancelReq;
import comatching.comatching3.charge.dto.request.ChargeReq;
import comatching.comatching3.charge.dto.response.ChargePendingInfo;
import comatching.comatching3.charge.entity.ChargeRequest;
import comatching.comatching3.charge.repository.ChargeRequestRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.history.repository.PointHistoryRepository;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeService {

    private final ChargeRequestRepository chargeRequestRepository;
    private final SecurityUtil securityUtil;
    private final UsersRepository usersRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 유저의 충전 요청 메소드
     * @param chargeReq
     */
    @Transactional
    public void createChargeRequest(ChargeReq chargeReq) {
        Users user = securityUtil.getCurrentUsersEntity();

        Boolean alreadyRequest = chargeRequestRepository.existsByUsers(user);
        if (alreadyRequest) {
            throw new BusinessException(ResponseCode.BAD_REQUEST);
        }

        //db에 저장
        ChargeRequest chargeRequest = ChargeRequest.builder()
                .users(user)
                .amount(chargeReq.getAmount())
                .build();

        chargeRequestRepository.save(chargeRequest);

        //웹소켓 업데이트
        ChargePendingInfo chargePendingInfo = ChargePendingInfo.builder()
                .userId(user.getUserAiFeature().getUuid())
                .username(user.getUsername())
                .requestAmount(chargeRequest.getAmount())
                .existingPoints(user.getPoint())
                .createdAt(LocalDateTime.now())
                .build();

        // 프론트에서 혹시 리스트로 받는게 편할 수 있어서 리스트로 보내는 형식 임시 추가
        List<ChargePendingInfo> chargePendingInfos = Arrays.asList(chargePendingInfo);
//        simpMessagingTemplate.convertAndSend("/topic/chargeRequests", chargePendingInfo);
        simpMessagingTemplate.convertAndSend("/topic/chargeRequests", chargePendingInfos);
    }

    /**
     * 충전 요청 목록 조회 메소드
     * @return 충전 요청 목록
     */
    public List<ChargePendingInfo> getAllChargeRequests() {
        return chargeRequestRepository.findAllChargePendingInfo();
    }

    /**
     * 관리자의 충전 요청 승인 메소드
     * @param approvalReq 웹 소켓으로 들어온 정보
     */
    @Transactional
    public void createApprovalRequest(ChargeApprovalReq approvalReq) {
        Users user = usersRepository.findUsersByUuid(UUIDUtil.uuidStringToBytes(approvalReq.getUserId()))
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        PointHistory pointHistory = PointHistory.builder()
                .users(user)
                .pointHistoryType(PointHistoryType.CHARGE)
                .changeAmount(approvalReq.getAmount())
                .pickMe(user.getPickMe())
                .build();

        //DB 반영
        user.addPoint(approvalReq.getAmount());
        pointHistory.setTotalPoint(user.getPoint());
        chargeRequestRepository.deleteByUsers(user);
        pointHistoryRepository.save(pointHistory);

        user.getPointHistoryList().add(pointHistory);

        //웹 소켓 반영
        simpMessagingTemplate.convertAndSend("/topic/approvalUpdate", approvalReq.getUserId());
    }

    @Transactional
    public void cancelChargeRequest(ChargeCancelReq chargeCancelReq) {
        Users user = usersRepository.findUsersByUuid(UUIDUtil.uuidStringToBytes(chargeCancelReq.getUserId()))
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        chargeRequestRepository.deleteByUsers(user);
        simpMessagingTemplate.convertAndSend("/topic/cancelUpdate", chargeCancelReq.getUserId());
    }
}
