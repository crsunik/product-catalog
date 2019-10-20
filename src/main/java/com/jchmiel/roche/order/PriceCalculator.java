package com.jchmiel.roche.order;

import com.jchmiel.roche.product.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public class PriceCalculator {

	public BigDecimal calculateTotalAmount(List<OrderLine> orderLines) {
		return orderLines.stream().map(OrderLine::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	public BigDecimal calculateLineAmount(Product product, BigInteger quantity) {
		return product.getPrice().multiply(new BigDecimal(quantity));
	}
}
