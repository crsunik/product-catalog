package com.jchmiel.roche.order;

import com.jchmiel.roche.order.dto.OrderLineDTO;
import com.jchmiel.roche.product.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@Entity
public class OrderLine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn
	private Order order;

	@OneToOne
	private Product product;

	@Column(nullable = false)
	private BigInteger quantity;

	@Column(nullable = false)
	private BigDecimal amount;

	public OrderLineDTO toDTO() {
		OrderLineDTO orderLineDTO = new OrderLineDTO();
		orderLineDTO.setAmount(amount);
		orderLineDTO.setQuantity(quantity);
		orderLineDTO.setProduct(product.toDTO());
		return orderLineDTO;
	}
}
