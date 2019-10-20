package com.jchmiel.roche.order

import com.jchmiel.roche.order.dto.OrderLineDTO
import com.jchmiel.roche.order.dto.PlaceOrderLineDTO
import com.jchmiel.roche.product.Product
import com.jchmiel.roche.product.dto.ProductDTO

class OrderTestUtils {

    static PlaceOrderLineDTO orderLineDTO(String productSku, BigInteger quantity) {
        new PlaceOrderLineDTO(productSku: productSku, quantity: quantity)
    }

    static OrderLine orderLine(BigDecimal price, BigInteger quantity) {
        new OrderLine(product: new Product(price: price), quantity: quantity, amount: price * new BigDecimal(quantity))
    }

    static List<OrderLineDTO> toOrderLines(List<PlaceOrderLineDTO> placeOrderLineDTOS) {
        placeOrderLineDTOS.collect { toOrderLine(it) }
    }

    static OrderLineDTO toOrderLine(PlaceOrderLineDTO placeOrderLineDTO) {
        new OrderLineDTO(
                product: new ProductDTO(sku: placeOrderLineDTO.productSku),
                quantity: placeOrderLineDTO.quantity,
                amount: placeOrderLineDTO.quantity
        )
    }
}
