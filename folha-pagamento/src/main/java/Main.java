import exception.FuncionarioJaCadastradoException;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import service.FuncionarioService;
import view.FuncionarioView;

import java.util.Scanner;

/**
 * Ponto de entrada da aplicação: monta as camadas e inicia o menu.
 * <p>
 * Use o argumento {@code --vazio} para iniciar sem os funcionários de exemplo.
 */
public class Main {

    public static void main(String[] args) {
        FuncionarioService service = new FuncionarioService();

        boolean carregarExemplos = args.length == 0 || !args[0].equals("--vazio");
        if (carregarExemplos) {
            carregarDadosDeExemplo(service);
        }

        new FuncionarioView(service, new Scanner(System.in), System.out).iniciar();
    }

    private static void carregarDadosDeExemplo(FuncionarioService service) {
        Funcionario[] exemplos = {
                new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 3000, 500),
                new FuncionarioPJ(2, "Bruno Lima", "111.444.777-35", 2000, 3),
                new FuncionarioComissionado(3, "Carla Dias", "123.456.789-09", 2000, 5, 10000)
        };
        for (Funcionario f : exemplos) {
            try {
                service.cadastrar(f);
            } catch (FuncionarioJaCadastradoException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        }
        System.out.println("Funcionários de exemplo carregados (códigos 1, 2 e 3).");
    }
}
