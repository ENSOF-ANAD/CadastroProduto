package com.example.cadastro_produtos.produto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.server.ResponseStatusException;

class ProdutoControllerTests {

	private ProdutoRepository produtoRepository;
	private ProdutoController controller;

	@BeforeEach
	void setUp() {
		produtoRepository = mock(ProdutoRepository.class);
		controller = new ProdutoController(produtoRepository);
	}

	@Test
	void listaProdutos() {
		Produto produto = produtoValido();
		when(produtoRepository.findAll()).thenReturn(List.of(produto));
		ExtendedModelMap model = new ExtendedModelMap();

		String view = controller.listar(model);

		assertEquals("produtos/lista", view);
		assertEquals(List.of(produto), model.get("produtos"));
	}

	@Test
	void salvaProdutoValido() {
		Produto produto = produtoValido();
		BeanPropertyBindingResult resultado = new BeanPropertyBindingResult(produto, "produto");
		RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

		String view = controller.salvar(produto, resultado, new ExtendedModelMap(), redirect);

		assertEquals("redirect:/produtos", view);
		assertEquals("Produto cadastrado com sucesso.", redirect.getFlashAttributes().get("sucesso"));
		verify(produtoRepository).save(produto);
	}

	@Test
	void atualizaProdutoExistente() {
		Produto produto = produtoValido();
		produto.setId("produto-1");
		when(produtoRepository.existsById("produto-1")).thenReturn(true);
		RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

		String view = controller.salvar(produto, new BeanPropertyBindingResult(produto, "produto"),
				new ExtendedModelMap(), redirect);

		assertEquals("redirect:/produtos", view);
		assertEquals("Produto atualizado com sucesso.", redirect.getFlashAttributes().get("sucesso"));
		verify(produtoRepository).save(produto);
	}

	@Test
	void rejeitaAtualizacaoDeProdutoInexistente() {
		Produto produto = produtoValido();
		produto.setId("inexistente");
		when(produtoRepository.existsById("inexistente")).thenReturn(false);

		assertThrows(ResponseStatusException.class,
				() -> controller.salvar(produto, new BeanPropertyBindingResult(produto, "produto"),
						new ExtendedModelMap(), new RedirectAttributesModelMap()));
	}

	@Test
	void naoSalvaProdutoComErroDeValidacao() {
		Produto produto = produtoValido();
		BeanPropertyBindingResult resultado = new BeanPropertyBindingResult(produto, "produto");
		resultado.rejectValue("nome", "NotBlank", "Informe o nome do produto.");
		ExtendedModelMap model = new ExtendedModelMap();

		String view = controller.salvar(produto, resultado, model, new RedirectAttributesModelMap());

		assertEquals("produtos/formulario", view);
		assertEquals("Cadastrar produto", model.get("titulo"));
	}

	@Test
	void excluiProdutoExistente() {
		Produto produto = produtoValido();
		produto.setId("produto-1");
		when(produtoRepository.findById("produto-1")).thenReturn(Optional.of(produto));
		RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

		String view = controller.excluir("produto-1", redirect);

		assertEquals("redirect:/produtos", view);
		assertEquals("Produto excluído com sucesso.", redirect.getFlashAttributes().get("sucesso"));
		verify(produtoRepository).delete(produto);
	}

	private Produto produtoValido() {
		Produto produto = new Produto();
		produto.setNome("Teclado");
		produto.setDescricao("Teclado mecânico");
		produto.setCategoria("Periféricos");
		produto.setPreco(new BigDecimal("199.90"));
		produto.setEstoque(5);
		return produto;
	}
}
