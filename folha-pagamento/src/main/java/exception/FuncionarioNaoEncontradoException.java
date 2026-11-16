package exception;

import java.io.Serial;

/**
 * Lançada quando uma busca ou remoção não encontra o funcionário solicitado.
 * <p>
 * É uma exceção verificada: quem chama o serviço é obrigado a tratá-la.
 */
public class FuncionarioNaoEncontradoException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public FuncionarioNaoEncontradoException(int codigo) {
        super("Funcionário com código " + codigo + " não encontrado.");
    }

    public FuncionarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
