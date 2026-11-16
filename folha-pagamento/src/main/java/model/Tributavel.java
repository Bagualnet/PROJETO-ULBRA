package model;

/**
 * Representa uma remuneração sobre a qual incidem tributos/descontos retidos pela empresa.
 * <p>
 * Implementada por {@link FuncionarioCLT} e {@link FuncionarioComissionado}.
 * {@link FuncionarioPJ} não implementa, pois o prestador PJ emite nota fiscal e
 * recolhe os próprios tributos.
 */
public interface Tributavel {

    /** Alíquota simplificada de desconto (referência: INSS), usada para fins didáticos. */
    double ALIQUOTA_PADRAO = 0.11;

    /**
     * @return o valor total de tributos/descontos aplicáveis à remuneração
     */
    double calcularTributos();
}
