package model;

import exception.CpfInvalidoException;
import exception.DadoInvalidoException;
import util.FormatadorMoeda;
import util.ValidadorCPF;

import java.util.Objects;

/**
 * Classe base abstrata de todos os funcionários.
 * <p>
 * Concentra os dados comuns (código, nome, CPF e salário-base), as validações
 * e declara o método abstrato {@link #calcularRemuneracao()}, que cada subclasse
 * sobrescreve com sua própria regra (polimorfismo por sobreposição).
 */
public abstract class Funcionario implements Identificavel {

    private final int codigo;
    private final String cpf;
    private String nome;
    private double salarioBase;

    protected Funcionario(int codigo, String nome, String cpf, double salarioBase) {
        if (codigo <= 0) {
            throw new DadoInvalidoException("O código identificador deve ser um número inteiro positivo.");
        }
        this.codigo = codigo;
        this.cpf = validarCpf(cpf);
        setNome(nome);
        setSalarioBase(salarioBase);
    }

    /**
     * Calcula a remuneração bruta do funcionário de acordo com sua categoria.
     */
    public abstract double calcularRemuneracao();

    /**
     * @return nome da categoria do funcionário (ex.: "CLT")
     */
    public abstract String getTipo();

    /**
     * @return as linhas de detalhamento da remuneração, específicas de cada categoria
     */
    protected abstract String descreverRemuneracao();

    @Override
    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) {
        this.nome = exigirTexto(nome, "nome");
    }

    /** @return o CPF somente com dígitos */
    public String getCpf() {
        return cpf;
    }

    /** @return o CPF no formato 000.000.000-00 */
    public String getCpfFormatado() {
        return ValidadorCPF.formatar(cpf);
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public final void setSalarioBase(double salarioBase) {
        this.salarioBase = exigirNaoNegativo(salarioBase, "salário-base");
    }

    /**
     * @return texto com os dados completos do funcionário
     */
    public String exibirDados() {
        return "Código: " + codigo + System.lineSeparator()
                + "Tipo: " + getTipo() + System.lineSeparator()
                + "Nome: " + nome + System.lineSeparator()
                + "CPF: " + getCpfFormatado() + System.lineSeparator()
                + descreverRemuneracao() + System.lineSeparator()
                + "Remuneração bruta: " + FormatadorMoeda.formatar(calcularRemuneracao());
    }

    // ----------------------------------------------------------------- validações

    /**
     * Garante que o valor é um número finito e não negativo.
     *
     * @throws DadoInvalidoException se o valor for negativo, NaN ou infinito
     */
    protected static double exigirNaoNegativo(double valor, String campo) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadoInvalidoException("O valor de " + campo + " deve ser um número válido.");
        }
        if (valor < 0) {
            throw new DadoInvalidoException("O valor de " + campo + " não pode ser negativo.");
        }
        return valor;
    }

    /**
     * Garante que o texto obrigatório foi informado.
     *
     * @throws DadoInvalidoException se o texto for nulo ou vazio
     */
    protected static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException("O campo " + campo + " é obrigatório.");
        }
        return valor.trim();
    }

    private static String validarCpf(String cpf) {
        exigirTexto(cpf, "CPF");
        if (!ValidadorCPF.isValido(cpf)) {
            throw new CpfInvalidoException(cpf);
        }
        return ValidadorCPF.normalizar(cpf);
    }

    // ----------------------------------------------------------------- Object

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Funcionario outro)) return false;
        return codigo == outro.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "[" + getTipo() + "] #" + codigo + " - " + nome;
    }
}
