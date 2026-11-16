package service;

import exception.DadoInvalidoException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import model.Funcionario;
import model.Tributavel;
import repository.FuncionarioRepository;
import util.FormatadorMoeda;
import util.ValidadorCPF;

import java.util.List;

/**
 * Implementa as regras de negócio do sistema e coordena o acesso ao repositório.
 * <p>
 * Contém o exemplo de polimorfismo por sobrecarga: três versões de
 * {@code buscarFuncionario} com o mesmo nome e parâmetros diferentes.
 */
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        if (repository == null) {
            throw new DadoInvalidoException("O repositório é obrigatório.");
        }
        this.repository = repository;
    }

    public FuncionarioService() {
        this(new FuncionarioRepository());
    }

    // ------------------------------------------------------------------ cadastro

    /**
     * Cadastra um funcionário, garantindo que código e CPF sejam únicos.
     *
     * @throws FuncionarioJaCadastradoException se o código ou o CPF já estiverem cadastrados
     */
    public void cadastrar(Funcionario funcionario) throws FuncionarioJaCadastradoException {
        if (funcionario == null) {
            throw new DadoInvalidoException("O funcionário é obrigatório.");
        }
        if (repository.existePorCodigo(funcionario.getCodigo())) {
            throw new FuncionarioJaCadastradoException(funcionario.getCodigo());
        }
        if (repository.existePorCpf(funcionario.getCpf())) {
            throw new FuncionarioJaCadastradoException(
                    "Já existe um funcionário cadastrado com o CPF " + funcionario.getCpfFormatado() + ".");
        }
        repository.salvar(funcionario);
    }

    public List<Funcionario> listarTodos() {
        return repository.listarTodos();
    }

    // ------------------------------------------------------- busca (sobrecarga)

    /** Busca pelo código identificador. */
    public Funcionario buscarFuncionario(int codigo) throws FuncionarioNaoEncontradoException {
        return repository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(codigo));
    }

    /** Busca pelo CPF (com ou sem máscara). */
    public Funcionario buscarFuncionario(String cpf) throws FuncionarioNaoEncontradoException {
        if (cpf == null || cpf.isBlank()) {
            throw new DadoInvalidoException("Informe o CPF para a busca.");
        }
        return repository.buscarPorCpf(cpf)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(
                        "Nenhum funcionário encontrado com o CPF " + ValidadorCPF.formatar(cpf) + "."));
    }

    /**
     * Busca pelo nome.
     *
     * @param buscaExata {@code true} para nome completo; {@code false} para trecho do nome
     */
    public List<Funcionario> buscarFuncionario(String nome, boolean buscaExata)
            throws FuncionarioNaoEncontradoException {
        if (nome == null || nome.isBlank()) {
            throw new DadoInvalidoException("Informe o nome para a busca.");
        }
        List<Funcionario> encontrados = repository.buscarPorNome(nome, buscaExata);
        if (encontrados.isEmpty()) {
            throw new FuncionarioNaoEncontradoException(
                    "Nenhum funcionário encontrado com o nome \"" + nome.trim() + "\".");
        }
        return encontrados;
    }

    // ------------------------------------------------------------------ remoção

    public void removerFuncionario(int codigo) throws FuncionarioNaoEncontradoException {
        if (!repository.remover(codigo)) {
            throw new FuncionarioNaoEncontradoException(codigo);
        }
    }

    // ----------------------------------------------------------------- cálculos

    public double calcularRemuneracao(int codigo) throws FuncionarioNaoEncontradoException {
        return buscarFuncionario(codigo).calcularRemuneracao();
    }

    /**
     * @return tributos do funcionário, ou zero se a categoria não for {@link Tributavel}
     */
    public double calcularTributos(Funcionario funcionario) {
        if (funcionario instanceof Tributavel tributavel) {
            return tributavel.calcularTributos();
        }
        return 0;
    }

    public double calcularRemuneracaoLiquida(Funcionario funcionario) {
        return funcionario.calcularRemuneracao() - calcularTributos(funcionario);
    }

    /** @return soma das remunerações brutas de todos os funcionários */
    public double calcularTotalFolha() {
        return repository.listarTodos().stream()
                .mapToDouble(Funcionario::calcularRemuneracao)
                .sum();
    }

    public double calcularTotalTributos() {
        return repository.listarTodos().stream()
                .mapToDouble(this::calcularTributos)
                .sum();
    }

    // -------------------------------------------------------------- relatórios

    public String exibirDados(int codigo) throws FuncionarioNaoEncontradoException {
        Funcionario f = buscarFuncionario(codigo);
        String nl = System.lineSeparator();
        return f.exibirDados() + nl
                + "Tributos/descontos: " + FormatadorMoeda.formatar(calcularTributos(f)) + nl
                + "Remuneração líquida: " + FormatadorMoeda.formatar(calcularRemuneracaoLiquida(f));
    }

    public String gerarRelatorio() {
        List<Funcionario> funcionarios = repository.listarTodos();
        String nl = System.lineSeparator();
        if (funcionarios.isEmpty()) {
            return "Nenhum funcionário cadastrado.";
        }

        String formato = "%-6s %-13s %-22s %-15s %15s %15s %15s" + nl;
        String separador = "-".repeat(107) + nl;
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO DA FOLHA DE PAGAMENTO").append(nl).append(separador);
        sb.append(String.format(formato, "Código", "Tipo", "Nome", "CPF", "Bruto", "Tributos", "Líquido"));
        sb.append(separador);
        for (Funcionario f : funcionarios) {
            sb.append(String.format(formato,
                    f.getCodigo(),
                    f.getTipo(),
                    abreviar(f.getNome(), 22),
                    f.getCpfFormatado(),
                    FormatadorMoeda.formatar(f.calcularRemuneracao()),
                    FormatadorMoeda.formatar(calcularTributos(f)),
                    FormatadorMoeda.formatar(calcularRemuneracaoLiquida(f))));
        }
        double total = calcularTotalFolha();
        double tributos = calcularTotalTributos();
        sb.append(separador);
        sb.append(String.format(formato, "", "", "TOTAL", "",
                FormatadorMoeda.formatar(total),
                FormatadorMoeda.formatar(tributos),
                FormatadorMoeda.formatar(total - tributos)));
        sb.append("Total de funcionários: ").append(funcionarios.size());
        return sb.toString();
    }

    private static String abreviar(String texto, int tamanho) {
        return texto.length() <= tamanho ? texto : texto.substring(0, tamanho - 1) + "…";
    }
}
