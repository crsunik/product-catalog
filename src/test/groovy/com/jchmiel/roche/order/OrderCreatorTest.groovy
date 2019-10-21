package com.jchmiel.roche.order

import com.jchmiel.roche.order.dto.PlaceOrderDTO
import com.jchmiel.roche.order.exception.EmptyOrderException
import com.jchmiel.roche.order.exception.ProductForOrderNotFound
import com.jchmiel.roche.product.ProductRepository
import com.jchmiel.roche.product.ProductStatus
import com.jchmiel.roche.product.ProductTestUtils
import com.jchmiel.roche.util.CurrentDateProvider
import spock.lang.Specification

import java.time.LocalDate

import static com.jchmiel.roche.order.OrderTestUtils.placeOrderLineDTO

class OrderCreatorTest extends Specification {


    ProductRepository productRepositoryMock
    PriceCalculator priceCalculatorMock
    CurrentDateProvider currentDateProviderMock

    OrderCreator orderCreator

    void setup() {
        productRepositoryMock = Mock()
        priceCalculatorMock = Mock()
        currentDateProviderMock = Mock()

        orderCreator = new OrderCreator(productRepositoryMock, priceCalculatorMock, currentDateProviderMock)
    }

    def "should create order"() {
        given:
        def orderLines = [placeOrderLineDTO("sku0", 10), placeOrderLineDTO("sku1", 20)]
        def placeOrderDto = new PlaceOrderDTO(buyerEmail: 'buyerEmail@test', orderLines: orderLines)
        def currentDate = LocalDate.of(1990, 1, 1)
        def product0 = ProductTestUtils.product(orderLines[0].productSku, orderLines[0].productSku, 10.0)
        def product1 = ProductTestUtils.product(orderLines[1].productSku, orderLines[1].productSku, 20.0)
        def totalAmount = 500.0
        def line0Amount = 100.0
        def line1Amount = 400.0

        when:
        Order result = orderCreator.createCreator(placeOrderDto)

        then:
        result.createdDate == currentDate
        result.buyerEmail == placeOrderDto.buyerEmail
        result.totalAmount == totalAmount
        result.orderLines[0].amount == line0Amount
        result.orderLines[0].quantity == orderLines[0].quantity
        result.orderLines[0].product == product0
        result.orderLines[0].order == result
        result.orderLines[1].amount == line1Amount
        result.orderLines[1].quantity == orderLines[1].quantity
        result.orderLines[1].product == product1
        result.orderLines[1].order == result

        and:
        1 * currentDateProviderMock.currentDate() >> currentDate
        1 * productRepositoryMock.findBySkuAndStatus(orderLines[0].productSku, ProductStatus.ACTIVE) >> Optional.of(product0)
        1 * productRepositoryMock.findBySkuAndStatus(orderLines[1].productSku, ProductStatus.ACTIVE) >> Optional.of(product1)
        1 * priceCalculatorMock.calculateLineAmount(product0, orderLines[0].quantity) >> line0Amount
        1 * priceCalculatorMock.calculateLineAmount(product1, orderLines[1].quantity) >> line1Amount
        1 * priceCalculatorMock.calculateTotalAmount(_) >> totalAmount
    }

    def "exception ProductNotFoundException should be thrown"() {
        given:
        def orderLines = [placeOrderLineDTO("sku0", 10), placeOrderLineDTO("sku1", 20)]
        def placeOrderDto = new PlaceOrderDTO(buyerEmail: 'buyerEmail@test', orderLines: orderLines)
        def product0 = ProductTestUtils.product(orderLines[0].productSku, orderLines[0].productSku, 10.0)
        def line0Amount = 100.0

        when:
        orderCreator.createCreator(placeOrderDto)

        then:
        thrown ProductForOrderNotFound

        and:
        1 * productRepositoryMock.findBySkuAndStatus(orderLines[0].productSku, ProductStatus.ACTIVE) >> Optional.of(product0)
        1 * productRepositoryMock.findBySkuAndStatus(orderLines[1].productSku, ProductStatus.ACTIVE) >> Optional.empty()
        1 * priceCalculatorMock.calculateLineAmount(product0, orderLines[0].quantity) >> line0Amount
    }

    def "should throw EmptyOrderException"() {
        given:
        def orderLines = []
        def placeOrderDto = new PlaceOrderDTO(buyerEmail: 'buyerEmail@test', orderLines: orderLines)

        when:
        orderCreator.createCreator(placeOrderDto)

        then:
        thrown EmptyOrderException
    }
}
