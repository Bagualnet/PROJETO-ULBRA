package model;

import exception.DadoInvalidoException;
import util.FormatadorMoeda;

/**
 * Prestador de serviço contratado como Pessoa Jurídica.
 * <p>
 * Remuneração = valor por projeto × quantidade de projetos realizados.<br>
 * Não possui salário-base (fica zerado) e não implementa {@link Tributavel},
 * pois os tributos são recolhidos pela própria empresa contratada.
 */
public class FuncionarioPJ extends Funcionario {

    private double valorPorProjeto;
    private int quantidadeProjetos;

    public FuncionarioPJ(int codigo, String nome, String cpf, double valorPorProjeto, int quantidadeProjetos) {
        super(codigo, nome, cpf, 0);
        setValorPorProjeto(valorPorProjeto);
        setQuantidadeProjetos(quantidadeProjetos);
    }

    @Override
    public double calcularRemuneracao() {
        return valorPorProjeto * quantidadeProjetos;
    }

    @Override
    public String getTipo() {
        return "PJ";
    }

    @Override
    protected String descreverRemuneracao() {
        return "Valor por projeto: " + FormatadorMoeda.formatar(valorPorProjeto) + System.lineSeparator()
                + "Projetos realizados: " + quantidadeProjetos;
    }

    public double getValorPorProjeto() {
        return valorPorProjeto;
    }

    public final void setValorPorProjeto(double valorPorProjeto) {
        this.valorPorProjeto = exigirNaoNegativo(valorPorProjeto, "valor por projeto");
    }

    public int getQuantidadeProjetos() {
        return quantidadeProjetos;
    }

    public final void setQuantidadeProjetos(int quantidadeProjetos) {
        if (quantidadeProjetos < 0) {
            throw new DadoInvalidoException("A quantidade de projetos não pode ser negativa.");
        }
        this.quantidadeProjetos = quantidadeProjetos;
    }
}
