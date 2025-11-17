package Controller;

import Model.Atendimento;
import Model.Chamado;
import DAO.AtendimentoDAO;
import Model.mongo.LogSistema;
import java.util.List;

/**
 * Controller de Atendimento com integração MongoDB
 */
public class AtendimentoController {

    private AtendimentoDAO atendimentoDAO;
    private ChamadoController chamadoController;
    private LogController logController;
    private HistoricoAtendimentoController historicoController;

    public AtendimentoController() {
        this.atendimentoDAO = new AtendimentoDAO();
        this.chamadoController = new ChamadoController();
        this.logController = new LogController();
        this.historicoController = new HistoricoAtendimentoController();
    }

    public void registrarAtendimento(Atendimento atendimento) {
        try {
            atendimentoDAO.create(atendimento);
            System.out.println("Atendimento registrado com sucesso (ID: " + atendimento.getId() + ").");

            Chamado chamado = chamadoController.buscarChamadoPorId(atendimento.getChamadoId());
            if (chamado != null) {
                // Se for o primeiro atendimento, atribui o técnico
                if (chamado.getTecnicoId() == null) {
                    chamadoController.atribuirTecnico(chamado.getId(), atendimento.getTecnicoId());
                }

                // Atualiza status se necessário
                if (chamado.getStatus().equals("Aberto")) {
                    chamadoController.atualizarStatusChamado(chamado.getId(), "Em Atendimento");
                }

                // Adiciona ao histórico no MongoDB
                // Por simplicidade, considerando tempo padrão de 30 minutos
                historicoController.adicionarAtendimento(
                        chamado.getId(),
                        atendimento.getTecnicoId(),
                        "Técnico ID: " + atendimento.getTecnicoId(), // Idealmente buscar nome
                        atendimento.getDescricao(),
                        30 // Tempo em minutos - pode ser parametrizável
                );

                // Registra log
                LogSistema log = new LogSistema(
                        LogSistema.TipoLog.ATENDIMENTO_REGISTRADO,
                        atendimento.getTecnicoId(),
                        "Técnico", // Idealmente buscar nome
                        "TECNICO"
                );
                log.adicionarDetalhe("chamado_id", chamado.getId());
                log.adicionarDetalhe("chamado_titulo", chamado.getTitulo());
                log.adicionarDetalhe("descricao", atendimento.getDescricao());
                logController.registrarLog(log);
            }

        } catch (Exception e) {
            System.err.println("Erro ao registrar atendimento: " + e.getMessage());
        }
    }

    public List<Atendimento> listarAtendimentosPorChamado(int chamadoId) {
        return atendimentoDAO.findByChamadoId(chamadoId);
    }
}