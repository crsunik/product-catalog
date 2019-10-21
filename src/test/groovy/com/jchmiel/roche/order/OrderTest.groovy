package com.jchmiel.roche.order

import com.jchmiel.roche.order.dto.OrderDTO
import spock.lang.Specification

import java.time.LocalDate

import static com.jchmiel.roche.order.OrderTestUtils.orderLine

class OrderTest extends Specification {

    def 'should properly convert to DTO'() {
        given:
        def orderLines = [orderLine('sku1', 10, 5), orderLine('sku2', 50, 1)]
        def order = new Order(createdDate: LocalDate.now(), totalAmount: 100.0, buyerEmail: 'buyer@email', orderLines: orderLines)

        when:
        OrderDTO orderDTO = order.toDTO()

        then:
        orderDTO.createdDate == order.createdDate
        orderDTO.totalAmount == order.totalAmount
        orderDTO.buyerEmail == order.buyerEmail
        orderDTO.orderLines.size() == order.orderLines.size()
        orderDTO.orderLines[0].product.sku == order.orderLines[0].product.sku
        orderDTO.orderLines[0].quantity == order.orderLines[0].quantity
        orderDTO.orderLines[0].amount == order.orderLines[0].amount
        orderDTO.orderLines[1].product.sku == order.orderLines[1].product.sku
        orderDTO.orderLines[1].quantity == order.orderLines[1].quantity
        orderDTO.orderLines[1].amount == order.orderLines[1].amount
    }
}
