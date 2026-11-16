package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioNaoEncontradoExceptionTest {

    @Test
    @DisplayName("Deve montar mensagem a partir do código")
    void deveMontarMensagemPeloCodigo() {
        FuncionarioNaoEncontradoException e = new FuncionarioNaoEncontradoException(42);
        assertEquals("Funcionário com código 42 não encontrado.", e.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar mensagem personalizada")
    void deveAceitarMensagemPersonalizada() {
        FuncionarioNaoEncontradoException e =
                new FuncionarioNaoEncontradoException("Nenhum funcionário com o CPF informado.");
        assertEquals("Nenhum funcionário com o CPF informado.", e.getMessage());
    }

    @Test
    @DisplayName("Deve ser uma exceção verificada (checked)")
    void deveSerChecked() {
        Exception e = new FuncionarioNaoEncontradoException(1);
        assertFalse(e instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve poder ser lançada e capturada")
    void devePoderSerLancada() {
        assertThrows(FuncionarioNaoEncontradoException.class, () -> {
            throw new FuncionarioNaoEncontradoException(7);
        });
    }
}
