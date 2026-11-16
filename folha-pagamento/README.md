# Sistema de Gerenciamento de Funcionários e Folha de Pagamento

Aplicação de console em **Java** que cadastra funcionários de diferentes categorias, calcula suas
remunerações e gera o relatório da folha de pagamento. O projeto foi desenvolvido com
**Programação Orientada a Objetos**, **arquitetura em camadas** e **TDD** (JUnit 5).

## Funcionalidades

| # | Funcionalidade | Onde está |
|---|----------------|-----------|
| 1 | Cadastrar funcionários (CLT, PJ, Comissionado) | `FuncionarioService.cadastrar` |
| 2 | Listar todos os funcionários | `FuncionarioService.listarTodos` |
| 3 | Buscar por código, CPF ou nome (exato/parcial) | `FuncionarioService.buscarFuncionario(...)` |
| 4 | Remover funcionário | `FuncionarioService.removerFuncionario` |
| 5 | Calcular a remuneração de um funcionário | `FuncionarioService.calcularRemuneracao` |
| 6 | Exibir os dados completos de um funcionário | `FuncionarioService.exibirDados` |
| 7 | Gerar relatório com todos os funcionários e salários | `FuncionarioService.gerarRelatorio` |
| 8 | Calcular o total da folha de pagamento | `FuncionarioService.calcularTotalFolha` |

## Regras de remuneração

| Categoria | Remuneração bruta | Tributos (`Tributavel`) |
|-----------|-------------------|-------------------------|
| `FuncionarioCLT` | salário-base + adicional de benefícios | 11% do salário-base (benefícios isentos) |
| `FuncionarioPJ` | valor por projeto × quantidade de projetos | não se aplica (o PJ recolhe os próprios tributos) |
| `FuncionarioComissionado` | salário-base + (vendas × percentual / 100) | 11% da remuneração total |

A alíquota de 11% é uma simplificação didática (referência: INSS). A remuneração líquida é
`bruta − tributos`.

## Arquitetura

```
src/
 ├── main/java/
 │    ├── model/        Funcionario (abstrata), FuncionarioCLT, FuncionarioPJ,
 │    │                 FuncionarioComissionado, Tributavel, Identificavel
 │    ├── repository/   FuncionarioRepository   (armazenamento em memória: LinkedHashMap)
 │    ├── service/      FuncionarioService      (regras de negócio)
 │    ├── exception/    FuncionarioNaoEncontradoException, FuncionarioJaCadastradoException,
 │    │                 DadoInvalidoException, CpfInvalidoException
 │    ├── view/         FuncionarioView         (menu de console)
 │    ├── util/         ValidadorCPF, FormatadorMoeda
 │    └── Main.java
 └── test/java/         uma classe de teste para cada classe de produção, nos mesmos pacotes
```

As pastas seguem o padrão do Maven (`src/main/java` e `src/test/java`), que equivale à
estrutura `src/` e `test/` sugerida no enunciado.

| Camada | Responsabilidade |
|--------|------------------|
| **Model** | Entidades e regras de cálculo de cada categoria, com validação nos construtores e setters |
| **Repository** | Guardar, buscar e remover funcionários (sem regras de negócio) |
| **Service** | Regras de negócio: duplicidade de código/CPF, buscas, cálculos, relatório |
| **View** | Ler a entrada do usuário, chamar o serviço, **tratar as exceções** e mostrar mensagens |
| **Exception** | Situações excepcionais do sistema |

## Conceitos de POO aplicados

- **Encapsulamento** – todos os atributos são `private`; código e CPF são `final` (sem setter);
  os setters validam os valores e não alteram o estado quando o valor é inválido.
- **Herança e abstração** – `Funcionario` é abstrata e declara `calcularRemuneracao()`,
  `getTipo()` e `descreverRemuneracao()` como métodos abstratos.
- **Interfaces** – `Identificavel` (implementada por herança em todas as classes concretas) e
  `Tributavel` (implementada apenas por CLT e Comissionado, onde faz sentido).
- **Sobreposição (override)** – cada subclasse sobrescreve `calcularRemuneracao()`:

  ```java
  Funcionario f1 = new FuncionarioCLT(1, "Ana", "529.982.247-25", 3000, 500);
  Funcionario f2 = new FuncionarioPJ(2, "Bruno", "111.444.777-35", 2000, 3);
  Funcionario f3 = new FuncionarioComissionado(3, "Carla", "123.456.789-09", 2000, 5, 10000);
  System.out.println(f1.calcularRemuneracao()); // 3500.0
  System.out.println(f2.calcularRemuneracao()); // 6000.0
  System.out.println(f3.calcularRemuneracao()); // 2500.0
  ```

- **Sobrecarga (overload)** – `FuncionarioService`:

  ```java
  Funcionario buscarFuncionario(int codigo)
  Funcionario buscarFuncionario(String cpf)
  List<Funcionario> buscarFuncionario(String nome, boolean buscaExata)
  ```

## Tratamento de exceções

| Situação | Exceção |
|----------|---------|
| CPF inválido (formato ou dígitos verificadores) | `CpfInvalidoException` |
| Código ou CPF já cadastrado | `FuncionarioJaCadastradoException` (verificada) |
| Buscar/remover funcionário inexistente | `FuncionarioNaoEncontradoException` (verificada) |
| Salário, benefícios, vendas ou valores negativos | `DadoInvalidoException` |
| Percentual de comissão fora de 0–100 | `DadoInvalidoException` |
| Dados obrigatórios não informados (nome, CPF, código ≤ 0) | `DadoInvalidoException` |

As exceções são **lançadas** no model/service e **tratadas** na `FuncionarioView`, que mostra a
mensagem ao usuário e continua a execução do menu.

## Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior

## Como executar

```bash
# compilar e empacotar (também roda os testes)
mvn package

# executar o menu (já vem com 3 funcionários de exemplo)
java -jar target/folha-pagamento-1.0.0.jar

# executar sem os dados de exemplo
java -jar target/folha-pagamento-1.0.0.jar --vazio

# alternativa sem gerar o jar
mvn compile exec:java
```

Na IDE (IntelliJ, Eclipse ou NetBeans), basta importar como projeto Maven e executar a classe `Main`.

> **Windows:** se os acentos aparecerem errados no terminal, execute `chcp 65001` antes de
> rodar o programa.

## Como executar os testes

```bash
mvn test
```

Para rodar uma classe de teste específica:

```bash
mvn test -Dtest=FuncionarioServiceTest
```

Os relatórios ficam em `target/surefire-reports/`.

### O que é testado

| Classe de teste | Cenários |
|-----------------|----------|
| `FuncionarioTest` | validações comuns, setters, `equals`, `exibirDados`, **comportamento polimórfico** |
| `FuncionarioCLTTest` | cálculo da remuneração e dos tributos, validação de dados |
| `FuncionarioPJTest` | cálculo por projetos, valores inválidos, não ser `Tributavel` |
| `FuncionarioComissionadoTest` | cálculo da comissão e da remuneração, limites do percentual |
| `TributavelTest` / `IdentificavelTest` | implementação das interfaces pelas classes concretas |
| `FuncionarioRepositoryTest` | armazenamento, recuperação, busca e remoção |
| `FuncionarioServiceTest` | cadastro, consulta (3 sobrecargas), remoção, duplicidade, cálculos, relatório |
| `FuncionarioViewTest` | fluxo do menu com entrada simulada e tratamento de erros |
| `*ExceptionTest` | criação, mensagens e lançamento das exceções |
| `ValidadorCPFTest` / `FormatadorMoedaTest` | utilitários |

São 141 casos de teste no total (contando os testes parametrizados).

## Desenvolvimento com TDD

Cada parte do sistema seguiu o ciclo **Red → Green → Refactor**, e o histórico do Git mostra
essas etapas:

- `test:` – testes escritos antes da implementação (**Red**: não compilam/falham);
- `feat:` – implementação mínima para os testes passarem (**Green**);
- `refactor:` – melhoria do código mantendo os testes verdes.

Veja com:

```bash
git log --oneline
```
