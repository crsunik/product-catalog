package com.jchmiel.roche.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.jchmiel.roche.product.dto.CreateProductDTO
import com.jchmiel.roche.product.exception.ProductNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static com.jchmiel.roche.product.ProductTestUtils.productDTO
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ProductController])
class ProductControllerTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    ProductFacade productFacade

    def 'should return empty set of products'() {
        when:
        def results = mockMvc.perform(get(ProductController.PRODUCTS_V1_URL))

        then:
        results.andExpect(status().isOk())

        and:
        1 * productFacade.findAll() >> []
        0 * _

        and:
        results.andExpect(jsonPath('$', hasSize(0)))
    }

    def 'should return set of all (two in this scenario) products'() {
        given:
        def products = [productDTO('productName1'), productDTO('productName2')]


        when:
        def results = mockMvc.perform(get(ProductController.PRODUCTS_V1_URL))

        then:
        results.andExpect(status().isOk())

        and:
        1 * productFacade.findAll() >> products
        0 * _

        and:
        results.andExpect(jsonPath('$', hasSize(2)))

        and:
        results.andExpect(jsonPath('$[0].name', equalTo(products[0].name)))
        results.andExpect(jsonPath('$[0].sku', equalTo(products[0].sku)))
        results.andExpect(jsonPath('$[0].price', equalTo(products[0].price.doubleValue())))
        results.andExpect(jsonPath('$[0].createdDate', equalTo(products[0].createdDate.format("dd-MM-yyyy"))))

        and:
        results.andExpect(jsonPath('$[1].name', equalTo(products[1].name)))
        results.andExpect(jsonPath('$[1].sku', equalTo(products[1].sku)))
        results.andExpect(jsonPath('$[1].price', equalTo(products[1].price.doubleValue())))
        results.andExpect(jsonPath('$[1].createdDate', equalTo(products[1].createdDate.format("dd-MM-yyyy"))))
    }

    def 'should create new product entry in storage'() {
        given:
        def productDTO = productDTO('productName')
        def createProductDTO = new CreateProductDTO(name: 'productName', price: 10.0)
        def createProductDTOContent = objectMapper.writeValueAsBytes(createProductDTO)

        when:
        def results = mockMvc.perform(post(ProductController.PRODUCTS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductDTOContent))

        then:
        results.andExpect(status().isCreated())

        and:
        1 * productFacade.create(createProductDTO) >> productDTO
        0 * _

        and:
        results.andExpect(jsonPath('$.sku', equalTo(productDTO.sku)))
        results.andExpect(jsonPath('$.name', equalTo(productDTO.name)))
        results.andExpect(jsonPath('$.price', equalTo(productDTO.price.doubleValue())))
        results.andExpect(jsonPath('$.createdDate', equalTo(productDTO.createdDate.format("dd-MM-yyyy"))))
    }

    def 'should find product by SKU'() {
        given:
        def productDTO = productDTO('product')

        when:
        def results = mockMvc.perform(get(ProductController.PRODUCTS_V1_URL + '/{sku}', productDTO.sku))

        then:
        results.andExpect(status().isOk())

        and:
        1 * productFacade.find(productDTO.sku) >> productDTO
        0 * _

        and:
        results.andExpect(jsonPath('$.sku', equalTo(productDTO.sku)))
        results.andExpect(jsonPath('$.name', equalTo(productDTO.name)))
        results.andExpect(jsonPath('$.price', equalTo(productDTO.price.doubleValue())))
        results.andExpect(jsonPath('$.createdDate', equalTo(productDTO.createdDate.format("dd-MM-yyyy"))))
    }

    def 'should return status 404 if no product was found for given SKU'() {
        given:
        def productDTO = productDTO('product')

        when:
        def results = mockMvc.perform(get(ProductController.PRODUCTS_V1_URL + '/{sku}', productDTO.sku))

        then:
        results.andExpect(status().isNotFound())

        and:
        1 * productFacade.find(_ as String) >> { String sku ->
            assert sku == productDTO.sku
            throw new ProductNotFoundException()
        }
    }

    def 'should return status 204 if product was successfully updated'() {
        given:
        def productDTO = productDTO('updateProduct')

        when:
        def results = mockMvc.perform(put(ProductController.PRODUCTS_V1_URL + '/{sku}', productDTO.sku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(productDTO)))

        then:
        results.andExpect(status().isNoContent())

        and:
        1 * productFacade.update(productDTO.sku, productDTO)
        0 * _
    }

    def 'should return status 404 if product was not found and thus not updated'() {
        given:
        def productDTO = productDTO('updateProduct')

        when:
        def results = mockMvc.perform(put(ProductController.PRODUCTS_V1_URL + '/{sku}', productDTO.sku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(productDTO)))

        then:
        results.andExpect(status().isNotFound())

        and:
        1 * productFacade.update(productDTO.sku, productDTO) >> { throw new ProductNotFoundException() }
        0 * _
    }

    def 'should return status 204 if product was successfully deleted'() {
        given:
        def sku = 'sku'

        when:
        def results = mockMvc.perform(delete(ProductController.PRODUCTS_V1_URL + '/{sku}', sku))

        then:
        results.andExpect(status().isNoContent())

        and:
        1 * productFacade.delete(sku)
        0 * _
    }

    def 'should return status 404 if product was not found and thus not deleted'() {
        given:
        def sku = 'sku'

        when:
        def results = mockMvc.perform(delete(ProductController.PRODUCTS_V1_URL + '/{sku}', sku))

        then:
        results.andExpect(status().isNotFound())

        and:
        1 * productFacade.delete(_ as String) >> { String s ->
            assert s == sku
            throw new ProductNotFoundException()
        }
        0 * _
    }

    @TestConfiguration
    static class FacadeMockConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        ProductFacade productFacade() {
            return detachedMockFactory.Mock(ProductFacade)
        }
    }

}