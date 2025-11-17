package Model;

/** Classe que cria o tecnico e atribui um id (é o mesmo id de usuario)**/

public class Tecnico extends Usuario {

    private String especialidade;

    // Construtor padrão
    public Tecnico() {
        super();
    }

    public Tecnico(String nome, String email, String senha, String especialidade) {
        super(nome, email, senha);
        this.especialidade = especialidade;
    }

    // Construtor compatível com passagem de id
    public Tecnico(int id, String nome, String email, String senha, String especialidade) {
        super(id, nome, email, senha);
        this.especialidade = especialidade;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    @Override
    public String toString() {
        return "Técnico - " + super.toString() + ", Especialidade: " + especialidade;
    }
}
