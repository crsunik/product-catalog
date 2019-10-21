package com.jchmiel.roche.product

import com.jchmiel.roche.product.dto.ProductDTO
import spock.lang.Specification

class ProductTest extends Specification {
    def "should properly convert to DTO"() {
        given:
        def product = ProductTestUtils.product()

        when:
        ProductDTO result = product.toDTO()

        then:
        result.price == product.price
        result.name == product.name
        result.sku == product.sku
        result.createdDate == product.createdDate
    }

}
