package com.jchmiel.roche.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Product for order was not found")
public class ProductForOrderNotFound extends RuntimeException {
}