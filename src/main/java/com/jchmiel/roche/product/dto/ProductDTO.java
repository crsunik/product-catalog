package com.jchmiel.roche.product.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductDTO {

	@NotEmpty
	private String sku;

	@NotEmpty
	private String name;

	@NotNull
	@Positive
	private BigDecimal price;

	@NotNull
	@PastOrPresent
	private LocalDate createdDate;
}
