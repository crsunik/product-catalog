package com.jchmiel.roche.order.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import java.util.List;

@Data
public class PlaceOrderDTO {

	@NotEmpty
	@Valid
	private List<PlaceOrderLineDTO> orderLines;

	@NotEmpty
	private String buyerEmail;
}
