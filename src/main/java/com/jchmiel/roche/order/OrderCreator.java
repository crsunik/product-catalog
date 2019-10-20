package com.jchmiel.roche.order;

import com.jchmiel.roche.order.dto.PlaceOrderDTO;
import com.jchmiel.roche.order.dto.PlaceOrderLineDTO;
import com.jchmiel.roche.order.exception.EmptyOrderException;
import com.jchmiel.roche.order.exception.ProductForOrderNotFound;
import com.jchmiel.roche.product.Product;
import com.jchmiel.roche.product.ProductRepository;
import com.jchmiel.roche.product.ProductStatus;
import com.jchmiel.roche.util.CurrentDateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderCreator {

	private final ProductRepository productRepository;
	private final PriceCalculator priceCalculator;
	private final CurrentDateProvider currentDateProvider;

	public Order createCreator(PlaceOrderDTO placeOrder) {
		if (placeOrder.getOrderLines() == null || placeOrder.getOrderLines().isEmpty()) {
			throw new EmptyOrderException();
		}
		Order order = new Order();
		List<OrderLine> orderLines
				= placeOrder.getOrderLines()
				.stream()
				.map(line -> createOrderLine(line, order))
				.collect(Collectors.toList());
		order.setBuyerEmail(placeOrder.getBuyerEmail());
		order.setCreatedDate(currentDateProvider.currentDate());
		order.setTotalAmount(priceCalculator.calculateTotalAmount(orderLines));
		order.setOrderLines(orderLines);
		return order;
	}

	private OrderLine createOrderLine(PlaceOrderLineDTO lineDTO, Order order) {
		Product product
				= productRepository.findBySkuAndStatus(lineDTO.getProductSku(), ProductStatus.ACTIVE)
				.orElseThrow(ProductForOrderNotFound::new);
		OrderLine line = new OrderLine();
		line.setOrder(order);
		line.setProduct(product);
		line.setQuantity(lineDTO.getQuantity());
		line.setAmount(priceCalculator.calculateLineAmount(product, lineDTO.getQuantity()));
		return line;
	}
}
