package model;

import exception.DadoInvalidoException;
import util.FormatadorMoeda;

import java.util.Locale;

/**
 * Funcionário que recebe salário-base mais comissão sobre suas vendas.
 * <p>
 * Comissão = total de vendas × percentual / 100.<br>
 * Remuneração = salário-base + comissão.<br>
 * Tributos = 11% sobre a remuneração total.
 */
public class FuncionarioComissionado extends Funcionario implements Tributavel {

    private double percentualComissao;
    private double totalVendas;

    public FuncionarioComissionado(int codigo, String nome, String cpf, double salarioBase,
                                   double percentualComissao, double totalVendas) {
        super(codigo, nome, cpf, salarioBase);
        setPercentualComissao(percentualComissao);
        setTotalVendas(totalVendas);
    }

    public double calcularComissao() {
        return totalVendas * percentualComissao / 100.0;
    }

    @Override
    public double calcularRemuneracao() {
        return getSalarioBase() + calcularComissao();
    }

    @Override
    public double calcularTributos() {
        return calcularRemuneracao() * ALIQUOTA_PADRAO;
    }

    @Override
    public String getTipo() {
        return "Comissionado";
    }

    @Override
    protected String descreverRemuneracao() {
        return "Salário-base: " + FormatadorMoeda.formatar(getSalarioBase()) + System.lineSeparator()
                + "Percentual de comissão: "
                + String.format(Locale.forLanguageTag("pt-BR"), "%.2f%%", percentualComissao) + System.lineSeparator()
                + "Total de vendas: " + FormatadorMoeda.formatar(totalVendas) + System.lineSeparator()
                + "Comissão: " + FormatadorMoeda.formatar(calcularComissao());
    }

    /**
     * Soma uma nova venda ao total de vendas do funcionário.
     */
    public void registrarVenda(double valor) {
        setTotalVendas(totalVendas + exigirNaoNegativo(valor, "venda"));
    }

    public double getPercentualComissao() {
        return percentualComissao;
    }

    public final void setPercentualComissao(double percentualComissao) {
        if (Double.isNaN(percentualComissao) || percentualComissao < 0 || percentualComissao > 100) {
            throw new DadoInvalidoException("O percentual de comissão deve estar entre 0 e 100.");
        }
        this.percentualComissao = percentualComissao;
    }

    public double getTotalVendas() {
        return totalVendas;
    }

    public final void setTotalVendas(double totalVendas) {
        this.totalVendas = exigirNaoNegativo(totalVendas, "total de vendas");
    }
}
