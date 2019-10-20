package com.jchmiel.roche.product;

import com.jchmiel.roche.product.dto.CreateProductDTO;
import com.jchmiel.roche.product.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductFacade productFacade;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping
	public Set<ProductDTO> findAll() {
		return productFacade.findAll();
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	@ResponseBody
	public ProductDTO create(
			@RequestBody
			@Valid CreateProductDTO createProductDTO) {
		return productFacade.create(createProductDTO);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{sku}")
	public ProductDTO find(@PathVariable String sku) {
		return productFacade.find(sku);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping("/{sku}")
	public void update(@PathVariable String sku,
			@RequestBody
			@Valid ProductDTO productDTO) {
		productFacade.update(sku, productDTO);
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{sku}")
	public void delete(@PathVariable String sku) {
		productFacade.delete(sku);
	}

}
