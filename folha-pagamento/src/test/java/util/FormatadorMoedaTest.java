package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatadorMoedaTest {

    @Test
    @DisplayName("Deve formatar valores no padrão brasileiro (R$ 1.234,56)")
    void deveFormatarNoPadraoBrasileiro() {
        String formatado = FormatadorMoeda.formatar(1234.56);
        assertTrue(formatado.startsWith("R$"));
        assertTrue(formatado.endsWith("1.234,56"));
    }

    @Test
    @DisplayName("Deve formatar zero com duas casas decimais")
    void deveFormatarZero() {
        assertTrue(FormatadorMoeda.formatar(0).endsWith("0,00"));
    }

    @Test
    @DisplayName("Deve arredondar para duas casas decimais")
    void deveArredondar() {
        assertTrue(FormatadorMoeda.formatar(10.126).endsWith("10,13"));
        assertTrue(FormatadorMoeda.formatar(99.999).endsWith("100,00"));
    }
}
