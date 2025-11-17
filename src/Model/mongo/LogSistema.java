package Model.mongo;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo para logs do sistema armazenados no MongoDB
 * Registra todas as ações importantes dos usuários
 */
public class LogSistema {

    private ObjectId id;
    private LocalDateTime timestamp;
    private String tipo; // LOGIN, LOGOUT, CHAMADO_ABERTO, CHAMADO_ATRIBUIDO, etc.
    private int usuarioId;
    private String usuarioNome;
    private String usuarioTipo; // CLIENTE, TECNICO, ADMIN
    private Map<String, Object> detalhes; // Informações adicionais flexíveis
    private String ipAddress;

    public LogSistema() {
        this.timestamp = LocalDateTime.now();
        this.detalhes = new HashMap<>();
    }

    // Construtor com parâmetros principais
    public LogSistema(String tipo, int usuarioId, String usuarioNome, String usuarioTipo) {
        this();
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.usuarioTipo = usuarioTipo;
    }

    // Getters e Setters

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getUsuarioTipo() {
        return usuarioTipo;
    }

    public void setUsuarioTipo(String usuarioTipo) {
        this.usuarioTipo = usuarioTipo;
    }

    public Map<String, Object> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(Map<String, Object> detalhes) {
        this.detalhes = detalhes;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // Métodos auxiliares para adicionar detalhes

    public void adicionarDetalhe(String chave, Object valor) {
        this.detalhes.put(chave, valor);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Usuario: %s (%s) - ID: %d - Detalhes: %s",
                timestamp.toString(), tipo, usuarioNome, usuarioTipo, usuarioId, detalhes);
    }

    // Tipos de log pré-definidos (constantes)
    public static class TipoLog {
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String LOGIN_FALHOU = "LOGIN_FALHOU";
        public static final String CHAMADO_ABERTO = "CHAMADO_ABERTO";
        public static final String CHAMADO_ATRIBUIDO = "CHAMADO_ATRIBUIDO";
        public static final String CHAMADO_ATUALIZADO = "CHAMADO_ATUALIZADO";
        public static final String CHAMADO_FECHADO = "CHAMADO_FECHADO";
        public static final String ATENDIMENTO_REGISTRADO = "ATENDIMENTO_REGISTRADO";
        public static final String USUARIO_CRIADO = "USUARIO_CRIADO";
        public static final String EMPRESA_CRIADA = "EMPRESA_CRIADA";
        public static final String ERRO_SISTEMA = "ERRO_SISTEMA";
    }
}