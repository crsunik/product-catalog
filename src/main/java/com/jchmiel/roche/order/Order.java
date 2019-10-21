package com.jchmiel.roche.order;

import com.jchmiel.roche.order.dto.OrderDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Entity
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderLine> orderLines;

	@Column(nullable = false)
	private BigDecimal totalAmount;

	@Column(nullable = false)
	private LocalDate createdDate;

	@Column(nullable = false)
	private String buyerEmail;

	public OrderDTO toDTO() {
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setBuyerEmail(buyerEmail);
		orderDTO.setCreatedDate(createdDate);
		orderDTO.setId(id);
		orderDTO.setOrderLines(orderLines == null ? null : orderLines.stream().map(OrderLine::toDTO).collect(Collectors.toList()));
		orderDTO.setTotalAmount(totalAmount);
		return orderDTO;
	}

	static List<OrderDTO> toDTO(Iterable<Order> orders) {
		return StreamSupport.stream(orders.spliterator(), false).map(Order::toDTO).collect(Collectors.toList());
	}
}
