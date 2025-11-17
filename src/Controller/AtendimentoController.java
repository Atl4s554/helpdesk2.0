package Controller;

import Model.Atendimento;
import Model.Chamado;
import DAO.AtendimentoDAO;
import DAO.ChamadoDAO;
import Model.mongo.LogSistema; // Importação
import Model.mongo.LogSistema.TipoLog; // Importação CORRETA
import java.util.List;
import java.util.Date;
// import DAO.ChamadoDAO; // Duplicado

/**
 * Controller de Atendimento com integração MongoDB
 */
public class AtendimentoController {

    private AtendimentoDAO atendimentoDAO;
    private ChamadoController chamadoController;
    private LogController logController;
    private HistoricoAtendimentoController historicoController;
    private ChamadoDAO chamadoDAO;

    public AtendimentoController() {
        this.atendimentoDAO = new AtendimentoDAO();
        this.chamadoController = new ChamadoController();
        this.logController = new LogController();
        this.historicoController = new HistoricoAtendimentoController();
        this.chamadoDAO = new ChamadoDAO();
    }

    /**
     * Método principal chamado pelo AtendimentoServlet.
     * Ele orquestra todas as ações necessárias.
     */
    public void registrarAtendimento(int chamadoId, int tecnicoId, String descricao, int tempoGasto, String novoStatus) {

        // 1. Cria o objeto Atendimento para o MySQL
        // CORREÇÃO: Usando o novo construtor que criei no Model/Atendimento.java
        Atendimento atendimento = new Atendimento(
                0, // id (auto-increment)
                chamadoId,
                tecnicoId,
                new Date(),
                descricao,
                tempoGasto
        );

        // 2. Insere o atendimento no MySQL
        // CORREÇÃO: O método 'inserir' não existia no DAO antigo, mas 'create' sim.
        // Vamos usar 'create', que foi padronizado no DAO.
        atendimentoDAO.create(atendimento);

        // 3. Atualiza o status do chamado no MySQL
        chamadoDAO.atualizarStatus(chamadoId, novoStatus);

        // 4. Adiciona ao histórico do MongoDB
        String historicoMsg = "Atendimento registrado: " + descricao + ". Status alterado para: " + novoStatus;
        historicoController.adicionarEntrada(chamadoId, tecnicoId, historicoMsg);

        // 5. Loga a ação no MongoDB
        // CORREÇÃO: Usando a importação correta do TipoLog
        logController.logar(tecnicoId, TipoLog.ATUALIZACAO, "Registrou atendimento no chamado #" + chamadoId);
    }

    /**
     * Método antigo (do CLI) - agora corrigido
     */
    public void registrarAtendimento(Atendimento atendimento) {
        try {
            // CORREÇÃO: O método 'inserir' foi removido do DAO e substituído por 'create'.
            atendimentoDAO.create(atendimento);
            System.out.println("Atendimento registrado com sucesso (ID: " + atendimento.getId() + ").");

            // CORREÇÃO: (Erro linha 59) Este método agora existe no ChamadoController
            Chamado chamado = chamadoController.buscarChamadoPorId(atendimento.getChamadoId());

            if (chamado != null) {
                // Se for o primeiro atendimento, atribui o técnico
                // CORREÇÃO: getIdTecnico() retorna int, não Integer. Comparar com 0.
                if (chamado.getIdTecnico() == 0) {
                    chamadoController.atribuirTecnico(chamado.getId(), atendimento.getTecnicoId());
                }

                // Atualiza status se necessário
                if (chamado.getStatus().equals("ABERTO")) {
                    // CORREÇÃO: Este método agora existe no ChamadoController
                    chamadoController.atualizarStatusChamado(chamado.getId(), "EM_ATENDIMENTO");
                }

                // Adiciona ao histórico no MongoDB
                // CORREÇÃO: (Erro linha 76) Este método existe no HistoricoAtendimentoController
                historicoController.adicionarAtendimento(
                        chamado.getId(),
                        atendimento.getTecnicoId(),
                        "Técnico ID: " + atendimento.getTecnicoId(), // Idealmente buscar nome
                        atendimento.getDescricao(),
                        atendimento.getTempoGastoMin() // CORREÇÃO: Usando o tempo gasto real
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