package util;

import java.util.regex.Pattern;

/**
 * Utilitário para validação, normalização e formatação de CPF.
 * <p>
 * A validação considera o formato (com ou sem máscara) e os dois dígitos verificadores.
 */
public final class ValidadorCPF {

    private static final Pattern FORMATO = Pattern.compile("^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$");

    private ValidadorCPF() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Verifica se o CPF informado é válido.
     *
     * @param cpf CPF com ou sem máscara (ex.: 529.982.247-25 ou 52998224725)
     * @return {@code true} se o CPF for válido
     */
    public static boolean isValido(String cpf) {
        if (cpf == null || !FORMATO.matcher(cpf.trim()).matches()) {
            return false;
        }
        String digitos = normalizar(cpf);
        if (digitos.chars().distinct().count() == 1) {
            return false; // ex.: 111.111.111-11
        }
        return calcularDigito(digitos, 9) == digitos.charAt(9) - '0'
                && calcularDigito(digitos, 10) == digitos.charAt(10) - '0';
    }

    /**
     * Remove todos os caracteres que não são dígitos.
     */
    public static String normalizar(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

    /**
     * Formata o CPF no padrão 000.000.000-00. Se não houver 11 dígitos, devolve o valor original.
     */
    public static String formatar(String cpf) {
        String d = normalizar(cpf);
        if (d.length() != 11) {
            return cpf;
        }
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    private static int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (digitos.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
