package com.jchmiel.roche.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Product sku not matched")
public class ProductSkuNotMatched extends RuntimeException {
}
