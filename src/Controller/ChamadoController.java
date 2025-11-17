package Controller;

import DAO.ChamadoDAO;
import Model.Chamado;
import Model.dto.ChamadoViewDTO; // Importe o DTO
import Model.mongo.LogSistema; // Importe o LogSistema

import java.util.Date;
import java.util.List;

public class ChamadoController {

    private ChamadoDAO chamadoDAO;
    private LogController logController;
    private HistoricoAtendimentoController historicoController; // Para logar abertura

    public ChamadoController() {
        this.chamadoDAO = new ChamadoDAO();
        this.logController = new LogController();
        this.historicoController = new HistoricoAtendimentoController();
    }

    // --- MÉTODOS NOVOS PARA OS SERVLETS ---

    public void abrirChamado(String titulo, String descricao, String prioridade, int idCliente, int idEmpresa) {
        Chamado chamado = new Chamado(
                0, titulo, descricao, "ABERTO", prioridade,
                new Date(), null, // dataAbertura, dataFechamento
                idCliente, 0, idEmpresa // idTecnico 0 ou null
        );

        // 1. Salva no MySQL
        chamadoDAO.abrirChamado(chamado); // Assume que este método existe no DAO

        // 2. Loga no Mongo (Ação)
        // CORREÇÃO: Usando o TipoLog do LogSistema
        logController.logar(idCliente, LogSistema.TipoLog.CRIACAO, "Chamado #" + chamado.getId() + " aberto: " + titulo);

        // 3. Loga no Mongo (Histórico)
        historicoController.adicionarEntrada(chamado.getId(), idCliente, "Chamado criado com a descrição: " + descricao);
    }

    public void atribuirTecnico(int chamadoId, int tecnicoId) {
        // 1. Atualiza MySQL
        chamadoDAO.atribuirTecnico(chamadoId, tecnicoId);

        // 2. Loga no Mongo (Ação)
        // CORREÇÃO: Usando o TipoLog do LogSistema
        logController.logar(tecnicoId, LogSistema.TipoLog.ATUALIZACAO, "Chamado #" + chamadoId + " atribuído ao técnico ID " + tecnicoId);

        // 3. Loga no Mongo (Histórico)
        historicoController.adicionarEntrada(chamadoId, tecnicoId, "Chamado atribuído a este técnico.");
    }

    /**
     * NOVO: Método adicionado para corrigir o erro da linha 70 no AtendimentoController.
     */
    public void atualizarStatusChamado(int chamadoId, String novoStatus) {
        chamadoDAO.atualizarStatus(chamadoId, novoStatus);
        // Adiciona log/histórico se desejar
        // CORREÇÃO: Usando o TipoLog do LogSistema
        logController.logar(0, LogSistema.TipoLog.ATUALIZACAO, "Status do chamado #" + chamadoId + " alterado para " + novoStatus);
    }


    public int contarChamadosPorStatus(String status) {
        return chamadoDAO.contarChamadosPorStatus(status);
    }

    // Métodos que retornam o DTO para o Servlet

    public List<ChamadoViewDTO> listarTodosChamadosView() {
        return chamadoDAO.listarTodosChamadosView();
    }

    public List<ChamadoViewDTO> listarChamadosPorClienteView(int clienteId) {
        return chamadoDAO.listarChamadosPorClienteView(clienteId);
    }

    public List<ChamadoViewDTO> listarChamadosPorTecnicoView(int tecnicoId) {
        return chamadoDAO.listarChamadosPorTecnicoView(tecnicoId);
    }

    public List<ChamadoViewDTO> listarChamadosAbertosView() {
        return chamadoDAO.listarChamadosAbertosView();
    }

    /**
     * Método que estava faltando (Erro linha 59).
     */
    public Chamado buscarChamadoPorId(int id) {
        // Este método precisa existir no seu ChamadoDAO
        return chamadoDAO.buscarPorId(id);
    }
}