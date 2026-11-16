package util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formata valores monetários no padrão brasileiro (R$ 1.234,56).
 */
public final class FormatadorMoeda {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private FormatadorMoeda() {
        // classe utilitária
    }

    public static String formatar(double valor) {
        // NumberFormat não é thread-safe, por isso uma instância nova por chamada.
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor).replace(' ', ' ');
    }
}
