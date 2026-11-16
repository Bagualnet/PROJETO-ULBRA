package view;

import model.FuncionarioCLT;
import model.FuncionarioPJ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.FuncionarioService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa a interação com o usuário simulando a entrada do teclado e capturando a saída.
 */
class FuncionarioViewTest {

    private FuncionarioService service;

    @BeforeEach
    void setUp() {
        service = new FuncionarioService();
    }

    /** Executa a view com as linhas informadas como entrada e devolve tudo o que foi impresso. */
    private String executar(String... linhas) {
        String entrada = String.join("\n", linhas) + "\n";
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(saida, true, StandardCharsets.UTF_8);
        new FuncionarioView(service, new Scanner(entrada), out).iniciar();
        return saida.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Deve exibir o menu e encerrar com a opção 0")
    void deveEncerrar() {
        String saida = executar("0");
        assertTrue(saida.contains("MENU"));
        assertTrue(saida.contains("Sistema encerrado"));
    }

    @Test
    @DisplayName("Deve cadastrar um funcionário CLT pelo menu")
    void deveCadastrarClt() throws Exception {
        String saida = executar("1", "1", "10", "Maria Silva", "529.982.247-25", "3000", "500", "0");
        assertTrue(saida.contains("cadastrado com sucesso"));
        assertEquals(3500, service.buscarFuncionario(10).calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Deve cadastrar PJ e Comissionado aceitando vírgula decimal")
    void deveCadastrarPjEComissionado() throws Exception {
        executar(
                "1", "2", "20", "João PJ", "111.444.777-35", "1.500,50", "2",
                "1", "3", "30", "Carla", "123.456.789-09", "2000", "5,5", "10000",
                "0");
        assertEquals(3001, service.buscarFuncionario(20).calcularRemuneracao(), 0.001);
        assertEquals(2550, service.buscarFuncionario(30).calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Deve repetir a pergunta quando o número digitado é inválido")
    void deveRepetirEntradaNumericaInvalida() throws Exception {
        String saida = executar("1", "1", "abc", "10", "Maria", "529.982.247-25", "xyz", "3000", "0", "0");
        assertTrue(saida.contains("Valor inválido"));
        assertNotNull(service.buscarFuncionario(10));
    }

    @Test
    @DisplayName("Deve tratar CPF inválido sem encerrar o sistema")
    void deveTratarCpfInvalido() {
        String saida = executar("1", "1", "10", "Maria", "123.456.789-00", "3000", "0", "0");
        assertTrue(saida.contains("Erro: CPF inválido"));
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    @DisplayName("Deve tratar valores negativos")
    void deveTratarValorNegativo() {
        String saida = executar("1", "1", "10", "Maria", "529.982.247-25", "-3000", "0", "0");
        assertTrue(saida.contains("não pode ser negativo"));
    }

    @Test
    @DisplayName("Deve tratar código duplicado")
    void deveTratarDuplicidade() throws Exception {
        service.cadastrar(new FuncionarioCLT(10, "Ana", "529.982.247-25", 1000, 0));
        String saida = executar("1", "2", "10", "Outro", "111.444.777-35", "100", "1", "0");
        assertTrue(saida.contains("Erro: Já existe um funcionário cadastrado com o código 10."));
    }

    @Test
    @DisplayName("Deve listar funcionários cadastrados")
    void deveListar() throws Exception {
        service.cadastrar(new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 1000, 0));
        String saida = executar("2", "0");
        assertTrue(saida.contains("[CLT] #1 - Ana Souza"));
    }

    @Test
    @DisplayName("Deve informar lista vazia")
    void deveInformarListaVazia() {
        assertTrue(executar("2", "0").contains("Nenhum funcionário cadastrado"));
    }

    @Test
    @DisplayName("Deve buscar por código, CPF e nome")
    void deveBuscarDeTodasAsFormas() throws Exception {
        service.cadastrar(new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 1000, 0));
        service.cadastrar(new FuncionarioPJ(2, "Bruno Souza", "111.444.777-35", 500, 2));
        String saida = executar(
                "3", "1", "1",
                "3", "2", "111.444.777-35",
                "3", "3", "souza", "n",
                "3", "3", "Ana", "s",
                "0");
        assertTrue(saida.contains("[CLT] #1 - Ana Souza"));
        assertTrue(saida.contains("[PJ] #2 - Bruno Souza"));
        assertTrue(saida.contains("Nenhum funcionário encontrado com o nome \"Ana\""));
    }

    @Test
    @DisplayName("Deve tratar busca de funcionário inexistente")
    void deveTratarBuscaInexistente() {
        String saida = executar("3", "1", "99", "0");
        assertTrue(saida.contains("Erro: Funcionário com código 99 não encontrado."));
    }

    @Test
    @DisplayName("Deve remover funcionário e tratar remoção inexistente")
    void deveRemover() throws Exception {
        service.cadastrar(new FuncionarioCLT(1, "Ana", "529.982.247-25", 1000, 0));
        String saida = executar("4", "1", "4", "1", "0");
        assertTrue(saida.contains("removido com sucesso"));
        assertTrue(saida.contains("Erro: Funcionário com código 1 não encontrado."));
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    @DisplayName("Deve calcular remuneração, exibir dados, relatório e total da folha")
    void deveExibirCalculosERelatorios() throws Exception {
        service.cadastrar(new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 3000, 500));
        service.cadastrar(new FuncionarioPJ(2, "Bruno Lima", "111.444.777-35", 2000, 3));
        String saida = executar("5", "1", "6", "2", "7", "8", "0");
        assertTrue(saida.contains("3.500,00"));             // remuneração da Ana
        assertTrue(saida.contains("Projetos realizados: 3")); // dados completos do Bruno
        assertTrue(saida.contains("RELATÓRIO DA FOLHA"));
        assertTrue(saida.contains("9.500,00"));             // total da folha
    }

    @Test
    @DisplayName("Deve avisar sobre opção inválida")
    void deveAvisarOpcaoInvalida() {
        String saida = executar("9", "0");
        assertTrue(saida.contains("Opção inválida"));
    }

    @Test
    @DisplayName("Deve encerrar normalmente se a entrada acabar")
    void deveEncerrarQuandoEntradaAcaba() {
        String saida = assertDoesNotThrow(() -> executar("1", "1"));
        assertTrue(saida.contains("Sistema encerrado"));
    }
}
