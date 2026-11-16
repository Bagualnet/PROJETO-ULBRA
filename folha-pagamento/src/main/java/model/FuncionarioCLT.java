package model;

import util.FormatadorMoeda;

/**
 * Funcionário contratado pelo regime CLT.
 * <p>
 * Remuneração = salário-base + adicional de benefícios.<br>
 * Tributos = 11% sobre o salário-base (benefícios não sofrem incidência).
 */
public class FuncionarioCLT extends Funcionario implements Tributavel {

    private double adicionalBeneficios;

    public FuncionarioCLT(int codigo, String nome, String cpf, double salarioBase, double adicionalBeneficios) {
        super(codigo, nome, cpf, salarioBase);
        setAdicionalBeneficios(adicionalBeneficios);
    }

    @Override
    public double calcularRemuneracao() {
        return getSalarioBase() + adicionalBeneficios;
    }

    @Override
    public double calcularTributos() {
        return getSalarioBase() * ALIQUOTA_PADRAO;
    }

    @Override
    public String getTipo() {
        return "CLT";
    }

    @Override
    protected String descreverRemuneracao() {
        return "Salário-base: " + FormatadorMoeda.formatar(getSalarioBase()) + System.lineSeparator()
                + "Benefícios: " + FormatadorMoeda.formatar(adicionalBeneficios);
    }

    public double getAdicionalBeneficios() {
        return adicionalBeneficios;
    }

    public final void setAdicionalBeneficios(double adicionalBeneficios) {
        this.adicionalBeneficios = exigirNaoNegativo(adicionalBeneficios, "adicional de benefícios");
    }
}
