package comatching.comatching3.history.service;

import comatching.comatching3.history.entity.PointHistory;
import comatching.comatching3.history.enums.PointHistoryType;
import comatching.comatching3.history.repository.PointHistoryRepository;
import comatching.comatching3.pay.entity.Orders;
import comatching.comatching3.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    public void makeChargePointHistory(Users user, PointHistoryType pointHistoryType, Orders order, Long amount) {
        PointHistory pointHistory = PointHistory.builder()
                .users(user)
                .pointHistoryType(pointHistoryType)
                .changeAmount(amount)
                .totalPoint(user.getPoint())
                .orders(order)
                .build();


        pointHistoryRepository.save(pointHistory);
    }

    public void makePointHistoryWithReason(Users user, PointHistoryType pointHistoryType, Long amount, String reason) {
        PointHistory pointHistory = PointHistory.builder()
                .users(user)
                .pointHistoryType(pointHistoryType)
                .changeAmount(amount)
                .reason(reason)
                .totalPoint(user.getPoint())
                .build();

        pointHistoryRepository.save(pointHistory);
    }
}
