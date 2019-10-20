package com.jchmiel.roche.product

import com.jchmiel.roche.product.dto.ProductDTO

import java.time.LocalDate

class ProductTestUtils {

    static Product product(String name, String sku, BigDecimal price) {
        new Product(name: name, sku: sku, price: price)
    }

    static ProductDTO productDTO(name) {
        productDTO('sku', name, 101.10, LocalDate.of(1990, 1, 1))
    }

    static ProductDTO productDTO(sku, name, price, createdDate) {
        new ProductDTO(sku: sku, name: name, price: price, createdDate: createdDate)
    }
}
