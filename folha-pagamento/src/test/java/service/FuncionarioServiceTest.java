package service;

import exception.DadoInvalidoException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import repository.FuncionarioRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest {

    private FuncionarioRepository repository;
    private FuncionarioService service;
    private Funcionario ana;
    private Funcionario bruno;
    private Funcionario carla;

    @BeforeEach
    void setUp() {
        repository = new FuncionarioRepository();
        service = new FuncionarioService(repository);
        ana = new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 3000, 500);                 // 3.500
        bruno = new FuncionarioPJ(2, "Bruno Lima", "111.444.777-35", 2000, 3);                 // 6.000
        carla = new FuncionarioComissionado(3, "Carla Souza", "123.456.789-09", 2000, 5, 10000); // 2.500
    }

    private void cadastrarTodos() throws FuncionarioJaCadastradoException {
        service.cadastrar(ana);
        service.cadastrar(bruno);
        service.cadastrar(carla);
    }

    @Nested
    @DisplayName("Cadastro")
    class Cadastro {

        @Test
        @DisplayName("Deve cadastrar funcionário válido")
        void deveCadastrar() throws Exception {
            service.cadastrar(ana);
            assertEquals(1, repository.contar());
            assertSame(ana, repository.buscarPorCodigo(1).orElseThrow());
        }

        @Test
        @DisplayName("Deve lançar FuncionarioJaCadastradoException para código repetido")
        void deveRejeitarCodigoDuplicado() throws Exception {
            service.cadastrar(ana);
            Funcionario mesmoCodigo = new FuncionarioPJ(1, "Outro", "111.444.777-35", 100, 1);
            FuncionarioJaCadastradoException e =
                    assertThrows(FuncionarioJaCadastradoException.class, () -> service.cadastrar(mesmoCodigo));
            assertTrue(e.getMessage().contains("código 1"));
            assertEquals(1, repository.contar());
        }

        @Test
        @DisplayName("Deve lançar FuncionarioJaCadastradoException para CPF repetido")
        void deveRejeitarCpfDuplicado() throws Exception {
            service.cadastrar(ana);
            Funcionario mesmoCpf = new FuncionarioPJ(50, "Outro", "52998224725", 100, 1);
            FuncionarioJaCadastradoException e =
                    assertThrows(FuncionarioJaCadastradoException.class, () -> service.cadastrar(mesmoCpf));
            assertTrue(e.getMessage().contains("529.982.247-25"));
        }

        @Test
        @DisplayName("Deve rejeitar funcionário nulo")
        void deveRejeitarNulo() {
            assertThrows(DadoInvalidoException.class, () -> service.cadastrar(null));
        }
    }

    @Nested
    @DisplayName("Consulta (sobrecarga de buscarFuncionario)")
    class Consulta {

        @BeforeEach
        void popular() throws Exception {
            cadastrarTodos();
        }

        @Test
        @DisplayName("Deve listar todos os funcionários")
        void deveListarTodos() {
            assertEquals(List.of(ana, bruno, carla), service.listarTodos());
        }

        @Test
        @DisplayName("buscarFuncionario(int) deve encontrar pelo código")
        void deveBuscarPorCodigo() throws Exception {
            assertSame(bruno, service.buscarFuncionario(2));
        }

        @Test
        @DisplayName("buscarFuncionario(int) deve lançar exceção para código inexistente")
        void deveFalharCodigoInexistente() {
            FuncionarioNaoEncontradoException e =
                    assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarFuncionario(99));
            assertEquals("Funcionário com código 99 não encontrado.", e.getMessage());
        }

        @Test
        @DisplayName("buscarFuncionario(String) deve encontrar pelo CPF")
        void deveBuscarPorCpf() throws Exception {
            assertSame(carla, service.buscarFuncionario("123.456.789-09"));
            assertSame(carla, service.buscarFuncionario("12345678909"));
        }

        @Test
        @DisplayName("buscarFuncionario(String) deve lançar exceção para CPF não cadastrado")
        void deveFalharCpfInexistente() {
            assertThrows(FuncionarioNaoEncontradoException.class,
                    () -> service.buscarFuncionario("935.411.347-80"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("buscarFuncionario(String) deve exigir o CPF")
        void deveExigirCpf(String cpf) {
            assertThrows(DadoInvalidoException.class, () -> service.buscarFuncionario(cpf));
        }

        @Test
        @DisplayName("buscarFuncionario(String, true) deve fazer busca exata")
        void deveBuscarNomeExato() throws Exception {
            assertEquals(List.of(ana), service.buscarFuncionario("ANA SOUZA", true));
            assertThrows(FuncionarioNaoEncontradoException.class,
                    () -> service.buscarFuncionario("Souza", true));
        }

        @Test
        @DisplayName("buscarFuncionario(String, false) deve fazer busca parcial")
        void deveBuscarNomeParcial() throws Exception {
            assertEquals(List.of(ana, carla), service.buscarFuncionario("souza", false));
            assertThrows(FuncionarioNaoEncontradoException.class,
                    () -> service.buscarFuncionario("Zé", false));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("buscarFuncionario(String, boolean) deve exigir o nome")
        void deveExigirNome(String nome) {
            assertThrows(DadoInvalidoException.class, () -> service.buscarFuncionario(nome, false));
        }
    }

    @Nested
    @DisplayName("Remoção")
    class Remocao {

        @Test
        @DisplayName("Deve remover funcionário existente")
        void deveRemover() throws Exception {
            cadastrarTodos();
            service.removerFuncionario(2);
            assertEquals(List.of(ana, carla), service.listarTodos());
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarFuncionario(2));
        }

        @Test
        @DisplayName("Deve lançar FuncionarioNaoEncontradoException ao remover inexistente")
        void deveFalharAoRemoverInexistente() {
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.removerFuncionario(5));
        }

        @Test
        @DisplayName("Deve permitir recadastrar o código após a remoção")
        void devePermitirRecadastro() throws Exception {
            service.cadastrar(ana);
            service.removerFuncionario(1);
            assertDoesNotThrow(() -> service.cadastrar(ana));
        }
    }

    @Nested
    @DisplayName("Cálculos e relatórios")
    class Calculos {

        @BeforeEach
        void popular() throws Exception {
            cadastrarTodos();
        }

        @Test
        @DisplayName("Deve calcular a remuneração de cada categoria pelo código")
        void deveCalcularRemuneracaoPorCodigo() throws Exception {
            assertEquals(3500, service.calcularRemuneracao(1), 0.001);
            assertEquals(6000, service.calcularRemuneracao(2), 0.001);
            assertEquals(2500, service.calcularRemuneracao(3), 0.001);
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.calcularRemuneracao(9));
        }

        @Test
        @DisplayName("Tributos: usa Tributavel quando implementado, senão zero")
        void deveCalcularTributos() {
            assertEquals(330, service.calcularTributos(ana), 0.001);
            assertEquals(0, service.calcularTributos(bruno), 0.001);
            assertEquals(275, service.calcularTributos(carla), 0.001);
        }

        @Test
        @DisplayName("Remuneração líquida = bruta − tributos")
        void deveCalcularLiquido() {
            assertEquals(3170, service.calcularRemuneracaoLiquida(ana), 0.001);
            assertEquals(6000, service.calcularRemuneracaoLiquida(bruno), 0.001);
            assertEquals(2225, service.calcularRemuneracaoLiquida(carla), 0.001);
        }

        @Test
        @DisplayName("Deve calcular o total da folha de pagamento (bruto)")
        void deveCalcularTotalFolha() {
            assertEquals(12000, service.calcularTotalFolha(), 0.001);
            assertEquals(605, service.calcularTotalTributos(), 0.001);
        }

        @Test
        @DisplayName("Total da folha deve ser zero sem funcionários")
        void totalDeveSerZeroSemFuncionarios() {
            FuncionarioService vazio = new FuncionarioService();
            assertEquals(0, vazio.calcularTotalFolha(), 0.001);
        }

        @Test
        @DisplayName("Deve exibir os dados completos de um funcionário")
        void deveExibirDados() throws Exception {
            String dados = service.exibirDados(3);
            assertTrue(dados.contains("Carla Souza"));
            assertTrue(dados.contains("Comissionado"));
            assertTrue(dados.contains("Tributos"));
            assertTrue(dados.contains("Remuneração líquida"));
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.exibirDados(77));
        }

        @Test
        @DisplayName("Relatório deve conter todos os funcionários, salários e total")
        void deveGerarRelatorio() {
            String relatorio = service.gerarRelatorio();
            assertAll(
                    () -> assertTrue(relatorio.contains("Ana Souza")),
                    () -> assertTrue(relatorio.contains("Bruno Lima")),
                    () -> assertTrue(relatorio.contains("Carla Souza")),
                    () -> assertTrue(relatorio.contains("3.500,00")),
                    () -> assertTrue(relatorio.contains("6.000,00")),
                    () -> assertTrue(relatorio.contains("2.500,00")),
                    () -> assertTrue(relatorio.contains("12.000,00")),
                    () -> assertTrue(relatorio.contains("Total de funcionários: 3"))
            );
        }

        @Test
        @DisplayName("Relatório vazio deve informar que não há funcionários")
        void deveGerarRelatorioVazio() {
            assertTrue(new FuncionarioService().gerarRelatorio().contains("Nenhum funcionário cadastrado"));
        }
    }
}
