package Model;

/** Classe que define Empresa. **/

public class Empresa {

    private int id;
    private String nome; // No script SQL é 'nome', mas no DER está 'razao_social'.
    private String cnpj;


    public Empresa() {}

    public Empresa(String nome, String cnpj) {
        this.nome = nome;
        this.cnpj = cnpj;
    }


    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getNome() {

        return nome;
    }

    public void setNome(String nome) {

        this.nome = nome;
    }

    public String getCnpj() {

        return cnpj;
    }

    public void setCnpj(String cnpj) {

        this.cnpj = cnpj;
    }

    @Override
    public String toString() {

        return "ID: " + id + ", Nome: " + nome + ", CNPJ: " + cnpj;
    }
}