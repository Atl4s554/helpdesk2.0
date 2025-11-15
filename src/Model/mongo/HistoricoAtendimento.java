package Model.mongo;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para histórico completo de atendimentos no MongoDB
 * Armazena a timeline completa de um chamado
 */
public class HistoricoAtendimento {

    private ObjectId id;
    private int chamadoId;
    private String chamadoTitulo;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private int clienteId;
    private String clienteNome;
    private List<EntradaAtendimento> atendimentos;
    private int totalTempoMinutos;

    public HistoricoAtendimento() {
        this.atendimentos = new ArrayList<>();
        this.totalTempoMinutos = 0;
    }

    public HistoricoAtendimento(int chamadoId, String chamadoTitulo, int clienteId, String clienteNome) {
        this();
        this.chamadoId = chamadoId;
        this.chamadoTitulo = chamadoTitulo;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.dataAbertura = LocalDateTime.now();
    }

    // Classe interna para representar cada entrada de atendimento
    public static class EntradaAtendimento {
        private LocalDateTime data;
        private int tecnicoId;
        private String tecnicoNome;
        private String descricao;
        private int tempoGastoMinutos;
        private String status; // Em análise, Aguardando, Resolvido, etc.

        public EntradaAtendimento() {}

        public EntradaAtendimento(int tecnicoId, String tecnicoNome, String descricao) {
            this.data = LocalDateTime.now();
            this.tecnicoId = tecnicoId;
            this.tecnicoNome = tecnicoNome;
            this.descricao = descricao;
            this.tempoGastoMinutos = 0;
        }

        // Getters e Setters
        public LocalDateTime getData() { return data; }
        public void setData(LocalDateTime data) { this.data = data; }

        public int getTecnicoId() { return tecnicoId; }
        public void setTecnicoId(int tecnicoId) { this.tecnicoId = tecnicoId; }

        public String getTecnicoNome() { return tecnicoNome; }
        public void setTecnicoNome(String tecnicoNome) { this.tecnicoNome = tecnicoNome; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public int getTempoGastoMinutos() { return tempoGastoMinutos; }
        public void setTempoGastoMinutos(int tempoGastoMinutos) { this.tempoGastoMinutos = tempoGastoMinutos; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s (Tempo: %d min)",
                    data, tecnicoNome, descricao, tempoGastoMinutos);
        }
    }

    // Métodos auxiliares

    public void adicionarAtendimento(EntradaAtendimento entrada) {
        this.atendimentos.add(entrada);
        this.totalTempoMinutos += entrada.getTempoGastoMinutos();
    }

    public void fecharHistorico() {
        this.dataFechamento = LocalDateTime.now();
    }

    // Getters e Setters

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public int getChamadoId() { return chamadoId; }
    public void setChamadoId(int chamadoId) { this.chamadoId = chamadoId; }

    public String getChamadoTitulo() { return chamadoTitulo; }
    public void setChamadoTitulo(String chamadoTitulo) { this.chamadoTitulo = chamadoTitulo; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public List<EntradaAtendimento> getAtendimentos() { return atendimentos; }
    public void setAtendimentos(List<EntradaAtendimento> atendimentos) { this.atendimentos = atendimentos; }

    public int getTotalTempoMinutos() { return totalTempoMinutos; }
    public void setTotalTempoMinutos(int totalTempoMinutos) { this.totalTempoMinutos = totalTempoMinutos; }

    @Override
    public String toString() {
        return String.format("Chamado #%d: %s - Cliente: %s - Atendimentos: %d - Tempo Total: %d min",
                chamadoId, chamadoTitulo, clienteNome, atendimentos.size(), totalTempoMinutos);
    }
}