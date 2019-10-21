package com.jchmiel.roche.product

import com.jchmiel.roche.product.dto.ProductDTO

import java.time.LocalDate

class ProductTestUtils {

    static final String PRODUCT_NAME = 'product_name'

    static final String PRODUCT_SKU = 'product_sku'

    static final BigDecimal PRODUCT_PRICE = 10.5

    static final LocalDate PRODUCT_CREATED_DATE = LocalDate.of(1990, 1, 1)

    static Product product() {
        product(PRODUCT_NAME, PRODUCT_SKU, PRODUCT_PRICE)
    }

    static Product product(String name, String sku, BigDecimal price) {
        new Product(name: name, sku: sku, price: price, status: ProductStatus.ACTIVE)
    }

    static ProductDTO productDTO() {
        productDTO(PRODUCT_NAME)
    }

    static ProductDTO productDTO(name) {
        productDTO(PRODUCT_SKU, name, PRODUCT_PRICE, PRODUCT_CREATED_DATE)
    }

    static ProductDTO productDTO(sku, name, price, createdDate) {
        new ProductDTO(sku: sku, name: name, price: price, createdDate: createdDate)
    }
}
