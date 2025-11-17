package Model;

import java.time.LocalDateTime;
import java.util.Date; // Importado para o novo construtor

/** Classe que liga o "chamado" ao "tecnico" **/

public class Atendimento {

    private int id;
    private String descricao; // Usado para registrar a solução ou o andamento do atendimento
    private LocalDateTime dataAtendimento;
    private int chamadoId;
    private int tecnicoId;
    private int tempoGastoMin; // <-- CAMPO ADICIONADO

    public Atendimento() {
        this.dataAtendimento = LocalDateTime.now();
    }

    /**
     * NOVO CONSTRUTOR:
     * Adicionado para ser usado pelo AtendimentoController (método do Servlet).
     * Aceita java.util.Date e o tempoGasto.
     */
    public Atendimento(int id, int chamadoId, int tecnicoId, Date dataAtendimento, String descricao, int tempoGastoMin) {
        this.id = id;
        this.chamadoId = chamadoId;
        this.tecnicoId = tecnicoId;
        // Converte java.util.Date para LocalDateTime
        if (dataAtendimento != null) {
            this.dataAtendimento = new java.sql.Timestamp(dataAtendimento.getTime()).toLocalDateTime();
        } else {
            this.dataAtendimento = LocalDateTime.now();
        }
        this.descricao = descricao;
        this.tempoGastoMin = tempoGastoMin;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }

    /**
     * Getter de conveniência para java.util.Date (usado pelo AtendimentoDAO)
     */
    public Date getDataAtendimentoAsDate() {
        if (this.dataAtendimento == null) {
            return new Date(); // Retorna data atual se nulo
        }
        return java.sql.Timestamp.valueOf(this.dataAtendimento);
    }

    public void setDataAtendimento(LocalDateTime dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public int getChamadoId() {
        return chamadoId;
    }

    public void setChamadoId(int chamadoId) {
        this.chamadoId = chamadoId;
    }

    public int getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(int tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    // Getter e Setter para o novo campo
    public int getTempoGastoMin() {
        return tempoGastoMin;
    }

    public void setTempoGastoMin(int tempoGastoMin) {
        this.tempoGastoMin = tempoGastoMin;
    }


    @Override
    public String toString() {
        return String.format("ID: %d, Chamado ID: %d, Técnico ID: %d, Data: %s, Descrição: %s, Tempo: %d min",
                id, chamadoId, tecnicoId, dataAtendimento.toString(), descricao, tempoGastoMin);
    }
}
