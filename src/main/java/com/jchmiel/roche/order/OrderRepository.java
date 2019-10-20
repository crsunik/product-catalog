package com.jchmiel.roche.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByCreatedDateBetween(LocalDate from, LocalDate to);
}
