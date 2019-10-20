package com.jchmiel.roche.order.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {

	@NotNull
	private Long id;

	@NotEmpty
	@Valid
	private List<OrderLineDTO> orderLines;

	@NotNull
	@Positive
	private BigDecimal totalAmount;

	@NotNull
	@PastOrPresent
	private LocalDate createdDate;

	@NotEmpty
	private String buyerEmail;
}
