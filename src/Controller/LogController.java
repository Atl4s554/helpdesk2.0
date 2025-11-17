package Controller;

import Model.mongo.LogSistema;
import DAO.mongo.LogDAO;
import java.time.LocalDateTime; // ADICIONADO
import java.util.Date;          // ADICIONADO
import java.util.List;
import Model.mongo.LogSistema.TipoLog; // ADICIONADO: Importa o TipoLog correto

/**
 * Controller para gerenciar logs do sistema no MongoDB
 */
public class LogController {

    private LogDAO logDAO;

    // REMOVIDO: O enum TipoLog foi removido daqui para evitar conflito.
    // Usaremos o que está em Model.mongo.LogSistema.TipoLog

    public LogController() {
        this.logDAO = new LogDAO();
    }

    public void logar(int idUsuario, TipoLog tipo, String mensagem) {
        LogSistema log = new LogSistema();
        log.setIdUsuario(idUsuario);
        log.setTipo(tipo.toString());
        log.setMensagem(mensagem);
        // CORREÇÃO: O modelo LogSistema espera LocalDateTime, não Date.
        log.setTimestamp(LocalDateTime.now());

        logDAO.inserir(log);
    }

    /**
     * Registra um novo log no sistema
     */
    public void registrarLog(LogSistema log) {
        try {
            // CORREÇÃO: O método no LogDAO é 'inserir', não 'create'.
            logDAO.inserir(log);
        } catch (Exception e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para criar e registrar log rapidamente
     */
    public void registrarLog(String tipo, int usuarioId, String usuarioNome, String usuarioTipo) {
        LogSistema log = new LogSistema(tipo, usuarioId, usuarioNome, usuarioTipo);
        registrarLog(log);
    }

    /**
     * Registra log com detalhes adicionais
     */
    public void registrarLogComDetalhes(String tipo, int usuarioId, String usuarioNome,
                                        String usuarioTipo, String chave, Object valor) {
        LogSistema log = new LogSistema(tipo, usuarioId, usuarioNome, usuarioTipo);
        log.adicionarDetalhe(chave, valor);
        registrarLog(log);
    }

    /**
     * Busca logs de um usuário
     */
    public List<LogSistema> buscarLogsPorUsuario(int usuarioId, int limite) {
        return logDAO.findByUsuarioId(usuarioId, limite);
    }

    /**
     * Busca logs por tipo
     */
    public List<LogSistema> buscarLogsPorTipo(String tipo, int limite) {
        return logDAO.findByTipo(tipo, limite);
    }

    /**
     * Busca logs recentes
     */
    public List<LogSistema> buscarLogsRecentes(int limite) {
        return logDAO.findAll(limite);
    }

    /**
     * Busca logs por período
     */
    public List<LogSistema> buscarLogsPorPeriodo(LocalDateTime inicio, LocalDateTime fim, int limite) {
        return logDAO.findByPeriodo(inicio, fim, limite);
    }

    /**
     * Conta logs por tipo
     */
    public long contarLogsPorTipo(String tipo) {
        return logDAO.countByTipo(tipo);
    }

    /**
     * Limpa logs antigos (manutenção)
     */
    public long limparLogsAntigos(LocalDateTime antes) {
        return logDAO.deleteOldLogs(antes);
    }

    /**
     * Busca os últimos logs.
     * NOVO: Necessário para o DashboardServlet e LogServlet.
     * @param limit 0 para todos, ou N para os últimos N.
     */
    public List<LogSistema> getLogs(int limit) {
        // Este método 'listarLogs' DEVE existir no seu LogDAO
        return logDAO.listarLogs(limit);
    }
}