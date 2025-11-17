package Model;

import java.time.LocalDateTime;


/** Classe que liga o "chamado" ao "tecnico" **/

public class Atendimento {

    private int id;
    private String descricao; // Usado para registrar a solução ou o andamento do atendimento
    private LocalDateTime dataAtendimento;
    private int chamadoId;
    private int tecnicoId;


    public Atendimento() {
        this.dataAtendimento = LocalDateTime.now();
    }


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

    @Override
    public String toString() {
        return String.format("ID: %d, Chamado ID: %d, Técnico ID: %d, Data: %s, Descrição: %s",
                id, chamadoId, tecnicoId, dataAtendimento.toString(), descricao);
    }
}