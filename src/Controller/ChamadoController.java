package Controller;

import DAO.ChamadoDAO;
import Model.Chamado;
import Model.Cliente;
import Model.Tecnico;
import Model.mongo.LogSistema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller de Chamado com integração MongoDB
 */
public class ChamadoController {

    private ChamadoDAO chamadoDAO;
    private LogController logController;
    private HistoricoAtendimentoController historicoController;

    public ChamadoController() {
        this.chamadoDAO = new ChamadoDAO();
        this.logController = new LogController();
        this.historicoController = new HistoricoAtendimentoController();
    }

    public void abrirChamado(Chamado chamado) {
        try {
            chamadoDAO.create(chamado);
            System.out.println("Chamado '" + chamado.getTitulo() + "' aberto com sucesso (ID: " + chamado.getId() + ").");

            // Cria histórico no MongoDB
            historicoController.criarHistorico(
                    chamado.getId(),
                    chamado.getTitulo(),
                    chamado.getClienteId(),
                    "Cliente ID: " + chamado.getClienteId() // Idealmente buscar nome do cliente
            );

            // Registra log
            LogSistema log = new LogSistema(
                    LogSistema.TipoLog.CHAMADO_ABERTO,
                    chamado.getClienteId(),
                    "Cliente", // Idealmente buscar nome
                    "CLIENTE"
            );
            log.adicionarDetalhe("chamado_id", chamado.getId());
            log.adicionarDetalhe("titulo", chamado.getTitulo());
            log.adicionarDetalhe("prioridade", chamado.getPrioridade());
            logController.registrarLog(log);

        } catch (Exception e) {
            System.err.println("Erro ao abrir chamado: " + e.getMessage());
        }
    }

    public Chamado buscarChamadoPorId(int id) {
        return chamadoDAO.read(id);
    }

    public List<Chamado> listarTodosChamados() {
        return chamadoDAO.findAll();
    }

    public List<Chamado> listarChamadosPorCliente(Cliente cliente) {
        return chamadoDAO.findAll(); // Idealmente filtrar por clienteId
    }

    public List<Chamado> listarChamadosPorTecnico(Tecnico tecnico) {
        return chamadoDAO.findAll(); // Idealmente filtrar por tecnicoId
    }

    public void atribuirTecnico(int chamadoId, int tecnicoId) {
        Chamado chamado = chamadoDAO.read(chamadoId);
        if (chamado != null) {
            chamado.setTecnicoId(tecnicoId);
            if (chamado.getStatus().equals("Aberto")) {
                chamado.setStatus("Em Atendimento");
            }
            chamadoDAO.update(chamado);
            System.out.println("Chamado " + chamadoId + " atribuído ao técnico " + tecnicoId + ".");

            // Registra log
            LogSistema log = new LogSistema(
                    LogSistema.TipoLog.CHAMADO_ATRIBUIDO,
                    tecnicoId,
                    "Técnico", // Idealmente buscar nome
                    "TECNICO"
            );
            log.adicionarDetalhe("chamado_id", chamadoId);
            log.adicionarDetalhe("titulo", chamado.getTitulo());
            logController.registrarLog(log);

        } else {
            System.err.println("Chamado não encontrado.");
        }
    }

    public void finalizarChamado(int chamadoId) {
        Chamado chamado = chamadoDAO.read(chamadoId);
        if (chamado != null) {
            chamado.setStatus("Fechado");
            chamado.setDataFechamento(LocalDateTime.now());
            chamadoDAO.update(chamado);
            System.out.println("Chamado " + chamadoId + " finalizado.");

            // Fecha histórico no MongoDB
            historicoController.fecharHistorico(chamadoId);

            // Registra log
            LogSistema log = new LogSistema(
                    LogSistema.TipoLog.CHAMADO_FECHADO,
                    chamado.getTecnicoId() != null ? chamado.getTecnicoId() : 0,
                    "Sistema",
                    "SISTEMA"
            );
            log.adicionarDetalhe("chamado_id", chamadoId);
            log.adicionarDetalhe("titulo", chamado.getTitulo());
            logController.registrarLog(log);

        } else {
            System.err.println("Chamado não encontrado.");
        }
    }

    public void atualizarStatusChamado(int chamadoId, String novoStatus) {
        Chamado chamado = chamadoDAO.read(chamadoId);
        if (chamado != null) {
            String statusAnterior = chamado.getStatus();
            chamado.setStatus(novoStatus);
            chamadoDAO.update(chamado);
            System.out.println("Status do Chamado " + chamadoId + " atualizado para " + novoStatus + ".");

            // Registra log
            LogSistema log = new LogSistema(
                    LogSistema.TipoLog.CHAMADO_ATUALIZADO,
                    chamado.getTecnicoId() != null ? chamado.getTecnicoId() : 0,
                    "Sistema",
                    "SISTEMA"
            );
            log.adicionarDetalhe("chamado_id", chamadoId);
            log.adicionarDetalhe("status_anterior", statusAnterior);
            log.adicionarDetalhe("status_novo", novoStatus);
            logController.registrarLog(log);

        } else {
            System.err.println("Chamado não encontrado.");
        }
    }
}