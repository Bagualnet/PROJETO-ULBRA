package model;

import exception.DadoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioPJTest {

    private FuncionarioPJ pj;

    @BeforeEach
    void setUp() {
        pj = new FuncionarioPJ(1, "Bruno Lima", "111.444.777-35", 2000, 3);
    }

    @Test
    @DisplayName("Remuneração = valor por projeto × quantidade de projetos")
    void deveCalcularRemuneracao() {
        assertEquals(6000, pj.calcularRemuneracao(), 0.001);
    }

    @ParameterizedTest(name = "{0} × {1} = {2}")
    @CsvSource({"1500, 0, 0", "1500, 1, 1500", "1250.50, 4, 5002", "0, 10, 0"})
    @DisplayName("Deve calcular remuneração para diferentes quantidades")
    void deveCalcularParaDiferentesValores(double valor, int quantidade, double esperado) {
        FuncionarioPJ outro = new FuncionarioPJ(2, "Rui", "529.982.247-25", valor, quantidade);
        assertEquals(esperado, outro.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("PJ não possui salário-base (fica zerado)")
    void naoDeveTerSalarioBase() {
        assertEquals(0, pj.getSalarioBase(), 0.001);
    }

    @Test
    @DisplayName("Deve permitir registrar novos projetos")
    void deveAlterarValores() {
        pj.setQuantidadeProjetos(5);
        pj.setValorPorProjeto(1000);
        assertEquals(5, pj.getQuantidadeProjetos());
        assertEquals(1000, pj.getValorPorProjeto(), 0.001);
        assertEquals(5000, pj.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Deve rejeitar valor por projeto negativo")
    void deveRejeitarValorNegativo() {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioPJ(2, "Rui", "529.982.247-25", -100, 1));
        assertThrows(DadoInvalidoException.class, () -> pj.setValorPorProjeto(-1));
    }

    @Test
    @DisplayName("Deve rejeitar quantidade de projetos negativa")
    void deveRejeitarQuantidadeNegativa() {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioPJ(2, "Rui", "529.982.247-25", 100, -1));
        assertThrows(DadoInvalidoException.class, () -> pj.setQuantidadeProjetos(-3));
        assertEquals(3, pj.getQuantidadeProjetos());
    }

    @Test
    @DisplayName("PJ não é Tributavel (os tributos são de responsabilidade da empresa contratada)")
    void naoDeveSerTributavel() {
        assertFalse(pj instanceof Tributavel);
    }

    @Test
    @DisplayName("Tipo e dados exibidos devem identificar o PJ")
    void deveExibirDados() {
        assertEquals("PJ", pj.getTipo());
        String dados = pj.exibirDados();
        assertTrue(dados.contains("Valor por projeto"));
        assertTrue(dados.contains("Projetos realizados: 3"));
        assertTrue(dados.contains("6.000,00"));
    }
}
