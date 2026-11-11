# Cadastro de Produtos — construindo do zero

### [UNICEPLAC Centro Universitário](https://www.uniceplac.edu.br/)

**Curso:** Engenharia de Software<br>
**Professor:** Profº Hudson Neves

Neste tutorial, você vai construir uma aplicação em Java para cadastrar, listar, editar e excluir produtos. Os dados ficam no MongoDB Atlas, e as telas são abertas no navegador.

O projeto se chama **cadastro-produtos**. Na interface, usamos o nome **Estoque Fácil**.

O roteiro foi escrito para quem está começando. É útil conhecer variáveis, classes e métodos em Java, além de algumas tags HTML. Os comandos usam **Windows e PowerShell**. Digite apenas o comando: não copie o texto `PS C:\...>` que aparece antes dele no terminal.

## Como usar este guia

Se você quer construir tudo, siga as etapas na ordem. Os arquivos Java e HTML aparecem completos; o CSS e os testes completos estão em blocos expansíveis.

Se você já recebeu o projeto pronto, faça as etapas **1, 4, 5 e 12** para preparar o Java, configurar o banco e executar. Não substitua um `.env` que já esteja configurado.

1. [Preparar o computador](#1-preparar-o-computador)
2. [Gerar o projeto](#2-gerar-o-projeto)
3. [Entender as pastas e dependências](#3-entender-as-pastas-e-dependências)
4. [Preparar o MongoDB Atlas](#4-preparar-o-mongodb-atlas)
5. [Conectar o projeto ao banco](#5-conectar-o-projeto-ao-banco)
6. [Criar a classe principal](#6-criar-a-classe-principal)
7. [Representar um produto](#7-representar-um-produto)
8. [Criar o acesso ao banco](#8-criar-o-acesso-ao-banco)
9. [Criar os controllers](#9-criar-os-controllers)
10. [Criar as telas](#10-criar-as-telas)
11. [Adicionar os estilos](#11-adicionar-os-estilos)
12. [Executar e experimentar](#12-executar-e-experimentar)
13. [Criar e executar os testes](#13-criar-e-executar-os-testes)
14. [Gerar o executável](#14-gerar-o-executável)
15. [Resolver problemas comuns](#15-resolver-problemas-comuns)
16. [Praticar com novos desafios](#16-praticar-com-novos-desafios)

## O que estamos construindo?

As quatro operações são conhecidas como **CRUD**:

| Operação | Significado | Exemplo na aplicação |
| --- | --- | --- |
| Create | Criar | Cadastrar um teclado |
| Read | Ler | Mostrar os produtos cadastrados |
| Update | Atualizar | Alterar o preço do teclado |
| Delete | Excluir | Remover um produto |

Cada ferramenta tem um papel:

| Ferramenta | Para que serve aqui |
| --- | --- |
| Java 17 | Linguagem e ambiente usados para executar o código |
| Spring Boot | Prepara e inicia a aplicação e seu servidor web |
| Spring MVC | Recebe os pedidos enviados pelo navegador |
| Thymeleaf | Preenche o HTML com os dados vindos do Java |
| Spring Data MongoDB | Facilita salvar e consultar os produtos |
| MongoDB Atlas | Hospeda o banco de dados na nuvem |
| Jakarta Validation | Verifica regras, como nome obrigatório e estoque não negativo |
| Gradle | Baixa dependências, compila, executa testes e monta o executável |
| HTML e CSS | Definem o conteúdo e a aparência das páginas |

Quando você abre a listagem, o caminho é este:

```mermaid
sequenceDiagram
    actor Aluno
    participant Navegador
    participant Controller as ProdutoController
    participant Repository as ProdutoRepository
    participant Banco as MongoDB Atlas
    participant Template as Thymeleaf
    Aluno->>Navegador: Abre /produtos
    Navegador->>Controller: GET /produtos
    Controller->>Repository: findAll()
    Repository->>Banco: Consulta os documentos
    Banco-->>Repository: Produtos encontrados
    Repository-->>Controller: Lista de produtos
    Controller->>Template: Dados + nome da tela
    Template-->>Navegador: HTML preenchido
    Navegador-->>Aluno: Mostra a lista
```

Se o visualizador não mostrar o diagrama: o navegador pede a página, o controller busca os dados pelo repository, e o Thymeleaf monta o HTML da resposta.

## 1. Preparar o computador

Você precisa de um **JDK 17**, um editor como VS Code ou IntelliJ IDEA, um navegador, internet e uma conta no MongoDB Atlas. JDK é o pacote que contém o Java e as ferramentas de compilação.

No terminal, confira:

```powershell
java -version
javac -version
```

Para acompanhar a aula, configure o JDK 17. Este projeto pede especificamente essa versão na configuração do Gradle. Ter somente o Java 21 ou 26 não substitui a instalação do JDK 17.

Se o Java não for encontrado, instale o JDK 17 e configure `JAVA_HOME` para a pasta da instalação. Feche e abra novamente o terminal após mudar as variáveis do Windows.

Se o JDK 17 já estiver instalado, mas o Gradle não o encontrar, crie um arquivo chamado `gradle.properties` na raiz do projeto:

```properties
org.gradle.java.installations.paths=C:/CAMINHO/DO/SEU/JDK-17
```

Troque o caminho de exemplo pela pasta real do seu JDK, **sem acrescentar `/bin`**. Use barras `/` no arquivo. O caminho é diferente em cada computador; não copie o caminho da máquina do professor.

No VS Code, as extensões de Java ajudam a reconhecer o projeto e os imports. Espere a importação das dependências terminar antes de avaliar os avisos do editor.

**Resultado esperado:** o computador tem um JDK 17 acessível ao Gradle.

## 2. Gerar o projeto

O [Spring Initializr](https://start.spring.io/) cria a estrutura inicial. Ele não cria as telas ou as regras do cadastro; faremos isso nas próximas etapas. Esse é também o ponto de partida do [guia oficial de Spring MVC com Thymeleaf](https://spring.io/guides/gs/serving-web-content).

Preencha:

| Campo | Valor |
| --- | --- |
| Project | Gradle - Groovy |
| Language | Java |
| Spring Boot | 4.1.2, versão usada neste projeto |
| Group | `com.example` |
| Artifact / Name | `cadastro-produtos` |
| Package name | `com.example.cadastro_produtos` |
| Packaging | Jar |
| Java | 17 |

O nome da pasta pode ter hífen. O pacote Java usa **sublinhado**, pois `cadastro-produtos` não é um nome válido de pacote Java.

Em **Add Dependencies**, adicione **Spring Web**, **Thymeleaf**, **Spring Data MongoDB** e **Validation**. Clique em **Generate**, extraia o ZIP e abra a pasta no editor.

As opções do Initializr mudam com o tempo. Se 4.1.2 não estiver disponível, gere uma estrutura Gradle/Groovy com Java 17 e ajuste o `build.gradle` para o conteúdo da próxima etapa. Os exemplos deste guia correspondem ao Spring Boot 4.1.2.

Abra o terminal na pasta que contém `build.gradle` e `gradlew.bat`. Exemplo de navegação — adapte ao lugar em que você extraiu o ZIP:

```powershell
cd "C:\Projetos\cadastro-produtos"
Get-ChildItem
```

Se houver uma pasta com o mesmo nome dentro da pasta extraída, entre nela até encontrar esses arquivos.

**Resultado esperado:** a estrutura do projeto está aberta no editor e o terminal está na raiz correta.

## 3. Entender as pastas e dependências

Crie as pastas e arquivos conforme avançar. Ao terminar, a parte principal ficará assim:

```text
cadastro-produtos/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/wrapper/             # Arquivos gerados pelo Initializr
├── .gitignore
├── .env.example               # Modelo sem credenciais reais
├── .env                       # Sua conexão; não compartilhar
└── src/
    ├── main/
    │   ├── java/com/example/cadastro_produtos/
    │   │   ├── CadastroProdutosApplication.java
    │   │   ├── HomeController.java
    │   │   └── produto/
    │   │       ├── Produto.java
    │   │       ├── ProdutoRepository.java
    │   │       └── ProdutoController.java
    │   └── resources/
    │       ├── application.properties
    │       ├── templates/produtos/
    │       │   ├── lista.html
    │       │   └── formulario.html
    │       └── static/css/style.css
    └── test/java/com/example/cadastro_produtos/
        ├── CadastroProdutosApplicationTests.java
        └── produto/ProdutoControllerTests.java
```

`src/main` contém a aplicação. `src/test` contém os testes. `build` é gerada pelo Gradle; não escreva seu código nela. A pasta `bin`, quando existir, também contém arquivos gerados pelo editor.

Uma **dependência** é uma biblioteca que nosso código utiliza. Os nomes que começam com `spring-boot-starter` agrupam bibliotecas necessárias para uma função.

Arquivo: [build.gradle](build.gradle). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```groovy
plugins {
	id 'java'
	id 'org.springframework.boot' version '4.1.2'
	id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-webmvc'
	testImplementation 'org.springframework.boot:spring-boot-starter-data-mongodb-test'
	testImplementation 'org.springframework.boot:spring-boot-starter-thymeleaf-test'
	testImplementation 'org.springframework.boot:spring-boot-starter-validation-test'
	testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
	useJUnitPlatform()
}
```

`implementation` adiciona bibliotecas à aplicação. `testImplementation` adiciona bibliotecas para os testes. A toolchain escolhe o JDK 17. `useJUnitPlatform()` habilita a plataforma de testes usada aqui.

Arquivo: [settings.gradle](settings.gradle). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```groovy
rootProject.name = 'cadastro-produtos'
```

Os scripts `gradlew` e `gradlew.bat` são o **Gradle Wrapper**. Eles permitem executar o Gradle sem instalá-lo separadamente. O projeto de referência usa Gradle 9.8.2, definido em `gradle/wrapper/gradle-wrapper.properties`. Preserve os scripts e o JAR do wrapper gerados pelo Initializr; não crie um JAR vazio manualmente.

Para reproduzir a versão do wrapper desta referência, mantenha os demais arquivos do wrapper e confira esta linha em `gradle/wrapper/gradle-wrapper.properties`:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.8.2-bin.zip
```

## 4. Preparar o MongoDB Atlas

O **cluster** é o ambiente que hospeda os bancos. Dentro dele, usaremos o banco `cadastro_produtos` e a coleção `produtos`. Uma coleção reúne documentos, e cada documento representa um produto.

### 4.1. Criar o cluster

1. Acesse [MongoDB Atlas](https://cloud.mongodb.com/) e entre na sua conta.
2. Crie ou selecione um projeto.
3. Na área **Clusters**, crie um cluster. Para a aula, selecione a opção gratuita, se disponível na sua conta, e confira o custo antes de confirmar.
4. Você pode usar o nome `Cluster0`. Espere o cluster ficar disponível.

Os nomes dos menus podem variar. O [guia de conexão do Atlas](https://www.mongodb.com/docs/atlas/driver-connection/) mostra o fluxo oficial de usuário, IP e conexão.

### 4.2. Criar um usuário de banco

1. Abra **Database & Network Access → Database Users**.
2. Clique em **Add New Database User**.
3. Escolha autenticação por senha e crie um usuário, por exemplo `app-cadastro`.
4. Defina sua própria senha e guarde-a para preencher o `.env`.
5. Nas permissões específicas, atribua o papel `readWrite` ao banco `cadastro_produtos` e salve. Ele permite consultar e modificar os produtos desse banco.

Esse usuário é diferente do usuário usado para entrar no site do Atlas. O nome do cluster também não é o nome do usuário.

### 4.3. Liberar o IP do computador

1. Abra **Network Access → IP Access List**.
2. Clique em **Add IP Address → Add Current IP Address** e confirme.
3. Aguarde a regra ficar ativa. Se trocar de rede, talvez precise adicionar o novo IP.

### 4.4. Copiar o endereço de conexão

1. Volte a **Clusters** e clique em **Connect** no cluster.
2. Selecione **Drivers** e escolha **Java**.
3. Copie a URI que começa com `mongodb+srv://`.

URI é o endereço com as informações necessárias para conectar ao banco. Use o endereço fornecido para o **seu** cluster, sem copiar o endereço de outro aluno.

**Resultado esperado:** cluster disponível, usuário de banco criado, IP autorizado e URI copiada. O banco e a coleção podem aparecer apenas depois do primeiro cadastro bem-sucedido.

## 5. Conectar o projeto ao banco

Crie `.env.example` na raiz, ao lado de `build.gradle`, com este modelo:

```properties
MONGODB_URI=mongodb+srv://USUARIO:SENHA@SEU-CLUSTER/cadastro_produtos?retryWrites=true&w=majority
```

Crie a sua cópia local. Execute este comando somente se ainda não tiver um `.env` configurado:

```powershell
Copy-Item .env.example .env
```

Abra `.env` no editor e substitua a linha pela conexão real. Mantenha o nome `MONGODB_URI=` no início. Configure o usuário de banco, a senha e o endereço do cluster. Entre o endereço e o `?`, informe `/cadastro_produtos`. Preserve os parâmetros fornecidos pelo Atlas depois do `?`.

Exemplo de estrutura, **não de conexão válida**:

```properties
MONGODB_URI=mongodb+srv://app-cadastro:SUA_SENHA@ENDERECO_COPIADO_DO_ATLAS/cadastro_produtos?retryWrites=true&w=majority
```

Não coloque aspas ao redor do valor. Substitua o marcador inteiro: se o Atlas mostrar `<db_password>`, remova também `<` e `>`. O arquivo deve se chamar `.env`, e não `.env.txt`.

Se a senha tiver caracteres reservados de URL, codifique esses caracteres **na senha**, sem codificar a URI inteira. Por exemplo, `@` vira `%40` e `#` vira `%23`. Essa transformação só muda a representação na URI; a senha cadastrada no Atlas continua a mesma.

Não coloque credenciais reais no README, em prints ou no Git. Adicione estas entradas ao `.gitignore` gerado pelo Initializr:

```gitignore
.env
gradle.properties
```

O `.env.example` pode ser compartilhado porque contém apenas marcadores. O `gradle.properties` fica local porque o caminho do JDK depende do computador.

Agora configure o Spring Boot:

Arquivo: [src/main/resources/application.properties](src/main/resources/application.properties). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```properties
spring.application.name=cadastro-produtos
spring.config.import=optional:file:./.env[.properties]
spring.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/cadastro_produtos}
```

Entenda cada linha:

| Configuração | Efeito |
| --- | --- |
| `spring.application.name` | Define o nome da aplicação |
| `spring.config.import` | Importa o `.env` da pasta em que o comando foi executado; `[.properties]` indica o formato do arquivo |
| `spring.mongodb.uri` | Usa o valor de `MONGODB_URI`; se estiver ausente, tenta o MongoDB local |

O Spring Boot lê nosso `.env` **porque configuramos essa importação**. O trecho `optional:` permite iniciar sem o arquivo, mas não cria um banco local. Se você escolher a alternativa local, precisará ter um MongoDB ativo em `localhost:27017`.

No Spring Boot 4, use `spring.mongodb.uri`. Não troque por `spring.data.mongodb.uri`, nome usado em versões anteriores.

Uma variável `MONGODB_URI` já definida no ambiente pode ter prioridade sobre o arquivo. Depois de alterar a conexão, reinicie a aplicação.

## 6. Criar a classe principal

O Initializr normalmente já cria essa classe. Confira o nome, o pacote e o conteúdo. Ela fica acima dos outros pacotes para o Spring encontrar os controllers e o repository.

Arquivo: [src/main/java/com/example/cadastro_produtos/CadastroProdutosApplication.java](src/main/java/com/example/cadastro_produtos/CadastroProdutosApplication.java). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```java
package com.example.cadastro_produtos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CadastroProdutosApplication {

	public static void main(String[] args) {
		SpringApplication.run(CadastroProdutosApplication.class, args);
	}
}
```

O método `main` é o ponto de entrada. `SpringApplication.run` inicia o Spring Boot. A anotação `@SpringBootApplication` habilita a configuração automática e a descoberta das classes da aplicação.

## 7. Representar um produto

Crie o pacote `produto` dentro de `com.example.cadastro_produtos`. A classe `Produto` descreve os dados de cada item.

| Campo | Tipo Java | Regra |
| --- | --- | --- |
| `id` | `String` | Identifica o documento; gerado ao salvar um produto novo |
| `nome` | `String` | Obrigatório, até 100 caracteres |
| `descricao` | `String` | Opcional, até 500 caracteres |
| `categoria` | `String` | Obrigatória, até 60 caracteres |
| `preco` | `BigDecimal` | Obrigatório, mínimo de 0,01 |
| `estoque` | `Integer` | Obrigatório, zero ou maior |

Arquivo: [src/main/java/com/example/cadastro_produtos/produto/Produto.java](src/main/java/com/example/cadastro_produtos/produto/Produto.java). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```java
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
```

As anotações são informações que o Java fornece às bibliotecas. Aqui, `@Document` escolhe a coleção `produtos`, `@Id` marca o identificador e as anotações de validação definem os valores aceitos.

`@NotBlank` rejeita texto vazio ou só com espaços. `@NotNull` exige um valor. `@Size` limita o tamanho do texto. `@DecimalMin` e `@PositiveOrZero` verificam os números.

Os métodos `get...` leem os campos; os métodos `set...` permitem preenchê-los. O Spring os utiliza para ligar os campos do formulário ao objeto Java.

Usamos `BigDecimal` para representar valores decimais sem a imprecisão típica de `double`. Isso não limita automaticamente o preço a duas casas: a versão atual tem essa restrição no formulário, mas ainda não na validação do servidor. Esse será um exercício de melhoria.

## 8. Criar o acesso ao banco

Um repository é a parte do código responsável por acessar os dados. Não precisamos escrever manualmente cada comando do MongoDB neste cadastro simples.

Arquivo: [src/main/java/com/example/cadastro_produtos/produto/ProdutoRepository.java](src/main/java/com/example/cadastro_produtos/produto/ProdutoRepository.java). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```java
package com.example.cadastro_produtos.produto;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProdutoRepository extends MongoRepository<Produto, String> {
}
```

Em `MongoRepository<Produto, String>`, `Produto` é o tipo salvo e `String` é o tipo do identificador. O Spring cria a implementação da interface e fornece métodos como `findAll`, `findById`, `existsById`, `save` e `delete`.

## 9. Criar os controllers

O controller recebe um pedido HTTP, chama o código necessário e escolhe a resposta. Uma **rota** combina um método HTTP e um caminho. `GET` é usado aqui para abrir páginas; `POST`, para enviar alterações.

### 9.1. Página inicial

Quando o aluno abrir `/`, vamos encaminhá-lo para `/produtos`:

Arquivo: [src/main/java/com/example/cadastro_produtos/HomeController.java](src/main/java/com/example/cadastro_produtos/HomeController.java). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```java
package com.example.cadastro_produtos;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String inicio() {
		return "redirect:/produtos";
	}
}
```

### 9.2. Operações de produto

Estas serão nossas rotas:

| Método | Caminho | Ação |
| --- | --- | --- |
| GET | `/` | Redirecionar para a lista |
| GET | `/produtos` | Listar os produtos |
| GET | `/produtos/novo` | Abrir formulário vazio |
| GET | `/produtos/{id}/editar` | Abrir formulário com um produto |
| POST | `/produtos/salvar` | Cadastrar ou atualizar |
| POST | `/produtos/{id}/excluir` | Excluir o produto |

`{id}` representa o identificador do produto escolhido. Não é um texto para digitar literalmente no navegador.

Arquivo: [src/main/java/com/example/cadastro_produtos/produto/ProdutoController.java](src/main/java/com/example/cadastro_produtos/produto/ProdutoController.java). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```java
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
```

Leia o controller com atenção:

- O construtor recebe o repository. O Spring fornece essa dependência; isso é chamado de **injeção de dependência**.
- `Model` transporta os dados para o HTML. Por exemplo, o atributo `produtos` será lido pela tela de listagem.
- O retorno `produtos/lista` procura `templates/produtos/lista.html`. Não é um caminho para um arquivo Java.
- `@ModelAttribute` recebe os campos enviados pelo formulário em um objeto `Produto`.
- `@Valid` aplica as regras da classe `Produto`. O `BindingResult`, imediatamente depois desse objeto no método, informa se houve erro.
- Se houver erro, o formulário reaparece com os dados e as mensagens. O produto não é salvo.
- Sem ID, o objeto representa um novo cadastro. Com ID, o controller verifica se o produto existe antes de atualizar.
- `redirect:/produtos` manda o navegador abrir a lista após salvar ou excluir. Isso evita repetir o POST apenas por atualizar a página de destino.
- `addFlashAttribute` leva a mensagem de sucesso até a próxima página.
- Um ID não encontrado resulta em HTTP 404, que significa recurso não encontrado.

Neste projeto pequeno, o controller chama o repository diretamente. Uma camada de serviço pode ser adicionada quando houver regras de negócio mais complexas.

## 10. Criar as telas

Crie `templates/produtos` dentro de `src/main/resources`. As páginas precisam ser acessadas pelo servidor, pois o Thymeleaf preenche as expressões antes de enviá-las ao navegador. Abrir o HTML com duplo clique não executa esse processamento.

### 10.1. Formulário de cadastro e edição

A mesma página atende às duas operações: no cadastro, os campos estão vazios; na edição, recebem os dados do produto selecionado.

Arquivo: [src/main/resources/templates/produtos/formulario.html](src/main/resources/templates/produtos/formulario.html). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```html
<!DOCTYPE html>
<html lang="pt-BR" xmlns:th="http://www.thymeleaf.org">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title th:text="${titulo} + ' | Estoque Fácil'">Produto | Estoque Fácil</title>
	<link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
	<header class="topbar">
		<a class="brand" th:href="@{/produtos}"><span class="brand-mark">P</span> Estoque Fácil</a>
		<span class="topbar-caption">Cadastro de produtos</span>
	</header>

	<main class="container form-container">
		<a class="back-link" th:href="@{/produtos}">← Voltar para produtos</a>
		<section class="form-card">
			<div class="form-heading">
				<p class="eyebrow">CATÁLOGO</p>
				<h1 th:text="${titulo}">Cadastrar produto</h1>
				<p class="subtitle">Preencha os dados do produto abaixo.</p>
			</div>

			<form class="product-form" th:action="@{/produtos/salvar}" th:object="${produto}" method="post">
				<input type="hidden" th:field="*{id}" th:if="${produto.id != null}">

				<div class="form-field">
					<label for="nome">Nome do produto <span class="required">*</span></label>
					<input id="nome" type="text" th:field="*{nome}" maxlength="100" placeholder="Ex.: Fone Bluetooth" required autofocus>
					<p class="field-error" th:if="${#fields.hasErrors('nome')}" th:errors="*{nome}"></p>
				</div>

				<div class="form-field">
					<label for="categoria">Categoria <span class="required">*</span></label>
					<input id="categoria" type="text" th:field="*{categoria}" maxlength="60" placeholder="Ex.: Eletrônicos" required>
					<p class="field-error" th:if="${#fields.hasErrors('categoria')}" th:errors="*{categoria}"></p>
				</div>

				<div class="form-field">
					<label for="descricao">Descrição</label>
					<textarea id="descricao" th:field="*{descricao}" rows="4" maxlength="500" placeholder="Detalhes do produto (opcional)"></textarea>
					<p class="field-hint">Até 500 caracteres.</p>
					<p class="field-error" th:if="${#fields.hasErrors('descricao')}" th:errors="*{descricao}"></p>
				</div>

				<div class="form-row">
					<div class="form-field">
						<label for="preco">Preço (R$) <span class="required">*</span></label>
						<input id="preco" type="number" th:field="*{preco}" min="0.01" step="0.01" placeholder="0,00" required>
						<p class="field-error" th:if="${#fields.hasErrors('preco')}" th:errors="*{preco}"></p>
					</div>
					<div class="form-field">
						<label for="estoque">Quantidade em estoque <span class="required">*</span></label>
						<input id="estoque" type="number" th:field="*{estoque}" min="0" step="1" placeholder="0" required>
						<p class="field-error" th:if="${#fields.hasErrors('estoque')}" th:errors="*{estoque}"></p>
					</div>
				</div>

				<div class="form-actions">
					<a class="button button-secondary" th:href="@{/produtos}">Cancelar</a>
					<button class="button button-primary" type="submit">Salvar produto</button>
				</div>
			</form>
		</section>
	</main>
</body>
</html>
```

### 10.2. Listagem

Esta página mostra os produtos, as mensagens de sucesso e os botões de edição e exclusão. Se a lista estiver vazia, oferece o primeiro cadastro.

Arquivo: [src/main/resources/templates/produtos/lista.html](src/main/resources/templates/produtos/lista.html). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```html
<!DOCTYPE html>
<html lang="pt-BR" xmlns:th="http://www.thymeleaf.org">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Produtos | Cadastro de Produtos</title>
	<link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
	<header class="topbar">
		<a class="brand" th:href="@{/produtos}"><span class="brand-mark">P</span> Estoque Fácil</a>
		<span class="topbar-caption">Cadastro de produtos</span>
	</header>

	<main class="container">
		<section class="page-heading">
			<div>
				<p class="eyebrow">CATÁLOGO</p>
				<h1>Seus produtos</h1>
				<p class="subtitle">Gerencie os itens cadastrados no seu estoque.</p>
			</div>
			<a class="button button-primary" th:href="@{/produtos/novo}"><span aria-hidden="true">+</span> Novo produto</a>
		</section>

		<div class="alert alert-success" role="status" th:if="${sucesso}" th:text="${sucesso}"></div>

		<section class="product-list" aria-label="Lista de produtos" th:if="${!#lists.isEmpty(produtos)}">
			<article class="product-card" th:each="produto : ${produtos}">
				<div class="product-main">
					<div class="product-avatar" aria-hidden="true" th:text="${#strings.substring(produto.nome, 0, 1).toUpperCase()}">P</div>
					<div class="product-info">
						<div class="product-title-line">
							<h2 th:text="${produto.nome}">Nome do produto</h2>
							<span class="category" th:text="${produto.categoria}">Categoria</span>
						</div>
						<p class="product-description" th:text="${produto.descricao != null and !produto.descricao.isBlank() ? produto.descricao : 'Sem descrição'}">Descrição do produto</p>
					</div>
				</div>
				<div class="product-meta">
					<div>
						<span class="meta-label">Preço</span>
						<strong class="price" th:text="${'R$ ' + #numbers.formatDecimal(produto.preco, 1, 'POINT', 2, 'COMMA')}">R$ 0,00</strong>
					</div>
					<div>
						<span class="meta-label">Estoque</span>
						<strong th:text="${produto.estoque} + ' un.'">0 un.</strong>
					</div>
				</div>
				<div class="product-actions">
					<a class="button button-secondary" th:href="@{/produtos/{id}/editar(id=${produto.id})}">Editar</a>
					<form th:action="@{/produtos/{id}/excluir(id=${produto.id})}" method="post"
						onsubmit="return confirm('Deseja realmente excluir este produto?');">
						<button class="button button-danger" type="submit">Excluir</button>
					</form>
				</div>
			</article>
		</section>

		<section class="empty-state" th:if="${#lists.isEmpty(produtos)}">
			<div class="empty-icon" aria-hidden="true">＋</div>
			<h2>Nenhum produto cadastrado</h2>
			<p>Comece adicionando o primeiro item ao seu catálogo.</p>
			<a class="button button-primary" th:href="@{/produtos/novo}">Cadastrar primeiro produto</a>
		</section>
	</main>
</body>
</html>
```

### 10.3. Entender o Thymeleaf

| Expressão | O que faz |
| --- | --- |
| `th:text="${titulo}"` | Substitui o texto pelo valor de `titulo` |
| `th:object="${produto}"` | Seleciona o objeto usado pelo formulário |
| `th:field="*{nome}"` | Liga o campo ao atributo `nome` desse objeto |
| `th:each="produto : ${produtos}"` | Repete um trecho para cada produto |
| `th:if="..."` | Mostra um trecho somente quando a condição é verdadeira |
| `th:errors="*{nome}"` | Mostra os erros de validação do campo |
| `th:href="@{/produtos}"` | Gera o endereço de um link |
| `th:action="@{/produtos/salvar}"` | Define o destino do formulário |

`required`, `min`, `maxlength` e `step` ajudam o navegador a verificar os dados. A validação no Java também é necessária, pois uma requisição pode ser enviada sem usar nosso formulário.

O campo oculto `id` identifica o produto durante a edição. A exclusão usa um formulário POST e uma confirmação em JavaScript. A confirmação ajuda a evitar um clique acidental; ela não é uma regra de autorização.

**Resultado esperado:** os dois HTMLs estão nas pastas corretas e usam os mesmos nomes de atributos que o controller.

## 11. Adicionar os estilos

Crie `src/main/resources/static/css/style.css`. O Spring disponibiliza esse arquivo em `/css/style.css`; por isso o HTML não inclui `static` no endereço.

O CSS usa variáveis de cor em `:root`, Flexbox para organizar os elementos e media queries para ajustar o layout em telas menores. As classes `.product-card`, `.form-field` e `.button` correspondem às classes usadas nos HTMLs.

Expanda o bloco abaixo para copiar o CSS completo. Você também pode abrir o arquivo pelo link e estudar cada grupo de seletores.

<details>
<summary>Ver o CSS completo</summary>


Arquivo: [src/main/resources/static/css/style.css](src/main/resources/static/css/style.css). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```css
:root {
	--ink: #172b4d;
	--muted: #667085;
	--line: #e4e9f0;
	--surface: #ffffff;
	--page: #f5f7fb;
	--blue: #2563eb;
	--blue-dark: #1d4ed8;
	--red: #c2413b;
	--green: #18794e;
}

* {
	box-sizing: border-box;
}

body {
	margin: 0;
	background: var(--page);
	color: var(--ink);
	font-family: Inter, "Segoe UI", Arial, sans-serif;
	font-size: 15px;
	line-height: 1.5;
}

a {
	color: inherit;
	text-decoration: none;
}

.topbar {
	min-height: 68px;
	padding: 0 max(24px, calc((100% - 1120px) / 2));
	display: flex;
	align-items: center;
	justify-content: space-between;
	background: var(--surface);
	border-bottom: 1px solid var(--line);
}

.brand {
	display: inline-flex;
	align-items: center;
	gap: 10px;
	font-size: 17px;
	font-weight: 750;
	letter-spacing: -0.3px;
}

.brand-mark {
	width: 32px;
	height: 32px;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	border-radius: 10px;
	background: #eaf1ff;
	color: var(--blue);
	font-weight: 800;
}

.topbar-caption,
.subtitle,
.meta-label,
.field-hint {
	color: var(--muted);
}

.container {
	width: min(100% - 40px, 1000px);
	margin: 48px auto;
}

.page-heading {
	margin-bottom: 28px;
	display: flex;
	align-items: flex-end;
	justify-content: space-between;
	gap: 20px;
}

.eyebrow {
	margin: 0 0 6px;
	color: var(--blue);
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 1.4px;
}

h1,
h2,
p {
	margin-top: 0;
}

h1 {
	margin-bottom: 5px;
	font-size: clamp(26px, 4vw, 34px);
	letter-spacing: -1px;
}

.subtitle {
	margin-bottom: 0;
}

.button {
	min-height: 42px;
	padding: 0 16px;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
	border: 1px solid transparent;
	border-radius: 9px;
	font: inherit;
	font-weight: 650;
	cursor: pointer;
	transition: background-color 120ms ease, border-color 120ms ease, transform 120ms ease;
}

.button:hover {
	transform: translateY(-1px);
}

.button-primary {
	background: var(--blue);
	color: white;
}

.button-primary:hover {
	background: var(--blue-dark);
}

.button-secondary {
	background: white;
	border-color: var(--line);
	color: var(--ink);
}

.button-secondary:hover {
	background: #f8fafc;
	border-color: #cbd5e1;
}

.button-danger {
	background: white;
	border-color: #f1d4d2;
	color: var(--red);
}

.button-danger:hover {
	background: #fff5f4;
}

.product-list {
	display: flex;
	flex-direction: column;
	gap: 14px;
}

.product-card {
	padding: 20px 22px;
	display: flex;
	align-items: center;
	gap: 24px;
	background: var(--surface);
	border: 1px solid var(--line);
	border-radius: 14px;
	box-shadow: 0 3px 12px rgb(23 43 77 / 3%);
}

.product-main {
	min-width: 220px;
	flex: 1 1 40%;
	display: flex;
	align-items: center;
	gap: 14px;
}

.product-avatar {
	width: 46px;
	height: 46px;
	flex: 0 0 46px;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 13px;
	background: #eef3ff;
	color: var(--blue);
	font-size: 20px;
	font-weight: 750;
}

.product-info {
	min-width: 0;
}

.product-title-line {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 9px;
}

.product-title-line h2 {
	margin: 0;
	font-size: 16px;
	font-weight: 700;
}

.category {
	padding: 3px 9px;
	border-radius: 999px;
	background: #f0f3f8;
	color: #596579;
	font-size: 11px;
	font-weight: 650;
}

.product-description {
	max-width: 340px;
	margin: 5px 0 0;
	overflow: hidden;
	color: var(--muted);
	font-size: 13px;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.product-meta {
	flex: 0 1 210px;
	display: flex;
	justify-content: space-between;
	gap: 20px;
}

.product-meta > div {
	display: flex;
	flex-direction: column;
	gap: 3px;
}

.meta-label {
	font-size: 12px;
}

.price {
	color: var(--ink);
}

.product-actions {
	display: flex;
	align-items: center;
	gap: 8px;
}

.product-actions form {
	margin: 0;
}

.product-actions .button {
	min-height: 36px;
	padding: 0 12px;
	font-size: 13px;
}

.alert {
	margin-bottom: 18px;
	padding: 12px 16px;
	border-radius: 9px;
}

.alert-success {
	border: 1px solid #bee5cf;
	background: #effaf3;
	color: var(--green);
}

.empty-state {
	padding: 58px 20px;
	display: flex;
	flex-direction: column;
	align-items: center;
	text-align: center;
	background: white;
	border: 1px dashed #cbd5e1;
	border-radius: 14px;
}

.empty-icon {
	width: 52px;
	height: 52px;
	margin-bottom: 16px;
	display: grid;
	place-items: center;
	border-radius: 16px;
	background: #eef3ff;
	color: var(--blue);
	font-size: 30px;
}

.empty-state h2 {
	margin-bottom: 5px;
	font-size: 18px;
}

.empty-state p {
	margin-bottom: 20px;
	color: var(--muted);
}

.form-container {
	max-width: 720px;
	margin-top: 32px;
}

.back-link {
	display: inline-block;
	margin-bottom: 18px;
	color: #475569;
	font-weight: 600;
}

.back-link:hover {
	color: var(--blue);
}

.form-card {
	padding: 30px;
	background: white;
	border: 1px solid var(--line);
	border-radius: 14px;
	box-shadow: 0 3px 12px rgb(23 43 77 / 3%);
}

.form-heading {
	margin-bottom: 26px;
	padding-bottom: 20px;
	border-bottom: 1px solid var(--line);
}

.product-form {
	display: flex;
	flex-direction: column;
	gap: 19px;
}

.form-field {
	min-width: 0;
	flex: 1 1 0;
	display: flex;
	flex-direction: column;
	gap: 7px;
}

.form-field label {
	font-size: 13px;
	font-weight: 700;
}

.required {
	color: var(--red);
}

.form-field input,
.form-field textarea {
	width: 100%;
	min-height: 43px;
	padding: 10px 12px;
	border: 1px solid #d6dde7;
	border-radius: 8px;
	background: white;
	color: var(--ink);
	font: inherit;
	outline: none;
}

.form-field textarea {
	min-height: 100px;
	resize: vertical;
}

.form-field input:focus,
.form-field textarea:focus {
	border-color: var(--blue);
	box-shadow: 0 0 0 3px rgb(37 99 235 / 12%);
}

.field-hint,
.field-error {
	margin: 0;
	font-size: 12px;
}

.field-error {
	color: var(--red);
}

.form-row {
	display: flex;
	gap: 18px;
}

.form-actions {
	margin-top: 4px;
	padding-top: 20px;
	display: flex;
	justify-content: flex-end;
	gap: 10px;
	border-top: 1px solid var(--line);
}

@media (max-width: 760px) {
	.product-card {
		align-items: stretch;
		flex-wrap: wrap;
		gap: 16px;
	}

	.product-main {
		flex-basis: calc(100% - 60px);
	}

	.product-meta {
		flex: 1 1 100%;
		padding-left: 60px;
	}

	.product-actions {
		margin-left: 60px;
	}
}

@media (max-width: 520px) {
	.topbar {
		padding: 0 18px;
	}

	.topbar-caption {
		font-size: 12px;
	}

	.container {
		width: min(100% - 28px, 1000px);
		margin: 30px auto;
	}

	.page-heading {
		align-items: flex-start;
		flex-direction: column;
	}

	.page-heading .button {
		width: 100%;
	}

	.product-card {
		padding: 16px;
	}

	.product-main {
		min-width: 0;
		flex-basis: 100%;
	}

	.product-meta,
	.product-actions {
		margin-left: 0;
		padding-left: 0;
	}

	.product-actions {
		width: 100%;
	}

	.product-actions .button {
		width: 100%;
	}

	.product-actions form {
		flex: 1;
	}

	.form-card {
		padding: 22px 18px;
	}

	.form-row {
		flex-direction: column;
	}
}
```

</details>

## 12. Executar e experimentar

Salve os arquivos e confirme que o terminal está na pasta de `build.gradle` e `.env`. Execute:

```powershell
.\gradlew.bat bootRun
```

Na primeira execução, o Gradle pode levar mais tempo para baixar arquivos. O servidor fica ligado enquanto esse comando está rodando; o terminal não volta imediatamente ao prompt.

Espere a mensagem `Started CadastroProdutosApplication` e abra **http://localhost:8080**. A aplicação deve redirecionar para `/produtos`.

Se a porta estiver ocupada, use outra:

```powershell
.\gradlew.bat bootRun --args="--server.port=8082"
```

Nesse caso, abra **http://localhost:8082**. A porta padrão do projeto continua sendo 8080; 8082 é apenas uma alternativa definida no comando.

### 12.1. Testar o fluxo no navegador

1. Clique em **Novo produto**.
2. Cadastre um item de teste: nome `Teclado de teste`, categoria `Periféricos`, descrição `Produto da aula`, preço `199,90` e estoque `5`. Se o navegador exigir ponto decimal, digite `199.90`.
3. Clique em **Salvar produto**. O item deve aparecer na lista com a mensagem de sucesso.
4. Clique em **Editar**, altere o estoque para `8` e salve. Confira a mudança.
5. No Atlas, abra **Browse Collections** ou **Data Explorer** e procure `cadastro_produtos → produtos`. Você deve encontrar o documento salvo.
6. Atualize a página ou reinicie a aplicação para confirmar que o item continua salvo no banco.
7. Exclua apenas o produto de teste e confirme que ele desapareceu da lista.
8. Experimente deixar o nome vazio ou informar estoque negativo. O navegador deve impedir o envio ou o formulário deve apresentar um erro.

O cadastro e a listagem confirmam mais do que apenas abrir o formulário: eles precisam conversar com o banco. Ver o formulário sozinho não comprova a conexão.

Para parar o servidor, pressione **Ctrl+C** no terminal. Depois de alterar Java ou configurações, pare e execute novamente; este projeto não inclui reinício automático com DevTools.

## 13. Criar e executar os testes

Testes automatizados executam verificações que podem ser repetidas depois de uma alteração. Aqui usamos JUnit para organizar os testes e Mockito para simular o repository.

### 13.1. Testar a inicialização e a configuração

Substitua o teste gerado pelo Initializr pelo código abaixo. Ele confere a inicialização e se a URI definida para o teste chega à configuração do MongoDB.

Arquivo: [src/test/java/com/example/cadastro_produtos/CadastroProdutosApplicationTests.java](src/test/java/com/example/cadastro_produtos/CadastroProdutosApplicationTests.java). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```java
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
```

`spring.config.import=` desativa a leitura do `.env` neste teste. A URI é local e exclusiva da configuração de teste. Nenhuma operação de cadastro é realizada por esses dois métodos.

### 13.2. Testar o controller

O mock do repository é um objeto de simulação. Com `when`, informamos o que ele deve devolver; com `verify`, conferimos se o controller chamou a operação esperada. Assim, estes testes não precisam acessar o Atlas.

<details>
<summary>Ver os seis testes do controller</summary>


Arquivo: [src/test/java/com/example/cadastro_produtos/produto/ProdutoControllerTests.java](src/test/java/com/example/cadastro_produtos/produto/ProdutoControllerTests.java). Crie ou substitua esse arquivo pelo conteúdo abaixo:

```java
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
```

</details>

Execute os testes na raiz do projeto:

```powershell
.\gradlew.bat test
```

O resultado esperado é `BUILD SUCCESSFUL`. Com os arquivos deste guia, são oito testes: dois de contexto/configuração e seis do controller. Se aparecer `UP-TO-DATE`, o Gradle reaproveitou um resultado porque as entradas não mudaram. Para forçar uma nova execução, use `test --rerun-tasks`.

Abra `build/reports/tests/test/index.html` no navegador para ler o relatório.

**Limite desses testes:** eles não comprovam autenticação no Atlas, renderização das telas nem gravação real. Os testes do controller chamam os métodos diretamente e simulam um erro de validação; não enviam uma requisição HTTP real. No teste de contexto pode aparecer um aviso de conexão local recusada, mesmo com os testes aprovados, porque nenhuma consulta é exigida. Faça também o roteiro manual da etapa 12.

## 14. Gerar o executável

Para compilar, executar os testes e gerar o pacote:

```powershell
.\gradlew.bat build
```

O JAR executável ficará em `build/libs/cadastro-produtos-0.0.1-SNAPSHOT.jar`. A partir da raiz do projeto, execute com o Java configurado:

```powershell
java -jar .\build\libs\cadastro-produtos-0.0.1-SNAPSHOT.jar
```

Use o JDK 17 para reproduzir o ambiente da aula. Se `java` no terminal apontar para outra instalação, ajuste `JAVA_HOME`/`PATH` ou chame o executável do JDK 17 pelo caminho completo.

O JAR inclui o servidor web e as bibliotecas, mas não inclui suas credenciais nem o MongoDB Atlas. O `.env` precisa estar no diretório de execução, ou `MONGODB_URI` precisa ser fornecida pelo ambiente. Pare o `bootRun` antes de iniciar o JAR na mesma porta.

## 15. Resolver problemas comuns

Procure primeiro a mensagem de erro no terminal. Um HTTP 500 no navegador indica falha no servidor, mas não informa sozinho a causa.

| Problema | O que verificar |
| --- | --- |
| `java` ou `javac` não reconhecido | Instalação do JDK e configuração de `JAVA_HOME`/`PATH`; reabra o terminal |
| Gradle não encontra Java 17 | Instale o JDK 17 ou ajuste `org.gradle.java.installations.paths` conforme a etapa 1 |
| `gradlew.bat` não encontrado | Entre na pasta onde estão o script e `build.gradle`; use `.\gradlew.bat` no PowerShell |
| Dependências não são baixadas | Confira internet, acesso aos repositórios e configurações de proxy da rede |
| Porta 8080 em uso | Pare a execução anterior com Ctrl+C ou escolha 8082 pelo comando da etapa 12 |
| `Authentication failed` | Confira usuário de banco e senha, sem os marcadores `< >`; codifique caracteres reservados |
| Conexão com Atlas expira | Confira cluster ativo, IP liberado e acesso da rede às portas indicadas pelo Atlas |
| `Failed looking up TXT record` ou erro SRV/DNS | Confira o endereço copiado e a resolução DNS da rede; não é necessariamente erro de senha |
| A aplicação tenta `localhost:27017` | Confira `.env`, nome `MONGODB_URI`, diretório de execução e `spring.mongodb.uri` |
| Formulário abre, mas lista dá HTTP 500 | A listagem consulta o banco; procure a causa de conexão ou autenticação no terminal |
| Mudou o `.env`, mas o comportamento não mudou | Reinicie a aplicação e confira se existe uma variável de ambiente substituindo o valor |
| Tela retorna HTTP 404 | Confira o caminho acessado e se o ID do produto ainda existe |
| HTML não mostra os dados | Use a URL do servidor; confira `templates/produtos` e os nomes dos atributos do `Model` |
| Página sem estilo | Confira `static/css/style.css` e o endereço `/css/style.css` no HTML |
| Código vermelho no editor após extrair o ZIP | Espere a importação do Gradle e confira o JDK configurado no editor |

O comando `bootRun` deve permanecer ativo para o site funcionar. Fechar o terminal encerra uma execução iniciada nele.

## 16. Praticar com novos desafios

Depois de concluir o CRUD, faça uma mudança por vez e teste o resultado:

1. Adicione um campo `marca` ao modelo e às duas telas.
2. Limite o preço a duas casas decimais no servidor e crie um teste para `10.999`.
3. Adicione uma pesquisa por nome.
4. Ordene a lista e implemente paginação para não carregar todos os produtos de uma vez.
5. Destaque produtos com estoque baixo usando CSS.
6. Adicione testes HTTP para formulário inválido, edição e produto inexistente.

Para conferir se entendeu, explique com suas palavras: por que precisamos de um repository? Como o `Model` chega ao HTML? Como o mesmo formulário distingue cadastro e edição? Por que um teste aprovado não garante acesso ao Atlas?

Este é um projeto didático: ainda não possui login, autorização, proteção CSRF configurada ou controle de edições simultâneas. Esses assuntos são próximos passos antes de disponibilizar um sistema de estoque para uso compartilhado.
