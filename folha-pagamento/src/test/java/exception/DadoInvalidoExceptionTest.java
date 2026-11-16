package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DadoInvalidoExceptionTest {

    @Test
    @DisplayName("Deve guardar a mensagem informada")
    void deveGuardarMensagem() {
        DadoInvalidoException e = new DadoInvalidoException("Valor inválido");
        assertEquals("Valor inválido", e.getMessage());
    }

    @Test
    @DisplayName("Deve ser uma exceção não verificada (IllegalArgumentException)")
    void deveSerIllegalArgumentException() {
        assertInstanceOf(IllegalArgumentException.class, new DadoInvalidoException("x"));
    }

    @Test
    @DisplayName("Deve poder ser lançada e capturada")
    void devePoderSerLancada() {
        DadoInvalidoException e = assertThrows(DadoInvalidoException.class, () -> {
            throw new DadoInvalidoException("Salário negativo");
        });
        assertEquals("Salário negativo", e.getMessage());
    }
}
