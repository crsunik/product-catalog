package com.jchmiel.roche.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.jchmiel.roche.CommonTestUtils
import com.jchmiel.roche.order.dto.OrderDTO
import com.jchmiel.roche.order.dto.PlaceOrderDTO
import com.jchmiel.roche.order.dto.PlaceOrderLineDTO
import com.jchmiel.roche.order.exception.OrderNotFoundException
import com.jchmiel.roche.order.exception.ProductForOrderNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import java.time.LocalDate

import static com.jchmiel.roche.order.OrderTestUtils.orderDTO
import static com.jchmiel.roche.order.OrderTestUtils.orderLineDTO
import static com.jchmiel.roche.order.OrderTestUtils.toOrderLines
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [OrderController])
class OrderControllerTest extends Specification {
    @Autowired
    OrderFacade orderFacade
    @Autowired
    MockMvc mockMvc
    @Autowired
    ObjectMapper objectMapper

    def 'should be able to place an order for existing products'() {
        given:
        def placeOrderLines = [new PlaceOrderLineDTO(productSku: 'productSku1', quantity: 10), new PlaceOrderLineDTO(productSku: 'productSku2', quantity: 5)]
        def placeOrder = new PlaceOrderDTO(orderLines: placeOrderLines, buyerEmail: 'test@buyer.com')
        def orderCreatedDate = LocalDate.of(1990, 1, 1)
        def orderTotalAmount = 15
        def orderId = 1

        when:
        def results = mockMvc.perform(MockMvcRequestBuilders.post(OrderController.ORDERS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(placeOrder)))

        then:
        results.andExpect(status().isCreated())

        and:
        1 * orderFacade.place(placeOrder) >> new OrderDTO(
                id: orderId,
                buyerEmail: placeOrder.buyerEmail,
                createdDate: orderCreatedDate,
                totalAmount: orderTotalAmount,
                orderLines: toOrderLines(placeOrderLines)
        )
        0 * _

        and:
        results.andExpect(jsonPath('$.buyerEmail', equalTo(placeOrder.buyerEmail)))
        results.andExpect(jsonPath('$.orderLines', hasSize(placeOrderLines.size())))
        results.andExpect(jsonPath('$.orderLines[0].product.sku', equalTo(placeOrderLines[0].productSku)))
        results.andExpect(jsonPath('$.orderLines[1].product.sku', equalTo(placeOrderLines[1].productSku)))
    }

    def 'should receive 409 during order placement if product is not available'() {
        given:
        def placeOrderLines = [new PlaceOrderLineDTO(productSku: 'productSku1', quantity: 10), new PlaceOrderLineDTO(productSku: 'productSku2', quantity: 5)]
        def placeOrder = new PlaceOrderDTO(orderLines: placeOrderLines, buyerEmail: 'test@buyer.com')

        when:
        def results = mockMvc.perform(MockMvcRequestBuilders.post(OrderController.ORDERS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(placeOrder)))

        then:
        results.andExpect(status().isConflict())

        and:
        1 * orderFacade.place(placeOrder) >> { throw new ProductForOrderNotFound() }
        0 * _
    }

    def 'should be able to retrieve an order based on its id'() {
        given:
        def orderDto = orderDTO([orderLineDTO()])

        when:
        def results = mockMvc.perform(get(OrderController.ORDERS_V1_URL + '/{id}', orderDto.id))

        then:
        results.andExpect(status().isOk())

        and:
        1 * orderFacade.find(orderDto.id) >> orderDto
        0 * _

        and:
        results.andExpect(jsonPath('$.id', equalTo(orderDto.id.intValue())))
        results.andExpect(jsonPath('$.totalAmount', equalTo(orderDto.totalAmount.doubleValue())))
        results.andExpect(jsonPath('$.createdDate', equalTo(orderDto.createdDate.format(CommonTestUtils.DATE_PATTERN))))
        results.andExpect(jsonPath('$.buyerEmail', equalTo(orderDto.buyerEmail)))
        results.andExpect(jsonPath('$.orderLines', hasSize(1)))
        results.andExpect(jsonPath('$.orderLines[0].quantity', equalTo(orderDto.orderLines[0].quantity.intValue())))
        results.andExpect(jsonPath('$.orderLines[0].amount', equalTo(orderDto.orderLines[0].amount.doubleValue())))
        results.andExpect(jsonPath('$.orderLines[0].product.sku', equalTo(orderDto.orderLines[0].product.sku)))
        results.andExpect(jsonPath('$.orderLines[0].product.name', equalTo(orderDto.orderLines[0].product.name)))
        results.andExpect(jsonPath('$.orderLines[0].product.name', equalTo(orderDto.orderLines[0].product.name)))
        results.andExpect(jsonPath('$.orderLines[0].product.price', equalTo(orderDto.orderLines[0].product.price.doubleValue())))
        results.andExpect(jsonPath('$.orderLines[0].product.createdDate', equalTo(orderDto.orderLines[0].product.createdDate.format(CommonTestUtils.DATE_PATTERN))))
    }

    def 'should receive 404 if order with given id wasnt found'() {
        when:
        def results = mockMvc.perform(get(OrderController.ORDERS_V1_URL + '/{id}', 1L))

        then:
        results.andExpect(status().isNotFound())

        and:
        1 * orderFacade.find(1L) >> { throw new OrderNotFoundException() }
        0 * _
    }

    def 'should return empty list LocalDate.of order'() {
        given:
        def from = LocalDate.now()
        def to = LocalDate.now()

        when:
        def results = mockMvc.perform(get(OrderController.ORDERS_V1_URL)
                .param('from', from.format(CommonTestUtils.DATE_PATTERN))
                .param('to', to.format(CommonTestUtils.DATE_PATTERN))

        )

        then:
        results.andExpect(status().isOk())

        and:
        1 * orderFacade.findAllPlacedBetween(from, to) >> []
        0 * _

        and:
        results.andExpect(jsonPath('$', hasSize(0)))

    }

    def 'should return all orders in the give period'() {
        given:
        def orderDto = orderDTO([orderLineDTO()])
        def orderDtos = [orderDto, orderDto]
        def from = LocalDate.now()
        def to = LocalDate.now()

        when:
        def results = mockMvc.perform(get(OrderController.ORDERS_V1_URL)
                .param('from', from.format(CommonTestUtils.DATE_PATTERN))
                .param('to', to.format(CommonTestUtils.DATE_PATTERN))
        )

        then:
        results.andExpect(status().isOk())

        and:
        1 * orderFacade.findAllPlacedBetween(from, to) >> orderDtos
        0 * _

        and:
        results.andExpect(jsonPath('$', hasSize(2)))
        results.andExpect(jsonPath('$[0].id', equalTo(orderDto.id.intValue())))
        results.andExpect(jsonPath('$[0].totalAmount', equalTo(orderDto.totalAmount.doubleValue())))
        results.andExpect(jsonPath('$[0].createdDate', equalTo(orderDto.createdDate.format(CommonTestUtils.DATE_PATTERN))))
        results.andExpect(jsonPath('$[0].buyerEmail', equalTo(orderDto.buyerEmail)))
        results.andExpect(jsonPath('$[0].orderLines', hasSize(1)))
        results.andExpect(jsonPath('$[1].id', equalTo(orderDto.id.intValue())))
        results.andExpect(jsonPath('$[1].totalAmount', equalTo(orderDto.totalAmount.doubleValue())))
        results.andExpect(jsonPath('$[1].createdDate', equalTo(orderDto.createdDate.format(CommonTestUtils.DATE_PATTERN))))
        results.andExpect(jsonPath('$[1].buyerEmail', equalTo(orderDto.buyerEmail)))
        results.andExpect(jsonPath('$[1].orderLines', hasSize(1)))
    }

    @TestConfiguration
    static class MockConfig {

        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        OrderFacade orderFacade() {
            return detachedMockFactory.Mock(OrderFacade)
        }

    }
}

