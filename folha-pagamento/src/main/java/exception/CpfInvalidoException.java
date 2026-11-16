package exception;

import java.io.Serial;

/**
 * Lançada quando o CPF informado não é válido (formato ou dígitos verificadores).
 */
public class CpfInvalidoException extends DadoInvalidoException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String cpf;

    public CpfInvalidoException(String cpf) {
        super("CPF inválido: " + cpf);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }
}
