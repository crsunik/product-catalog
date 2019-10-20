package com.jchmiel.roche.product;

import com.jchmiel.roche.product.dto.CreateProductDTO;
import com.jchmiel.roche.product.dto.ProductDTO;
import com.jchmiel.roche.product.exception.ProductNotFoundException;
import com.jchmiel.roche.product.exception.ProductSkuNotMatched;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
class ProductFacade {

	private final ProductCreator productCreator;
	private final ProductRepository productRepository;

	@Transactional
	ProductDTO create(CreateProductDTO createProductDTO) {
		Product product = productCreator.create(createProductDTO);
		return productRepository.save(product).toDTO();
	}

	Set<ProductDTO> findAll() {
		return Product.toDTO(productRepository.findAllByStatus(ProductStatus.ACTIVE));
	}

	public ProductDTO find(String sku) {
		return findActiveProduct(sku).toDTO();
	}

	@Transactional
	void update(String sku, ProductDTO updateProductDTO) {
		if (!StringUtils.equals(sku, updateProductDTO.getSku())) {
			throw new ProductSkuNotMatched();
		}
		Product product = findActiveProduct(sku);
		product.setPrice(updateProductDTO.getPrice());
		product.setName(updateProductDTO.getName());
		productRepository.save(product);
	}

	@Transactional
	void delete(String sku) {
		Product product = findActiveProduct(sku);
		product.setStatus(ProductStatus.DELETED);
		productRepository.save(product);
	}

	private Product findActiveProduct(String sku) {
		return productRepository.findBySkuAndStatus(sku, ProductStatus.ACTIVE).orElseThrow(ProductNotFoundException::new);
	}
}
