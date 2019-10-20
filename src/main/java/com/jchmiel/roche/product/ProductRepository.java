package com.jchmiel.roche.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findBySkuAndStatus(String sku, ProductStatus status);

	Set<Product> findAllByStatus(ProductStatus status);
}
