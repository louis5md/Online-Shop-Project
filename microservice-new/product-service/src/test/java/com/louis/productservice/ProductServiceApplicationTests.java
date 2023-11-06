package com.louis.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.louis.productservice.dto.ProductRequest;
import com.louis.productservice.dto.ProductResponse;
import com.louis.productservice.model.Product;
import com.louis.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.2");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}
	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
					.contentType(MediaType.APPLICATION_JSON)
					.content(productRequestString))
				.andExpect(status().isCreated());
        //Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetProduct() throws Exception {
		Product product = getFakeProduct();
		ProductResponse productResponse = getProductResponse();
		List<ProductResponse> listProducts = List.of(productResponse);
		String productResponseString = objectMapper.writeValueAsString(listProducts);
		productRepository.deleteAll();
		productRepository.save(product);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(content().string(productResponseString))
				.andExpect(status().isOk());
	}


	private Product getFakeProduct() {
		return Product.builder()
				.id("1234")
				.name("Iphone15")
				.description("Mobile phone , Iphone")
				.price(BigDecimal.valueOf(10000))
				.build();
	}

	private ProductResponse getProductResponse() {
		return ProductResponse.builder()
				.id("1234")
				.name("Iphone15")
				.description("Mobile phone , Iphone")
				.price(BigDecimal.valueOf(10000))
				.build();
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Iphone15")
				.description("Mobile phone , Iphone")
				.price(BigDecimal.valueOf(10000))
				.build();
	}


}
