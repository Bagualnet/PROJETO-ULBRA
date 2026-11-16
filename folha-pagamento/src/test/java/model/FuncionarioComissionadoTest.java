package model;

import exception.DadoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioComissionadoTest {

    private FuncionarioComissionado comissionado;

    @BeforeEach
    void setUp() {
        comissionado = new FuncionarioComissionado(1, "Carla Dias", "123.456.789-09", 2000, 5, 10000);
    }

    @Test
    @DisplayName("Comissão = vendas × percentual / 100")
    void deveCalcularComissao() {
        assertEquals(500, comissionado.calcularComissao(), 0.001);
    }

    @Test
    @DisplayName("Remuneração = salário-base + comissão")
    void deveCalcularRemuneracao() {
        assertEquals(2500, comissionado.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Sem vendas, a remuneração é o salário-base")
    void deveCalcularSemVendas() {
        FuncionarioComissionado semVendas =
                new FuncionarioComissionado(2, "Rui", "529.982.247-25", 1800, 10, 0);
        assertEquals(1800, semVendas.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Tributos = 11% da remuneração total")
    void deveCalcularTributos() {
        assertEquals(275, comissionado.calcularTributos(), 0.001);
    }

    @Test
    @DisplayName("Deve aceitar os limites 0% e 100%")
    void deveAceitarLimitesDoPercentual() {
        comissionado.setPercentualComissao(0);
        assertEquals(2000, comissionado.calcularRemuneracao(), 0.001);
        comissionado.setPercentualComissao(100);
        assertEquals(12000, comissionado.calcularRemuneracao(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, -5, 100.01, 150, Double.NaN})
    @DisplayName("Deve rejeitar percentual fora do intervalo 0–100")
    void deveRejeitarPercentualInvalido(double percentual) {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioComissionado(2, "Rui", "529.982.247-25", 1000, percentual, 0));
        assertThrows(DadoInvalidoException.class, () -> comissionado.setPercentualComissao(percentual));
        assertEquals(5, comissionado.getPercentualComissao(), 0.001);
    }

    @Test
    @DisplayName("Deve rejeitar total de vendas negativo")
    void deveRejeitarVendasNegativas() {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioComissionado(2, "Rui", "529.982.247-25", 1000, 5, -1));
        assertThrows(DadoInvalidoException.class, () -> comissionado.setTotalVendas(-100));
    }

    @Test
    @DisplayName("Deve acumular vendas registradas")
    void deveRegistrarVenda() {
        comissionado.registrarVenda(2000);
        assertEquals(12000, comissionado.getTotalVendas(), 0.001);
        assertEquals(600, comissionado.calcularComissao(), 0.001);
        assertThrows(DadoInvalidoException.class, () -> comissionado.registrarVenda(-1));
    }

    @Test
    @DisplayName("Tipo e dados exibidos devem identificar o comissionado")
    void deveExibirDados() {
        assertEquals("Comissionado", comissionado.getTipo());
        String dados = comissionado.exibirDados();
        assertTrue(dados.contains("Percentual de comissão: 5,00%"));
        assertTrue(dados.contains("Total de vendas"));
        assertTrue(dados.contains("2.500,00"));
    }
}
