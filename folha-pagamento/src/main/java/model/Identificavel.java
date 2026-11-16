package model;

/**
 * Representa qualquer entidade que possui um código identificador único.
 */
@FunctionalInterface
public interface Identificavel {

    /**
     * @return o código identificador da entidade
     */
    int getCodigo();
}
