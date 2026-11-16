package view;

import exception.DadoInvalidoException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import service.FuncionarioService;
import util.FormatadorMoeda;

import java.io.PrintStream;
import java.io.Serial;
import java.util.List;
import java.util.Scanner;

/**
 * Interface de console do sistema.
 * <p>
 * Lê as opções do usuário, chama o {@link FuncionarioService} e apresenta os resultados.
 * É aqui que as exceções do sistema são tratadas e convertidas em mensagens amigáveis.
 * Entrada e saída são injetadas para permitir testes automatizados.
 */
public class FuncionarioView {

    private final FuncionarioService service;
    private final Scanner entrada;
    private final PrintStream saida;

    /** Sinaliza que a entrada terminou (ex.: Ctrl+D ou fim do arquivo em testes). */
    private static class EntradaEncerradaException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    public FuncionarioView(FuncionarioService service, Scanner entrada, PrintStream saida) {
        this.service = service;
        this.entrada = entrada;
        this.saida = saida;
    }

    /** Inicia o laço principal do menu. */
    public void iniciar() {
        boolean executando = true;
        while (executando) {
            exibirMenu();
            try {
                executando = processarOpcao(lerLinha("Escolha uma opção: "));
            } catch (EntradaEncerradaException e) {
                executando = false;
            } catch (FuncionarioNaoEncontradoException | FuncionarioJaCadastradoException
                     | DadoInvalidoException e) {
                saida.println("Erro: " + e.getMessage());
            }
        }
        saida.println("Sistema encerrado. Até logo!");
    }

    private void exibirMenu() {
        saida.println();
        saida.println("===== MENU - FOLHA DE PAGAMENTO =====");
        saida.println("1 - Cadastrar funcionário");
        saida.println("2 - Listar funcionários");
        saida.println("3 - Buscar funcionário");
        saida.println("4 - Remover funcionário");
        saida.println("5 - Calcular remuneração de um funcionário");
        saida.println("6 - Exibir dados completos de um funcionário");
        saida.println("7 - Gerar relatório da folha");
        saida.println("8 - Calcular total da folha de pagamento");
        saida.println("0 - Sair");
    }

    /** @return {@code false} quando o usuário escolhe sair */
    private boolean processarOpcao(String opcao)
            throws FuncionarioNaoEncontradoException, FuncionarioJaCadastradoException {
        switch (opcao) {
            case "1" -> cadastrar();
            case "2" -> listar(service.listarTodos());
            case "3" -> buscar();
            case "4" -> remover();
            case "5" -> calcularRemuneracao();
            case "6" -> saida.println(service.exibirDados(lerInteiro("Código: ")));
            case "7" -> saida.println(service.gerarRelatorio());
            case "8" -> saida.println("Total da folha de pagamento: "
                    + FormatadorMoeda.formatar(service.calcularTotalFolha()));
            case "0" -> {
                return false;
            }
            default -> saida.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    // ------------------------------------------------------------------ ações

    private void cadastrar() throws FuncionarioJaCadastradoException {
        saida.println("Tipo de funcionário: 1 - CLT | 2 - PJ | 3 - Comissionado");
        String tipo = lerLinha("Tipo: ");
        if (!List.of("1", "2", "3").contains(tipo)) {
            saida.println("Tipo inválido.");
            return;
        }
        int codigo = lerInteiro("Código: ");
        String nome = lerLinha("Nome: ");
        String cpf = lerLinha("CPF: ");

        Funcionario funcionario = switch (tipo) {
            case "1" -> new FuncionarioCLT(codigo, nome, cpf,
                    lerDecimal("Salário-base: "),
                    lerDecimal("Adicional de benefícios: "));
            case "2" -> new FuncionarioPJ(codigo, nome, cpf,
                    lerDecimal("Valor por projeto: "),
                    lerInteiro("Quantidade de projetos: "));
            default -> new FuncionarioComissionado(codigo, nome, cpf,
                    lerDecimal("Salário-base: "),
                    lerDecimal("Percentual de comissão (0 a 100): "),
                    lerDecimal("Total de vendas: "));
        };
        service.cadastrar(funcionario);
        saida.println("Funcionário cadastrado com sucesso: " + funcionario);
    }

    private void listar(List<Funcionario> funcionarios) {
        if (funcionarios.isEmpty()) {
            saida.println("Nenhum funcionário cadastrado.");
            return;
        }
        funcionarios.forEach(f -> saida.println(f + " | "
                + FormatadorMoeda.formatar(f.calcularRemuneracao())));
    }

    private void buscar() throws FuncionarioNaoEncontradoException {
        saida.println("Buscar por: 1 - Código | 2 - CPF | 3 - Nome");
        switch (lerLinha("Opção: ")) {
            case "1" -> listar(List.of(service.buscarFuncionario(lerInteiro("Código: "))));
            case "2" -> listar(List.of(service.buscarFuncionario(lerLinha("CPF: "))));
            case "3" -> {
                String nome = lerLinha("Nome: ");
                boolean exata = lerLinha("Busca exata? (s/n): ").equalsIgnoreCase("s");
                listar(service.buscarFuncionario(nome, exata));
            }
            default -> saida.println("Opção de busca inválida.");
        }
    }

    private void remover() throws FuncionarioNaoEncontradoException {
        int codigo = lerInteiro("Código do funcionário a remover: ");
        service.removerFuncionario(codigo);
        saida.println("Funcionário removido com sucesso.");
    }

    private void calcularRemuneracao() throws FuncionarioNaoEncontradoException {
        int codigo = lerInteiro("Código: ");
        saida.println("Remuneração bruta: " + FormatadorMoeda.formatar(service.calcularRemuneracao(codigo)));
    }

    // ------------------------------------------------------------ leitura

    private String lerLinha(String rotulo) {
        saida.print(rotulo);
        if (!entrada.hasNextLine()) {
            throw new EntradaEncerradaException();
        }
        return entrada.nextLine().trim();
    }

    private int lerInteiro(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerLinha(rotulo));
            } catch (NumberFormatException e) {
                saida.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    /** Aceita tanto "1500.50" quanto "1.500,50". */
    private double lerDecimal(String rotulo) {
        while (true) {
            String texto = lerLinha(rotulo);
            try {
                if (texto.contains(",")) {
                    texto = texto.replace(".", "").replace(',', '.');
                }
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                saida.println("Valor inválido. Digite um número (ex.: 1500,00).");
            }
        }
    }
}
