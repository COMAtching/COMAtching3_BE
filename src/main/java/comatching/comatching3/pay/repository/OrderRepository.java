package comatching.comatching3.pay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import comatching.comatching3.pay.entity.Orders;
import comatching.comatching3.users.entity.Users;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

	Optional<Orders> findByOrderUuid(String orderUuid);

	@Query("select o from Orders o join fetch o.tossPayment tp where o.users = :user and o.orderStatus = 'ORDER_COMPLETE'")
	List<Orders> findCompleteByUsers(@Param("user") Users user);

	@Query("select o from Orders o join fetch o.tossPayment tp where o.users = :user")
	List<Orders> findAllByUsers(@Param("user") Users user);

}
