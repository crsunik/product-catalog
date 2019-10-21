package com.jchmiel.roche.product

import com.jchmiel.roche.product.dto.CreateProductDTO
import com.jchmiel.roche.util.CurrentDateProvider
import spock.lang.Specification

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
        def createProductDto = new CreateProductDTO(name: ProductTestUtils.PRODUCT_NAME, price: ProductTestUtils.PRODUCT_PRICE)

        when:
        Product result = productCreator.create(createProductDto)

        then:
        result.sku == ProductTestUtils.PRODUCT_SKU
        result.name == createProductDto.name
        result.price == createProductDto.price
        result.status == ProductStatus.ACTIVE
        result.createdDate == ProductTestUtils.PRODUCT_CREATED_DATE

        and:
        1 * currentDateProvider.currentDate() >> ProductTestUtils.PRODUCT_CREATED_DATE
        1 * productSkuProvider.provide() >> ProductTestUtils.PRODUCT_SKU
        0 * _
    }
}
