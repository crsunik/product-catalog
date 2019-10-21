package com.jchmiel.roche.product;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class ProductSkuProvider {

	String provide() {
		return RandomStringUtils.random(8, false, true);
	}
}
