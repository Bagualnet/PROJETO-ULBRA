package model;

import exception.CpfInvalidoException;
import exception.DadoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as regras comuns da classe abstrata {@link Funcionario} e o
 * polimorfismo por sobreposição entre as subclasses.
 */
class FuncionarioTest {

    private static final String CPF_VALIDO = "529.982.247-25";

    /** Implementação mínima apenas para testar a classe abstrata. */
    private static class FuncionarioFake extends Funcionario {
        FuncionarioFake(int codigo, String nome, String cpf, double salarioBase) {
            super(codigo, nome, cpf, salarioBase);
        }

        @Override
        public double calcularRemuneracao() {
            return getSalarioBase();
        }

        @Override
        public String getTipo() {
            return "Fake";
        }

        @Override
        protected String descreverRemuneracao() {
            return "Detalhe fake";
        }
    }

    @Nested
    @DisplayName("Construção e validação")
    class Validacao {

        @Test
        @DisplayName("Deve criar funcionário com dados válidos")
        void deveCriarComDadosValidos() {
            Funcionario f = new FuncionarioFake(1, "  Ana Souza ", CPF_VALIDO, 2500);
            assertEquals(1, f.getCodigo());
            assertEquals("Ana Souza", f.getNome());
            assertEquals("52998224725", f.getCpf());
            assertEquals("529.982.247-25", f.getCpfFormatado());
            assertEquals(2500, f.getSalarioBase(), 0.001);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("Deve rejeitar código não positivo")
        void deveRejeitarCodigoInvalido(int codigo) {
            assertThrows(DadoInvalidoException.class,
                    () -> new FuncionarioFake(codigo, "Ana", CPF_VALIDO, 1000));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Deve rejeitar nome não informado")
        void deveRejeitarNomeVazio(String nome) {
            assertThrows(DadoInvalidoException.class,
                    () -> new FuncionarioFake(1, nome, CPF_VALIDO, 1000));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Deve rejeitar CPF não informado")
        void deveRejeitarCpfVazio(String cpf) {
            assertThrows(DadoInvalidoException.class,
                    () -> new FuncionarioFake(1, "Ana", cpf, 1000));
        }

        @Test
        @DisplayName("Deve lançar CpfInvalidoException para CPF inválido")
        void deveRejeitarCpfInvalido() {
            CpfInvalidoException e = assertThrows(CpfInvalidoException.class,
                    () -> new FuncionarioFake(1, "Ana", "123.456.789-00", 1000));
            assertEquals("123.456.789-00", e.getCpf());
        }

        @Test
        @DisplayName("Deve rejeitar salário-base negativo")
        void deveRejeitarSalarioNegativo() {
            assertThrows(DadoInvalidoException.class,
                    () -> new FuncionarioFake(1, "Ana", CPF_VALIDO, -0.01));
        }

        @Test
        @DisplayName("Deve rejeitar salário-base NaN ou infinito")
        void deveRejeitarSalarioNaoNumerico() {
            assertThrows(DadoInvalidoException.class,
                    () -> new FuncionarioFake(1, "Ana", CPF_VALIDO, Double.NaN));
            assertThrows(DadoInvalidoException.class,
                    () -> new FuncionarioFake(1, "Ana", CPF_VALIDO, Double.POSITIVE_INFINITY));
        }
    }

    @Nested
    @DisplayName("Setters com validação")
    class Setters {

        @Test
        @DisplayName("Deve alterar nome e salário-base válidos")
        void deveAlterarValoresValidos() {
            Funcionario f = new FuncionarioFake(1, "Ana", CPF_VALIDO, 1000);
            f.setNome("Ana Lima");
            f.setSalarioBase(1500);
            assertEquals("Ana Lima", f.getNome());
            assertEquals(1500, f.getSalarioBase(), 0.001);
        }

        @Test
        @DisplayName("Não deve alterar o estado quando o setter recebe valor inválido")
        void naoDeveAlterarComValorInvalido() {
            Funcionario f = new FuncionarioFake(1, "Ana", CPF_VALIDO, 1000);
            assertThrows(DadoInvalidoException.class, () -> f.setNome(""));
            assertThrows(DadoInvalidoException.class, () -> f.setSalarioBase(-10));
            assertEquals("Ana", f.getNome());
            assertEquals(1000, f.getSalarioBase(), 0.001);
        }
    }

    @Nested
    @DisplayName("Identidade e exibição")
    class Identidade {

        @Test
        @DisplayName("Funcionários com o mesmo código devem ser iguais")
        void deveCompararPeloCodigo() {
            Funcionario a = new FuncionarioFake(1, "Ana", CPF_VALIDO, 1000);
            Funcionario b = new FuncionarioFake(1, "Outra", "111.444.777-35", 2000);
            Funcionario c = new FuncionarioFake(2, "Ana", CPF_VALIDO, 1000);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
        }

        @Test
        @DisplayName("exibirDados deve conter as informações completas")
        void deveExibirDadosCompletos() {
            Funcionario f = new FuncionarioFake(7, "Ana", CPF_VALIDO, 1000);
            String dados = f.exibirDados();
            assertAll(
                    () -> assertTrue(dados.contains("Código: 7")),
                    () -> assertTrue(dados.contains("Tipo: Fake")),
                    () -> assertTrue(dados.contains("Nome: Ana")),
                    () -> assertTrue(dados.contains("CPF: 529.982.247-25")),
                    () -> assertTrue(dados.contains("Detalhe fake")),
                    () -> assertTrue(dados.contains("1.000,00"))
            );
        }

        @Test
        @DisplayName("toString deve apresentar tipo, código e nome")
        void deveTerToStringLegivel() {
            Funcionario f = new FuncionarioFake(7, "Ana", CPF_VALIDO, 1000);
            assertEquals("[Fake] #7 - Ana", f.toString());
        }
    }

    @Test
    @DisplayName("Polimorfismo: cada subclasse executa sua própria regra de remuneração")
    void deveExecutarImplementacaoDoObjetoReal() {
        Funcionario funcionario1 = new FuncionarioCLT(1, "Ana", "529.982.247-25", 3000, 500);
        Funcionario funcionario2 = new FuncionarioPJ(2, "Bruno", "111.444.777-35", 2000, 3);
        Funcionario funcionario3 = new FuncionarioComissionado(3, "Carla", "123.456.789-09", 2000, 5, 10000);

        assertEquals(3500, funcionario1.calcularRemuneracao(), 0.001);
        assertEquals(6000, funcionario2.calcularRemuneracao(), 0.001);
        assertEquals(2500, funcionario3.calcularRemuneracao(), 0.001);

        List<Funcionario> lista = List.of(funcionario1, funcionario2, funcionario3);
        double total = 0;
        for (Funcionario f : lista) {
            total += f.calcularRemuneracao(); // chamada polimórfica
        }
        assertEquals(12000, total, 0.001);
        assertEquals(List.of("CLT", "PJ", "Comissionado"), lista.stream().map(Funcionario::getTipo).toList());
    }
}
