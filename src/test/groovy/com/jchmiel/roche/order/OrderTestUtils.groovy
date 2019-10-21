package com.jchmiel.roche.order

import com.jchmiel.roche.order.dto.OrderDTO
import com.jchmiel.roche.order.dto.OrderLineDTO
import com.jchmiel.roche.order.dto.PlaceOrderLineDTO
import com.jchmiel.roche.product.Product
import com.jchmiel.roche.product.dto.ProductDTO

import java.time.LocalDate

import static com.jchmiel.roche.product.ProductTestUtils.productDTO

class OrderTestUtils {

    static final LocalDate ORDER_CREATED_DATE = LocalDate.of(1999, 1, 1)


    static final String BUYER_EMAIL = 'buyer@email'

    static final BigDecimal ORDER_TOTAL_AMOUNT = 10.0

    static final long ORDER_ID = 1L

    static PlaceOrderLineDTO placeOrderLineDTO(productSku, quantity) {
        new PlaceOrderLineDTO(productSku: productSku, quantity: quantity)
    }

    static List<OrderLineDTO> toOrderLines(List<PlaceOrderLineDTO> placeOrderLineDTOS) {
        placeOrderLineDTOS.collect { toOrderLine(it) }
    }

    static OrderLineDTO toOrderLine(placeOrderLineDTO) {
        new OrderLineDTO(
                product: new ProductDTO(sku: placeOrderLineDTO.productSku),
                quantity: placeOrderLineDTO.quantity,
                amount: placeOrderLineDTO.quantity
        )
    }

    static OrderLine orderLine(sku, price, quantity) {
        new OrderLine(product: new Product(sku: sku, price: price), quantity: quantity, amount: price * new BigDecimal(quantity))
    }

    static Order order(orderLines) {
        new Order(createdDate: ORDER_CREATED_DATE, totalAmount: ORDER_TOTAL_AMOUNT, buyerEmail: BUYER_EMAIL, orderLines: orderLines)
    }

    static OrderDTO orderDTO(orderLines) {
        new OrderDTO(
                id: ORDER_ID,
                totalAmount: ORDER_TOTAL_AMOUNT,
                createdDate: ORDER_CREATED_DATE,
                buyerEmail: BUYER_EMAIL,
                orderLines: orderLines
        )
    }

    static OrderLineDTO orderLineDTO() {
        new OrderLineDTO(quantity: 10, amount: 100.0, product: productDTO())
    }


}
