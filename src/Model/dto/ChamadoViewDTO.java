package Model.dto;

import Model.Chamado;
import java.util.Date;

// DTO (Data Transfer Object) para transportar dados de 'JOINs' para o front-end.
// Ele herda de Chamado para pegar a maioria dos campos.
public class ChamadoViewDTO extends Chamado {

    // Campos adicionais vindos dos JOINs
    private String nomeCliente;
    private String nomeTecnico;
    private String nomeEmpresa;

    // Construtor é útil para o DAO
    public ChamadoViewDTO(Chamado chamado, String nomeCliente, String nomeTecnico, String nomeEmpresa) {
        // Copia propriedades da classe pai
        super(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getStatus(),
                chamado.getPrioridade(),
                chamado.getDataAbertura(),
                chamado.getDataFechamento(),
                chamado.getIdCliente(),
                chamado.getIdTecnico(),
                chamado.getIdEmpresa()
        );

        this.nomeCliente = nomeCliente;
        this.nomeTecnico = nomeTecnico;
        this.nomeEmpresa = nomeEmpresa;
    }

    // Getters para os novos campos
    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    // Setters (opcional, mas boa prática se necessário)
    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public void setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }
}