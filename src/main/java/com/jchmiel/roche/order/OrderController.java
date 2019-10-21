package com.jchmiel.roche.order;

import com.jchmiel.roche.order.dto.OrderDTO;
import com.jchmiel.roche.order.dto.PlaceOrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(OrderController.ORDERS_V1_URL)
public class OrderController {

	public static final String ORDERS_V1_URL = "/api/v1/orders";

	private final OrderFacade orderFacade;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	@ResponseBody
	public OrderDTO place(
			@RequestBody
			@Valid PlaceOrderDTO placeOrderDTO) {
		return orderFacade.place(placeOrderDTO);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{id}")
	public OrderDTO find(@PathVariable Long id) {
		return orderFacade.find(id);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	public List<OrderDTO> find(@RequestParam("from") LocalDate fromDate, @RequestParam("to") LocalDate toDate) {
		return orderFacade.findAllPlacedBetween(fromDate, toDate);
	}
}
