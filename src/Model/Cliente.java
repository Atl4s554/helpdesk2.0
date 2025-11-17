package Model;

/** ID do Cliente é o mesmo ID do Usuario **/

public class Cliente extends Usuario {

    private String cpf;

    public Cliente() {
        super();
    }

    public Cliente(String nome, String email, String senha, String cpf) {
        super(nome, email, senha);
        this.cpf = cpf;
    }

    // Construtor compatível com chamadas que passam o id no momento da criação
    public Cliente(int id, String nome, String email, String senha, String cpf) {
        super(id, nome, email, senha); // usa construtor com id da superclasse
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "Cliente - " + super.toString() + ", CPF: " + cpf;
    }
}
