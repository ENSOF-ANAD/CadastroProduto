package com.example.cadastro_produtos.produto;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Document(collection = "produtos")
public class Produto {

	@Id
	private String id;

	@NotBlank(message = "Informe o nome do produto.")
	@Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
	private String nome;

	@Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
	private String descricao;

	@NotBlank(message = "Informe a categoria.")
	@Size(max = 60, message = "A categoria deve ter no máximo 60 caracteres.")
	private String categoria;

	@NotNull(message = "Informe o preço.")
	@DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
	private BigDecimal preco;

	@NotNull(message = "Informe a quantidade em estoque.")
	@PositiveOrZero(message = "O estoque não pode ser negativo.")
	private Integer estoque;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public Integer getEstoque() {
		return estoque;
	}

	public void setEstoque(Integer estoque) {
		this.estoque = estoque;
	}
}
