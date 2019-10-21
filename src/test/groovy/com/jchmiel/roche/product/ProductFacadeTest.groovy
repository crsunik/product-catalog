package com.jchmiel.roche.product

import com.jchmiel.roche.product.dto.CreateProductDTO
import com.jchmiel.roche.product.dto.ProductDTO
import com.jchmiel.roche.product.exception.ProductNotFoundException
import com.jchmiel.roche.product.exception.ProductSkuNotMatched
import spock.lang.Specification

import java.time.LocalDate

class ProductFacadeTest extends Specification {
    ProductCreator productCreator
    ProductRepository productRepository
    ProductFacade productFacade

    def setup() {
        productCreator = Mock()
        productRepository = Mock()
        productFacade = new ProductFacade(productCreator, productRepository)
    }

    def "should create product"() {
        given:
        def createProductDto = Mock(CreateProductDTO)
        def product = Mock(Product)
        def productDto = Mock(ProductDTO)

        when:
        ProductDTO result = productFacade.create(createProductDto)

        then:
        result == productDto

        and:
        1 * productCreator.create(createProductDto) >> product
        1 * productRepository.save(product) >> product
        1 * product.toDTO() >> productDto
        0 * _
    }

    def "should return set of products"() {
        given:
        def product0 = Mock(Product)
        def product1 = Mock(Product)
        def productDto0 = Mock(ProductDTO)
        def productDto1 = Mock(ProductDTO)

        when:
        Set<ProductDTO> result = productFacade.findAll()

        then:
        result.size() == 2
        result.contains(productDto0)
        result.contains(productDto1)

        and:
        1 * productRepository.findAllByStatus(ProductStatus.ACTIVE) >> [product0, product1]
        1 * product0.toDTO() >> productDto0
        1 * product1.toDTO() >> productDto1
        1 * product0.hashCode()
        1 * product1.hashCode()
        0 * _
    }

    def "should return empty set of products"() {

        when:
        Set<ProductDTO> result = productFacade.findAll()

        then:
        result.isEmpty()

        and:
        1 * productRepository.findAllByStatus(ProductStatus.ACTIVE) >> []
        0 * _
    }

    def "should return product for given sku"() {
        given:
        def sku = ProductTestUtils.PRODUCT_SKU
        def product = Mock(Product)
        def productDto = Mock(ProductDTO)

        when:
        ProductDTO result = productFacade.find(sku)

        then:
        result == productDto

        and:
        1 * productRepository.findBySkuAndStatus(sku, ProductStatus.ACTIVE) >> Optional.of(product)
        1 * product.toDTO() >> productDto
        0 * _
    }

    def "exception ProductNotFoundException should be thrown if no product found"() {
        given:
        def sku = ProductTestUtils.PRODUCT_SKU

        when:
        productFacade.find(sku)

        then:
        thrown ProductNotFoundException

        and:
        1 * productRepository.findBySkuAndStatus(sku, ProductStatus.ACTIVE) >> Optional.empty()
        0 * _
    }

    def "should update product with given sku"() {
        given:
        def sku = ProductTestUtils.PRODUCT_SKU
        def product = Mock(Product)
        def productDto = new ProductDTO(sku: sku, name: 'newName', price: 101.0, createdDate: LocalDate.now())

        when:
        productFacade.update(sku, productDto)

        then:
        1 * productRepository.findBySkuAndStatus(sku, ProductStatus.ACTIVE) >> Optional.of(product)
        1 * product.setPrice(productDto.price)
        1 * product.setName(productDto.name)
        1 * productRepository.save(product)
        0 * _
    }

    def "exception ProductNotFoundException should be thrown if no product found and thus not update"() {
        given:
        def sku = ProductTestUtils.PRODUCT_SKU
        def productDto = new ProductDTO(sku: sku, name: 'newName', price: 101.0, createdDate: LocalDate.now())

        when:
        productFacade.update(sku, productDto)

        then:
        thrown ProductNotFoundException

        and:
        1 * productRepository.findBySkuAndStatus(sku, ProductStatus.ACTIVE) >> Optional.empty()
        0 * _
    }

    def "exception ProductSkuNotMatched should be thrown if path does not match product sku and thus not update"() {
        given:
        def sku = 'sku1'
        def productDto = new ProductDTO(sku: 'sku2', name: 'newName', price: 101.0, createdDate: LocalDate.now())

        when:
        productFacade.update(sku, productDto)

        then:
        thrown ProductSkuNotMatched

        and:
        0 * _
    }

    def "should delete product with given sku"() {
        given:
        def sku = ProductTestUtils.PRODUCT_SKU
        def product = Mock(Product)

        when:
        productFacade.delete(sku)

        then:
        1 * productRepository.findBySkuAndStatus(sku, ProductStatus.ACTIVE) >> Optional.of(product)
        1 * product.setStatus(ProductStatus.DELETED)
        1 * productRepository.save(product)
        0 * _
    }

    def "exception ProductNotFoundException should be thrown if no product found and thus not delete"() {
        given:
        def sku = ProductTestUtils.PRODUCT_SKU

        when:
        productFacade.delete(sku)

        then:
        thrown ProductNotFoundException

        and:
        1 * productRepository.findBySkuAndStatus(sku, ProductStatus.ACTIVE) >> Optional.empty()
        0 * _
    }
}