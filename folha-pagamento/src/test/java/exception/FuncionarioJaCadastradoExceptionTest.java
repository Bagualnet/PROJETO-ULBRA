package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioJaCadastradoExceptionTest {

    @Test
    @DisplayName("Deve montar mensagem a partir do código duplicado")
    void deveMontarMensagemPeloCodigo() {
        FuncionarioJaCadastradoException e = new FuncionarioJaCadastradoException(10);
        assertEquals("Já existe um funcionário cadastrado com o código 10.", e.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem personalizada")
    void deveAceitarMensagemPersonalizada() {
        FuncionarioJaCadastradoException e = new FuncionarioJaCadastradoException("CPF duplicado");
        assertEquals("CPF duplicado", e.getMessage());
    }

    @Test
    @DisplayName("Deve ser uma exceção verificada (checked)")
    void deveSerChecked() {
        Exception e = new FuncionarioJaCadastradoException(1);
        assertFalse(e instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve poder ser lançada e capturada")
    void devePoderSerLancada() {
        assertThrows(FuncionarioJaCadastradoException.class, () -> {
            throw new FuncionarioJaCadastradoException(3);
        });
    }
}
