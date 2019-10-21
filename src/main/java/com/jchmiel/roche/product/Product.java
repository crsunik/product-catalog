package com.jchmiel.roche.product;

import com.jchmiel.roche.product.dto.ProductDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Entity
@Audited
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 8, updatable = false)
	private String sku;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false, updatable = false)
	private LocalDate createdDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductStatus status;

	public ProductDTO toDTO() {
		ProductDTO dto = new ProductDTO();
		dto.setSku(sku);
		dto.setName(name);
		dto.setPrice(price);
		dto.setCreatedDate(createdDate);
		return dto;
	}

	static Set<ProductDTO> toDTO(Iterable<Product> productIterable) {
		return StreamSupport.stream(productIterable.spliterator(), false).map(Product::toDTO).collect(Collectors.toSet());
	}
}
