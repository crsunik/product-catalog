package com.jchmiel.roche.order

import com.jchmiel.roche.order.dto.OrderDTO
import com.jchmiel.roche.order.dto.PlaceOrderDTO
import com.jchmiel.roche.order.exception.OrderNotFoundException
import spock.lang.Specification

import java.time.LocalDate

class OrderFacadeTest extends Specification {
    OrderCreator orderCreator
    OrderRepository orderRepository
    OrderFacade orderFacade

    def setup() {
        orderCreator = Mock()
        orderRepository = Mock()
        orderFacade = new OrderFacade(orderCreator, orderRepository)

    }

    def "should successfully create and save the order"() {
        given:
        def placeOrder = Mock(PlaceOrderDTO)
        def order = Mock(Order)
        def orderDto = Mock(OrderDTO)

        when:
        OrderDTO result = orderFacade.place(placeOrder)

        then:
        result == orderDto

        and:
        1 * orderCreator.createCreator(placeOrder) >> order
        1 * orderRepository.save(order) >> order
        1 * order.toDTO() >> orderDto
        0 * _
    }

    def "should successfully find Order and return its dto"() {
        given:
        def orderId = 1L
        def order = Mock(Order)
        def orderDto = Mock(OrderDTO)
        when:
        OrderDTO result = orderFacade.find(orderId)

        then:
        result == orderDto

        and:
        1 * orderRepository.findById(orderId) >> Optional.of(order)
        1 * order.toDTO() >> orderDto
        0 * _
    }

    def "should throw an OrderNotFoundException if there is no order for given id"() {
        given:
        def orderId = 1L

        when:
        orderFacade.find(orderId)

        then:
        thrown OrderNotFoundException

        and:
        1 * orderRepository.findById(orderId) >> Optional.empty()
    }

    def 'should successfully find and return orders'() {
        given:
        def from = LocalDate.now()
        def to = LocalDate.now()
        def order = Mock(Order)
        def orders = [order, order]
        def orderDto = Mock(OrderDTO)

        when:
        def results = orderFacade.findAllPlacedBetween(from, to)

        then:
        results.size() == 2

        and:
        1 * orderRepository.findByCreatedDateBetween(from, to) >> orders
        2 * order.toDTO() >> orderDto

        and:
        results[0] == orderDto
        results[1] == orderDto
    }
}
