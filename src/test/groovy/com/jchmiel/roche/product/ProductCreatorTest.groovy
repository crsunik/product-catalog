package com.jchmiel.roche.product

import com.jchmiel.roche.product.dto.CreateProductDTO
import com.jchmiel.roche.util.CurrentDateProvider
import spock.lang.Specification

import java.time.LocalDate

class ProductCreatorTest extends Specification {
    ProductSkuProvider productSkuProvider
    CurrentDateProvider currentDateProvider
    ProductCreator productCreator

    def setup() {
        productSkuProvider = Mock()
        currentDateProvider = Mock()
        productCreator = new ProductCreator(productSkuProvider, currentDateProvider)
    }

    def "Product should be created"() {
        given:
        def createProductDto = new CreateProductDTO(name: 'productName', price: 10.0)
        def currentDate = LocalDate.of(1990, 1, 1)
        def sku = 'sampleSku'

        when:
        Product result = productCreator.create(createProductDto)

        then:
        result.sku == sku
        result.name == createProductDto.name
        result.price == createProductDto.price
        result.status == ProductStatus.ACTIVE
        result.createdDate == currentDate

        and:
        1 * currentDateProvider.currentDate() >> currentDate
        1 * productSkuProvider.provide(createProductDto.name) >> sku
        0 * _
    }
}
