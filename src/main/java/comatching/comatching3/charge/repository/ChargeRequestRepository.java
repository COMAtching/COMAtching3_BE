package comatching.comatching3.charge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comatching.comatching3.charge.entity.ChargeRequest;
import comatching.comatching3.pay.enums.OrderStatus;
import comatching.comatching3.users.entity.Users;

@Repository
public interface ChargeRequestRepository extends JpaRepository<ChargeRequest, Long> {

    /*@Query("SELECT new comatching.comatching3.charge.dto.response.ChargePendingInfo(" +
            "u.userAiFeature.uuid, u.username, cr.amount, u.point, cr.createdAt) " +
            "FROM ChargeRequest cr " +
            "JOIN cr.users u " +
            "JOIN u.userAiFeature uaf")
    List<ChargePendingInfo> findAllChargePendingInfo();
*/
    Boolean existsByUsers(Users user);

    void deleteByUsers(Users user);

    Optional<ChargeRequest> findByOrderId(String orderId);

    List<ChargeRequest> findAllByUsers(Users user);

    List<ChargeRequest> findAllByOrderStatus(OrderStatus status);

    Optional<ChargeRequest> findByUsersAndOrderStatus(Users user, OrderStatus orderStatus);
}
