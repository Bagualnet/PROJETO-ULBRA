package exception;

import java.io.Serial;

/**
 * Lançada ao tentar cadastrar um funcionário cujo código (ou CPF) já existe.
 * <p>
 * É uma exceção verificada: quem chama o serviço é obrigado a tratá-la.
 */
public class FuncionarioJaCadastradoException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public FuncionarioJaCadastradoException(int codigo) {
        super("Já existe um funcionário cadastrado com o código " + codigo + ".");
    }

    public FuncionarioJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
