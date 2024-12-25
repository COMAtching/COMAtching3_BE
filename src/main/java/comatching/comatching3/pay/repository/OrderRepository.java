package comatching.comatching3.pay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comatching.comatching3.pay.entity.Orders;
import comatching.comatching3.users.entity.Users;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

	Optional<Orders> findByOrderUuid(String orderUuid);

	// @EntityGraph를 사용하면, JPA가 LEFT OUTER JOIN을 통해 Orders와 TossPayment 데이터를 한 번에 로드
	@EntityGraph(attributePaths = {"tossPayment"})
	List<Orders> findAllByUsers(Users user);
}
