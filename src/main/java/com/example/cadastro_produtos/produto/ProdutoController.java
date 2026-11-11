package com.example.cadastro_produtos.produto;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

	private final ProdutoRepository produtoRepository;

	public ProdutoController(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("produtos", produtoRepository.findAll());
		return "produtos/lista";
	}

	@GetMapping("/novo")
	public String novo(Model model) {
		model.addAttribute("produto", new Produto());
		model.addAttribute("titulo", "Cadastrar produto");
		return "produtos/formulario";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable String id, Model model) {
		model.addAttribute("produto", buscarProduto(id));
		model.addAttribute("titulo", "Editar produto");
		return "produtos/formulario";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid @ModelAttribute("produto") Produto produto, BindingResult resultado, Model model,
			RedirectAttributes redirectAttributes) {
		if (resultado.hasErrors()) {
			model.addAttribute("titulo", produto.getId() == null ? "Cadastrar produto" : "Editar produto");
			return "produtos/formulario";
		}

		boolean novoProduto = produto.getId() == null || produto.getId().isBlank();
		if (novoProduto) {
			produto.setId(null);
		} else if (!produtoRepository.existsById(produto.getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado.");
		}

		produtoRepository.save(produto);
		redirectAttributes.addFlashAttribute("sucesso",
				novoProduto ? "Produto cadastrado com sucesso." : "Produto atualizado com sucesso.");
		return "redirect:/produtos";
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable String id, RedirectAttributes redirectAttributes) {
		Produto produto = buscarProduto(id);
		produtoRepository.delete(produto);
		redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso.");
		return "redirect:/produtos";
	}

	private Produto buscarProduto(String id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
	}
}
