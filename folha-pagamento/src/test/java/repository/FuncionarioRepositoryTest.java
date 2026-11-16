package repository;

import exception.DadoInvalidoException;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioRepositoryTest {

    private FuncionarioRepository repository;
    private Funcionario ana;
    private Funcionario bruno;
    private Funcionario carla;

    @BeforeEach
    void setUp() {
        repository = new FuncionarioRepository();
        ana = new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 3000, 500);
        bruno = new FuncionarioPJ(2, "Bruno Lima", "111.444.777-35", 2000, 3);
        carla = new FuncionarioComissionado(3, "Carla Souza", "123.456.789-09", 2000, 5, 10000);
    }

    @Test
    @DisplayName("Repositório novo deve estar vazio")
    void deveIniciarVazio() {
        assertTrue(repository.listarTodos().isEmpty());
        assertEquals(0, repository.contar());
    }

    @Test
    @DisplayName("Deve salvar e recuperar pelo código")
    void deveSalvarERecuperarPorCodigo() {
        repository.salvar(ana);
        Optional<Funcionario> encontrado = repository.buscarPorCodigo(1);
        assertTrue(encontrado.isPresent());
        assertSame(ana, encontrado.get());
        assertTrue(repository.existePorCodigo(1));
    }

    @Test
    @DisplayName("Busca por código inexistente deve retornar Optional vazio")
    void deveRetornarVazioParaCodigoInexistente() {
        assertTrue(repository.buscarPorCodigo(99).isEmpty());
        assertFalse(repository.existePorCodigo(99));
    }

    @Test
    @DisplayName("Deve buscar por CPF com ou sem máscara")
    void deveBuscarPorCpf() {
        repository.salvar(ana);
        assertSame(ana, repository.buscarPorCpf("529.982.247-25").orElseThrow());
        assertSame(ana, repository.buscarPorCpf("52998224725").orElseThrow());
        assertTrue(repository.existePorCpf("529.982.247-25"));
        assertTrue(repository.buscarPorCpf("111.444.777-35").isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por nome exato (ignorando maiúsculas) e parcial")
    void deveBuscarPorNome() {
        repository.salvar(ana);
        repository.salvar(bruno);
        repository.salvar(carla);

        assertEquals(List.of(ana), repository.buscarPorNome("ana souza", true));
        assertTrue(repository.buscarPorNome("Ana", true).isEmpty());
        assertEquals(List.of(ana, carla), repository.buscarPorNome("souza", false));
        assertEquals(List.of(bruno), repository.buscarPorNome("  LIMA ", false));
    }

    @Test
    @DisplayName("Deve listar todos na ordem de cadastro")
    void deveListarNaOrdemDeCadastro() {
        repository.salvar(carla);
        repository.salvar(ana);
        repository.salvar(bruno);
        assertEquals(List.of(carla, ana, bruno), repository.listarTodos());
        assertEquals(3, repository.contar());
    }

    @Test
    @DisplayName("A lista retornada não deve permitir alterar o armazenamento")
    void listaDeveSerSomenteLeitura() {
        repository.salvar(ana);
        List<Funcionario> lista = repository.listarTodos();
        assertThrows(UnsupportedOperationException.class, () -> lista.add(bruno));
        assertEquals(1, repository.contar());
    }

    @Test
    @DisplayName("Deve remover funcionário existente")
    void deveRemover() {
        repository.salvar(ana);
        assertTrue(repository.remover(1));
        assertTrue(repository.buscarPorCodigo(1).isEmpty());
        assertEquals(0, repository.contar());
    }

    @Test
    @DisplayName("Remover inexistente deve retornar false")
    void removerInexistenteDeveRetornarFalse() {
        assertFalse(repository.remover(42));
    }

    @Test
    @DisplayName("Não deve salvar funcionário nulo")
    void naoDeveSalvarNulo() {
        assertThrows(DadoInvalidoException.class, () -> repository.salvar(null));
    }
}
