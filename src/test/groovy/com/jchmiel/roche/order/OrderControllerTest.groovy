package com.jchmiel.roche.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.jchmiel.roche.order.dto.OrderDTO
import com.jchmiel.roche.order.dto.OrderLineDTO
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

import static com.jchmiel.roche.order.OrderTestUtils.toOrderLines
import static com.jchmiel.roche.product.ProductTestUtils.productDTO
import static java.time.LocalDate.of
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
        def orderCreatedDate = of(1990, 1, 1)
        def orderTotalAmount = 15
        def orderId = 1

        when:
        def results = mockMvc.perform(MockMvcRequestBuilders.post('/api/v1/orders')
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
        def results = mockMvc.perform(MockMvcRequestBuilders.post('/api/v1/orders')
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
        def orderDto =
                new OrderDTO(
                        id: 1L,
                        totalAmount: 10.0,
                        createdDate: of(1999, 1, 1),
                        buyerEmail: 'test@buyer',
                        orderLines: [new OrderLineDTO(
                                quantity: 10,
                                amount: 100.0,
                                product: productDTO('sku', 'name', 10.0, of(1990, 1, 1))
                        )]
                )

        when:
        def results = mockMvc.perform(get('/api/v1/orders/{id}', orderDto.id))

        then:
        results.andExpect(status().isOk())

        and:
        1 * orderFacade.find(orderDto.id) >> orderDto
        0 * _

        and:
        results.andExpect(jsonPath('$.id', equalTo(orderDto.id.intValue())))
        results.andExpect(jsonPath('$.totalAmount', equalTo(orderDto.totalAmount.doubleValue())))
        results.andExpect(jsonPath('$.createdDate', equalTo(orderDto.createdDate.format("dd-MM-yyyy"))))
        results.andExpect(jsonPath('$.buyerEmail', equalTo(orderDto.buyerEmail)))
        results.andExpect(jsonPath('$.orderLines', hasSize(1)))
        results.andExpect(jsonPath('$.orderLines[0].quantity', equalTo(orderDto.orderLines[0].quantity.intValue())))
        results.andExpect(jsonPath('$.orderLines[0].amount', equalTo(orderDto.orderLines[0].amount.doubleValue())))
        results.andExpect(jsonPath('$.orderLines[0].product.sku', equalTo(orderDto.orderLines[0].product.sku)))
        results.andExpect(jsonPath('$.orderLines[0].product.name', equalTo(orderDto.orderLines[0].product.name)))
        results.andExpect(jsonPath('$.orderLines[0].product.name', equalTo(orderDto.orderLines[0].product.name)))
        results.andExpect(jsonPath('$.orderLines[0].product.price', equalTo(orderDto.orderLines[0].product.price.doubleValue())))
        results.andExpect(jsonPath('$.orderLines[0].product.createdDate', equalTo(orderDto.orderLines[0].product.createdDate.format("dd-MM-yyyy"))))
    }

    def 'should receive 404 if order with given id wasnt found'() {
        when:
        def results = mockMvc.perform(get('/api/v1/orders/{id}', 1L))

        then:
        results.andExpect(status().isNotFound())

        and:
        1 * orderFacade.find(1L) >> { throw new OrderNotFoundException() }
        0 * _
    }

    def 'should return empty list of order'() {
        given:
        def from = LocalDate.now()
        def to = LocalDate.now()

        when:
        def results = mockMvc.perform(get('/api/v1/orders')
                .param('from', from.format('dd-MM-yyyy'))
                .param('to', to.format('dd-MM-yyyy'))

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
        def orderDto =
                new OrderDTO(
                        id: 1L,
                        totalAmount: 10.0,
                        createdDate: of(1999, 1, 1),
                        buyerEmail: 'test@buyer',
                        orderLines: [new OrderLineDTO(
                                quantity: 10,
                                amount: 100.0,
                                product: productDTO('sku', 'name', 10.0, of(1990, 1, 1))
                        )]
                )
        def orderDtos = [orderDto, orderDto]
        def from = LocalDate.now()
        def to = LocalDate.now()

        when:
        def results = mockMvc.perform(get('/api/v1/orders')
                .param('from', from.format('dd-MM-yyyy'))
                .param('to', to.format('dd-MM-yyyy'))
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
        results.andExpect(jsonPath('$[0].createdDate', equalTo(orderDto.createdDate.format("dd-MM-yyyy"))))
        results.andExpect(jsonPath('$[0].buyerEmail', equalTo(orderDto.buyerEmail)))
        results.andExpect(jsonPath('$[0].orderLines', hasSize(1)))
        results.andExpect(jsonPath('$[1].id', equalTo(orderDto.id.intValue())))
        results.andExpect(jsonPath('$[1].totalAmount', equalTo(orderDto.totalAmount.doubleValue())))
        results.andExpect(jsonPath('$[1].createdDate', equalTo(orderDto.createdDate.format("dd-MM-yyyy"))))
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

