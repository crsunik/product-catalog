package com.jchmiel.roche.product.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
public class CreateProductDTO {

	@NotEmpty
	private String name;

	@NotNull
	@Positive
	private BigDecimal price;
}
