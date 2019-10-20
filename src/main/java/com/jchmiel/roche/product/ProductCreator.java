package com.jchmiel.roche.product;

import com.jchmiel.roche.product.dto.CreateProductDTO;
import com.jchmiel.roche.util.CurrentDateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCreator {

	private final ProductSkuProvider productSkuProvider;
	private final CurrentDateProvider currentDateProvider;

	public Product create(CreateProductDTO createProductDTO) {
		Product product = new Product();
		product.setName(createProductDTO.getName());
		product.setPrice(createProductDTO.getPrice());
		product.setSku(productSkuProvider.provide(createProductDTO.getName()));
		product.setCreatedDate(currentDateProvider.currentDate());
		product.setStatus(ProductStatus.ACTIVE);
		return product;
	}
}
