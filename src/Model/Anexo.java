package Model;

/** Classe que define o modelo **/

public class Anexo {

    private int id;
    private String nomeArquivo;
    private String caminho;
    private int chamadoId;


    public Anexo() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public int getChamadoId() {
        return chamadoId;
    }

    public void setChamadoId(int chamadoId) {
        this.chamadoId = chamadoId;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Arquivo: %s, Caminho: %s, Chamado ID: %d",
                id, nomeArquivo, caminho, chamadoId);
    }
}