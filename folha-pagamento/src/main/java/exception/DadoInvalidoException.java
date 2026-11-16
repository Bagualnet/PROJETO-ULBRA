package exception;

import java.io.Serial;

/**
 * Lançada quando um dado obrigatório não é informado ou quando um valor é inválido
 * (ex.: salário negativo, percentual fora do intervalo, código não positivo).
 * <p>
 * É uma exceção não verificada, pois representa erro de entrada detectado na
 * validação de construtores e setters.
 */
public class DadoInvalidoException extends IllegalArgumentException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DadoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
