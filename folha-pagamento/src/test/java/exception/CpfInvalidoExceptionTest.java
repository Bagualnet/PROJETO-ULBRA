package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfInvalidoExceptionTest {

    @Test
    @DisplayName("Deve montar mensagem com o CPF inválido")
    void deveMontarMensagemComCpf() {
        CpfInvalidoException e = new CpfInvalidoException("123.456.789-00");
        assertEquals("CPF inválido: 123.456.789-00", e.getMessage());
        assertEquals("123.456.789-00", e.getCpf());
    }

    @Test
    @DisplayName("Deve ser um tipo de DadoInvalidoException")
    void deveSerDadoInvalido() {
        assertInstanceOf(DadoInvalidoException.class, new CpfInvalidoException("1"));
    }

    @Test
    @DisplayName("Deve poder ser lançada e capturada como DadoInvalidoException")
    void devePoderSerCapturadaPelaSuperclasse() {
        assertThrows(DadoInvalidoException.class, () -> {
            throw new CpfInvalidoException("000");
        });
    }
}
