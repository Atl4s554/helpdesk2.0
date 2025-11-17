package Model;

import java.time.LocalDateTime;

/** Classe que define o Chamado. **/

public class Chamado {

    private int id;
    private String titulo;
    private String descricao;
    private String prioridade; // Não está no script SQL, mas é um atributo forte no DER. Vou manter.
    private String status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private int clienteId; // fk_id_cliente no código original, cliente_id no script SQL
    private Integer tecnicoId; // tecnico_id no script SQL (pode ser NULL)
    private int empresaId; // fk_id_empresa no código original, empresa_id no script SQL


    public Chamado() {
        this.dataAbertura = LocalDateTime.now();
        this.status = "Aberto";
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Integer tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public int getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(int empresaId) {
        this.empresaId = empresaId;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Título: %s, Status: %s, Cliente ID: %d, Técnico ID: %s",
                id, titulo, status, clienteId, tecnicoId != null ? tecnicoId.toString() : "N/A");
    }
}