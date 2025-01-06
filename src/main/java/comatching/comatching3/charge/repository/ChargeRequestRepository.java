package comatching.comatching3.charge.repository;

/*@Repository
public interface ChargeRequestRepository extends JpaRepository<ChargeRequest, Long> {

    @Query("SELECT new comatching.comatching3.charge.dto.response.ChargePendingInfo(" +
            "u.userAiFeature.uuid, u.username, cr.amount, u.point, cr.createdAt) " +
            "FROM ChargeRequest cr " +
            "JOIN cr.users u " +
            "JOIN u.userAiFeature uaf")
    List<ChargePendingInfo> findAllChargePendingInfo();

    Boolean existsByUsers(Users user);

    void deleteByUsers(Users user);
}*/
