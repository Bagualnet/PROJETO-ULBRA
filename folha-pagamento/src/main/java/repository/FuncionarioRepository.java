package repository;

import exception.DadoInvalidoException;
import model.Funcionario;
import util.ValidadorCPF;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Armazena e recupera funcionários em memória.
 * <p>
 * Usa um {@link LinkedHashMap} indexado pelo código, o que mantém a ordem de cadastro
 * e permite busca por código em tempo constante. Esta camada não aplica regras de
 * negócio (como duplicidade); isso é responsabilidade do serviço.
 */
public class FuncionarioRepository {

    private final Map<Integer, Funcionario> funcionarios = new LinkedHashMap<>();

    public void salvar(Funcionario funcionario) {
        if (funcionario == null) {
            throw new DadoInvalidoException("O funcionário é obrigatório.");
        }
        funcionarios.put(funcionario.getCodigo(), funcionario);
    }

    public Optional<Funcionario> buscarPorCodigo(int codigo) {
        return Optional.ofNullable(funcionarios.get(codigo));
    }

    public Optional<Funcionario> buscarPorCpf(String cpf) {
        String digitos = ValidadorCPF.normalizar(cpf);
        return funcionarios.values().stream()
                .filter(f -> f.getCpf().equals(digitos))
                .findFirst();
    }

    /**
     * @param nome      nome (ou trecho do nome) procurado
     * @param buscaExata {@code true} compara o nome inteiro; {@code false} procura o trecho
     * @return funcionários encontrados, na ordem de cadastro (maiúsculas/minúsculas são ignoradas)
     */
    public List<Funcionario> buscarPorNome(String nome, boolean buscaExata) {
        String termo = nome == null ? "" : nome.trim().toLowerCase(Locale.ROOT);
        return funcionarios.values().stream()
                .filter(f -> {
                    String atual = f.getNome().toLowerCase(Locale.ROOT);
                    return buscaExata ? atual.equals(termo) : atual.contains(termo);
                })
                .toList();
    }

    /** @return lista somente leitura com todos os funcionários, na ordem de cadastro */
    public List<Funcionario> listarTodos() {
        return List.copyOf(funcionarios.values());
    }

    /** @return {@code true} se algum funcionário foi removido */
    public boolean remover(int codigo) {
        return funcionarios.remove(codigo) != null;
    }

    public boolean existePorCodigo(int codigo) {
        return funcionarios.containsKey(codigo);
    }

    public boolean existePorCpf(String cpf) {
        return buscarPorCpf(cpf).isPresent();
    }

    public int contar() {
        return funcionarios.size();
    }
}
