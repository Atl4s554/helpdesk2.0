package Model;

/**
 * classe mae de Cliente e Técnico
 */
public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha;

    // NOVO campo: muitas servlets chamam getPerfil()
    // Use String para máxima compatibilidade com código legado.
    private String perfil;

    public Usuario() {}

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Construtor com id (compatibilidade com chamadas que passam id)
    public Usuario(int id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Getters / Setters padrão
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // Perfil: getter/setter adicionados para compatibilidade com servlets/views
    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nome: " + nome + ", Email: " + email + (perfil != null ? ", Perfil: " + perfil : "");
    }
}
