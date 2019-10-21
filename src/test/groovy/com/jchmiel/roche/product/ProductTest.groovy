package com.jchmiel.roche.product

import com.jchmiel.roche.product.dto.ProductDTO
import spock.lang.Specification

import java.time.LocalDate

class ProductTest extends Specification {
    def "should properly convert to DTO"() {
        given:
        def product = new Product(id: 1L, name: 'name', sku: 'sku', price: 15.0, createdDate: LocalDate.now(), status: ProductStatus.ACTIVE)

        when:
        ProductDTO result = product.toDTO()

        then:
        result.price == product.price
        result.name == product.name
        result.sku == product.sku
        result.createdDate == product.createdDate
    }

}
