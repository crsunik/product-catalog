package com.jchmiel.roche.order

import com.jchmiel.roche.product.Product
import spock.lang.Specification
import spock.lang.Unroll

import static com.jchmiel.roche.order.OrderTestUtils.orderLine

class PriceCalculatorTest extends Specification {
    PriceCalculator priceCalculator = new PriceCalculator()

    @Unroll
    def "should correctly calculate order total amount"(orderLines, totalAmount) {

        when:
        BigDecimal result = priceCalculator.calculateTotalAmount(orderLines)

        then:
        result == totalAmount

        where:
        orderLines                               | totalAmount
        [orderLine(10.0, 1), orderLine(5.0, 5)]  | 35.0
        [orderLine(10.0, 3), orderLine(5.0, 5)]  | 55.0
        [orderLine(10.0, 1), orderLine(5.0, 10)] | 60.0
        [orderLine(10.0, 1), orderLine(5.0, 1)]  | 15.0
        []                                       | 0.0
    }

    @Unroll
    def "should correctly calculate order line amount"(productPrice, quantity, lineAmount) {
        given:
        def product = new Product(price: productPrice)

        when:
        BigDecimal result = priceCalculator.calculateLineAmount(product, quantity)

        then:
        result == lineAmount

        where:
        productPrice | quantity | lineAmount
        10.0         | 1        | 10.0
        13.5         | 2        | 27.0
        4.5          | 16       | 72.0
        100          | 0        | 0.0
    }
}
