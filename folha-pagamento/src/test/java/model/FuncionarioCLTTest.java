package model;

import exception.CpfInvalidoException;
import exception.DadoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioCLTTest {

    private FuncionarioCLT clt;

    @BeforeEach
    void setUp() {
        clt = new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 3000, 500);
    }

    @Test
    @DisplayName("Remuneração = salário-base + adicional de benefícios")
    void deveCalcularRemuneracao() {
        assertEquals(3500, clt.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Remuneração sem benefícios é igual ao salário-base")
    void deveCalcularRemuneracaoSemBeneficios() {
        FuncionarioCLT semBeneficio = new FuncionarioCLT(2, "Rui", "111.444.777-35", 2000, 0);
        assertEquals(2000, semBeneficio.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Tributos = 11% do salário-base")
    void deveCalcularTributos() {
        assertEquals(330, clt.calcularTributos(), 0.001);
    }

    @Test
    @DisplayName("Remuneração deve refletir alterações via setters")
    void deveRecalcularAposAlteracao() {
        clt.setSalarioBase(4000);
        clt.setAdicionalBeneficios(1000);
        assertEquals(5000, clt.calcularRemuneracao(), 0.001);
        assertEquals(1000, clt.getAdicionalBeneficios(), 0.001);
    }

    @Test
    @DisplayName("Deve rejeitar benefícios negativos no construtor e no setter")
    void deveRejeitarBeneficiosNegativos() {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioCLT(2, "Rui", "111.444.777-35", 2000, -1));
        assertThrows(DadoInvalidoException.class, () -> clt.setAdicionalBeneficios(-50));
        assertEquals(500, clt.getAdicionalBeneficios(), 0.001);
    }

    @Test
    @DisplayName("Deve rejeitar salário-base negativo")
    void deveRejeitarSalarioNegativo() {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioCLT(2, "Rui", "111.444.777-35", -2000, 0));
    }

    @Test
    @DisplayName("Deve rejeitar CPF inválido")
    void deveRejeitarCpfInvalido() {
        assertThrows(CpfInvalidoException.class,
                () -> new FuncionarioCLT(2, "Rui", "000.000.000-00", 2000, 0));
    }

    @Test
    @DisplayName("Tipo e dados exibidos devem identificar o CLT")
    void deveExibirDados() {
        assertEquals("CLT", clt.getTipo());
        String dados = clt.exibirDados();
        assertTrue(dados.contains("Salário-base"));
        assertTrue(dados.contains("Benefícios"));
        assertTrue(dados.contains("3.500,00"));
    }
}
