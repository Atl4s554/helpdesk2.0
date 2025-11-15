package Controller;

import Model.mongo.HistoricoAtendimento;
import Model.mongo.HistoricoAtendimento.EntradaAtendimento;
import DAO.mongo.HistoricoAtendimentoDAO;
import java.util.List;

/**
 * Controller para gerenciar histórico de atendimentos no MongoDB
 */
public class HistoricoAtendimentoController {

    private HistoricoAtendimentoDAO historicoDAO;

    public HistoricoAtendimentoController() {
        this.historicoDAO = new HistoricoAtendimentoDAO();
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
     * Adiciona uma entrada de atendimento ao histórico
     */
    public void adicionarAtendimento(int chamadoId, int tecnicoId, String tecnicoNome,
                                     String descricao, int tempoGastoMinutos) {
        try {
            EntradaAtendimento entrada = new EntradaAtendimento(tecnicoId, tecnicoNome, descricao);
            entrada.setTempoGastoMinutos(tempoGastoMinutos);

            historicoDAO.adicionarAtendimento(chamadoId, entrada);
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