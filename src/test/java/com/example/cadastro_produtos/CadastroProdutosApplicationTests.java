package com.example.cadastro_produtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.config.import=",
		"MONGODB_URI=mongodb://127.0.0.1:27017/cadastro_produtos_test"
})
class CadastroProdutosApplicationTests {

	@Autowired
	private MongoProperties mongoProperties;

	@Test
	void contextLoads() {
	}

	@Test
	void configuraMongoComUriDoAmbiente() {
		assertEquals("mongodb://127.0.0.1:27017/cadastro_produtos_test", mongoProperties.getUri());
	}

}
