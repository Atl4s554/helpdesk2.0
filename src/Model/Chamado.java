package Model;

import java.time.LocalDateTime;

/** Classe que define o Chamado. **/

public class Chamado {

    private int id;
    private String titulo;
    private String descricao;
    private String prioridade; // mantive
    private String status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private int clienteId;
    private Integer tecnicoId; // pode ser null
    private int empresaId;

    public Chamado() {
        this.dataAbertura = LocalDateTime.now();
        this.status = "Aberto";
    }

    // Construtor com id (compatibilidade)
    public Chamado(int id, String titulo, String descricao, String prioridade, String status,
                   LocalDateTime dataAbertura, LocalDateTime dataFechamento,
                   int clienteId, Integer tecnicoId, int empresaId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataFechamento = dataFechamento;
        this.clienteId = clienteId;
        this.tecnicoId = tecnicoId;
        this.empresaId = empresaId;
    }

    // Getters / Setters normais
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

    // ---------- ALIAS / MÉTODOS ADICIONAIS PARA COMPATIBILIDADE ----------

    // métodos com nomes que o código existente frequentemente chama:
    public int getIdChamado() {
        return this.id;
    }

    public void setIdChamado(int id) {
        this.id = id;
    }

    // muitos lugares chamavam getTime() para obter data/hora; mapeamos para dataAbertura
    public LocalDateTime getTime() {
        return this.dataAbertura;
    }

    public void setTime(LocalDateTime time) {
        this.dataAbertura = time;
    }

    // aliases com nomes alternativos:
    public int getIdCliente() {
        return this.clienteId;
    }

    public void setIdCliente(int clienteId) {
        this.clienteId = clienteId;
    }

    // Retorna -1 se não houver técnico atribuído, para compatibilidade com chamadas que esperam int
    public int getIdTecnico() {
        return this.tecnicoId != null ? this.tecnicoId : -1;
    }

    public void setIdTecnico(int tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public int getIdEmpresa() {
        return this.empresaId;
    }

    public void setIdEmpresa(int empresaId) {
        this.empresaId = empresaId;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Título: %s, Status: %s, Cliente ID: %d, Técnico ID: %s",
                id, titulo, status, clienteId, tecnicoId != null ? tecnicoId.toString() : "N/A");
    }
}
