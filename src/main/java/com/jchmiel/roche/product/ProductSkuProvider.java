package com.jchmiel.roche.product;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductSkuProvider {

	public String provide(String name) {
		String sku = RandomStringUtils.random(8, false, true);
		log.debug("Sku {} was assigned to product {}", sku, name);
		return sku;
	}
}
