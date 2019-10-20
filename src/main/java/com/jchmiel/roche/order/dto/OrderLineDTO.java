package com.jchmiel.roche.order.dto;

import com.jchmiel.roche.product.dto.ProductDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class OrderLineDTO {

	@NotNull
	private ProductDTO product;

	@NotNull
	@Positive
	private BigInteger quantity;

	@NotNull
	@Positive
	private BigDecimal amount;
}
