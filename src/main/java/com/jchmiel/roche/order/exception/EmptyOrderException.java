package com.jchmiel.roche.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Order cannot be empty")
public class EmptyOrderException extends RuntimeException {
}
