package Controller;

import Model.mongo.HistoricoAtendimento;
import Model.mongo.HistoricoAtendimento.EntradaAtendimento;
import DAO.mongo.HistoricoAtendimentoDAO;
import java.util.List;
import DAO.UsuarioDAO;
import Model.Usuario;
import java.util.Date;

/**
 * Controller para gerenciar histórico de atendimentos no MongoDB
 */
public class HistoricoAtendimentoController {

    private HistoricoAtendimentoDAO historicoDAO;
    private UsuarioDAO usuarioDAO;

    public HistoricoAtendimentoController() {
        this.historicoDAO = new HistoricoAtendimentoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * NOVO: Método usado pelo Servlet/Controller mais recente.
     */
    public void adicionarEntrada(int chamadoId, int idUsuario, String descricao) {
        // Busca o nome do usuário
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario); // Assegure que 'buscarPorId' existe no UsuarioDAO
        String nomeUsuario = (usuario != null) ? usuario.getNome() : "Sistema";

        HistoricoAtendimento.EntradaAtendimento entrada = new HistoricoAtendimento.EntradaAtendimento(
                idUsuario,
                nomeUsuario,
                new Date(),
                descricao
        );

        historicoDAO.adicionarEntrada(chamadoId, entrada);
    }

    /**
     * Busca o histórico completo de um chamado.
     * Usado pelo AtendimentoServlet.
     */
    public HistoricoAtendimento getHistorico(int chamadoId) {
        return historicoDAO.buscarPorChamadoId(chamadoId);
    }

    /**
     * Cria um novo histórico quando um chamado é aberto
     */
    public void criarHistorico(int chamadoId, String chamadoTitulo, int clienteId, String clienteNome) {
        try {
            HistoricoAtendimento historico = new HistoricoAtendimento(chamadoId, chamadoTitulo, clienteId, clienteNome);
            historicoDAO.create(historico);
            System.out.println("Histórico criado para chamado " + chamadoId + " no MongoDB.");
        } catch (Exception e) {
            System.err.println("Erro ao criar histórico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adiciona uma entrada de atendimento ao histórico (Método antigo do CLI)
     * CORREÇÃO: Este é o método que faltava (Erro linha 76).
     */
    public void adicionarAtendimento(int chamadoId, int tecnicoId, String tecnicoNome,
                                     String descricao, int tempoGastoMinutos) {
        try {
            EntradaAtendimento entrada = new EntradaAtendimento(tecnicoId, tecnicoNome, descricao);
            entrada.setTempoGastoMinutos(tempoGastoMinutos);

            historicoDAO.adicionarEntrada(chamadoId, entrada); // CORREÇÃO: Usando adicionarEntrada do DAO
            System.out.println("Atendimento adicionado ao histórico do chamado " + chamadoId);
        } catch (Exception e) {
            System.err.println("Erro ao adicionar atendimento ao histórico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Busca histórico de um chamado
     */
    public HistoricoAtendimento buscarHistoricoPorChamado(int chamadoId) {
        return historicoDAO.findByChamadoId(chamadoId);
    }

    /**
     * Busca históricos de um cliente
     */
    public List<HistoricoAtendimento> buscarHistoricosPorCliente(int clienteId) {
        return historicoDAO.findByClienteId(clienteId);
    }

    /**
     * Fecha o histórico quando o chamado é finalizado
     */
    public void fecharHistorico(int chamadoId) {
        try {
            historicoDAO.fecharHistorico(chamadoId);
            System.out.println("Histórico do chamado " + chamadoId + " fechado.");
        } catch (Exception e) {
            System.err.println("Erro ao fechar histórico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calcula tempo médio de resolução de chamados
     */
    public double calcularTempoMedioResolucao() {
        return historicoDAO.calcularTempoMedioResolucao();
    }
}