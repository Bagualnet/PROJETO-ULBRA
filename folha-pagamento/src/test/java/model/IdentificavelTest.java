package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdentificavelTest {

    @Test
    @DisplayName("Todas as classes concretas devem ser Identificavel (por herança)")
    void todasAsClassesDevemSerIdentificaveis() {
        assertTrue(Identificavel.class.isAssignableFrom(Funcionario.class));
        assertTrue(Identificavel.class.isAssignableFrom(FuncionarioCLT.class));
        assertTrue(Identificavel.class.isAssignableFrom(FuncionarioPJ.class));
        assertTrue(Identificavel.class.isAssignableFrom(FuncionarioComissionado.class));
    }

    @Test
    @DisplayName("getCodigo deve retornar o identificador de cada funcionário")
    void deveRetornarIdentificador() {
        List<Identificavel> itens = List.of(
                new FuncionarioCLT(10, "Ana", "529.982.247-25", 3000, 0),
                new FuncionarioPJ(20, "Bruno", "111.444.777-35", 1000, 1),
                new FuncionarioComissionado(30, "Carla", "123.456.789-09", 1000, 1, 0)
        );
        assertEquals(List.of(10, 20, 30), itens.stream().map(Identificavel::getCodigo).toList());
    }

    @Test
    @DisplayName("Interface deve poder ser implementada por qualquer classe")
    void devePermitirImplementacaoLambda() {
        Identificavel id = () -> 99;
        assertEquals(99, id.getCodigo());
    }
}
