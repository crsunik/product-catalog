package com.jchmiel.roche.order;

import com.jchmiel.roche.order.dto.OrderDTO;
import com.jchmiel.roche.order.dto.PlaceOrderDTO;
import com.jchmiel.roche.order.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {

	private final OrderCreator orderCreator;
	private final OrderRepository orderRepository;

	public OrderDTO place(PlaceOrderDTO placeOrder) {
		Order order = orderCreator.createCreator(placeOrder);
		return orderRepository.save(order).toDTO();
	}

	public OrderDTO find(Long orderId) {
		return orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new).toDTO();
	}

	public List<OrderDTO> findAllPlacedBetween(LocalDate from, LocalDate to) {
		return Order.toDTO(orderRepository.findByCreatedDateBetween(from, to));
	}
}
