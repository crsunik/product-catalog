package com.jchmiel.roche.order.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.math.BigInteger;

@Data
public class PlaceOrderLineDTO {

	@NotEmpty
	private String productSku;

	@NotNull
	@Positive
	private BigInteger quantity;
}
