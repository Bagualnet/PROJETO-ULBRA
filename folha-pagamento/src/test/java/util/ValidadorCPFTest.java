package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorCPFTest {

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-25", "52998224725", "111.444.777-35", "123.456.789-09"})
    @DisplayName("Deve aceitar CPFs válidos, com ou sem máscara")
    void deveAceitarCpfsValidos(String cpf) {
        assertTrue(ValidadorCPF.isValido(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "529.982.247-24",   // dígito verificador errado
            "111.111.111-11",   // todos os dígitos iguais
            "123",              // tamanho inválido
            "529982247251",     // dígitos demais
            "529.982.247-2a",   // caractere inválido
            "abc.def.ghi-jk"
    })
    @DisplayName("Deve rejeitar CPFs inválidos")
    void deveRejeitarCpfsInvalidos(String cpf) {
        assertFalse(ValidadorCPF.isValido(cpf));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Deve rejeitar CPF nulo ou vazio")
    void deveRejeitarCpfNuloOuVazio(String cpf) {
        assertFalse(ValidadorCPF.isValido(cpf));
    }

    @Test
    @DisplayName("Deve remover a máscara do CPF")
    void deveNormalizarCpf() {
        assertEquals("52998224725", ValidadorCPF.normalizar("529.982.247-25"));
        assertEquals("", ValidadorCPF.normalizar(null));
    }

    @Test
    @DisplayName("Deve formatar CPF com 11 dígitos")
    void deveFormatarCpf() {
        assertEquals("529.982.247-25", ValidadorCPF.formatar("52998224725"));
        assertEquals("529.982.247-25", ValidadorCPF.formatar("529.982.247-25"));
    }

    @Test
    @DisplayName("Não deve formatar valor que não tem 11 dígitos")
    void naoDeveFormatarValorInvalido() {
        assertEquals("123", ValidadorCPF.formatar("123"));
    }
}
