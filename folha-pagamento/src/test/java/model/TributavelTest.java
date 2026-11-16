package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TributavelTest {

    @Test
    @DisplayName("CLT e Comissionado devem ser tributáveis; PJ não")
    void deveImplementarApenasOndeFazSentido() {
        assertTrue(Tributavel.class.isAssignableFrom(FuncionarioCLT.class));
        assertTrue(Tributavel.class.isAssignableFrom(FuncionarioComissionado.class));
        assertFalse(Tributavel.class.isAssignableFrom(FuncionarioPJ.class));
    }

    @Test
    @DisplayName("Cada classe tributável aplica sua própria regra de tributos")
    void deveCalcularTributosPolimorficamente() {
        Tributavel clt = new FuncionarioCLT(1, "Ana", "529.982.247-25", 3000, 500);
        Tributavel comissionado = new FuncionarioComissionado(2, "Carla", "123.456.789-09", 2000, 5, 10000);

        assertEquals(330, clt.calcularTributos(), 0.001);          // 11% de 3000 (benefícios isentos)
        assertEquals(275, comissionado.calcularTributos(), 0.001); // 11% de 2500
    }

    @Test
    @DisplayName("A alíquota padrão deve ser 11%")
    void deveTerAliquotaPadrao() {
        assertEquals(0.11, Tributavel.ALIQUOTA_PADRAO, 0.0001);
    }
}
