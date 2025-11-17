package Controller;

import DAO.ClienteDAO;
import DAO.TecnicoDAO;
import DAO.UsuarioDAO;
import Model.Cliente;
import Model.Tecnico;
import Model.Usuario;
import Utils.PasswordUtil; // Seu utilitário de hash

import java.util.ArrayList;
import java.util.List;

public class UsuarioController {

    private UsuarioDAO usuarioDAO;
    private ClienteDAO clienteDAO;
    private TecnicoDAO tecnicoDAO;
    private LogController logController; // Para logar ações

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
        this.clienteDAO = new ClienteDAO(); // Assume que existe
        this.tecnicoDAO = new TecnicoDAO(); // Assume que existe
        this.logController = new LogController();
    }

    // Método de autenticação (usado pelo LoginServlet)
    public Usuario autenticarUsuario(String email, String senha) {
        Usuario usuario = usuarioDAO.autenticar(email, senha); // O DAO deve tratar o HASH
        if (usuario != null) {
            logController.logar(usuario.getId(), LogController.TipoLog.LOGIN, "Usuário " + email + " autenticado com sucesso.");
        } else {
            logController.logar(0, LogController.TipoLog.LOGIN, "Falha na autenticação para " + email + ".");
        }
        return usuario;
    }

    // --- MÉTODOS NOVOS PARA OS SERVLETS ---

    public void criarCliente(String nome, String email, String senha, String cpf) throws Exception {
        //String senhaHash = PasswordUtil.hashSenha(senha); // Use seu utilitário
        String senhaHash = senha; // REMOVA ISSO E USE O HASH
        Cliente cliente = new Cliente(0, nome, email, senhaHash, cpf);
        clienteDAO.inserir(cliente); // Assume que ClienteDAO tem o método inserir
        logController.logar(0, LogController.TipoLog.CRIACAO, "Cliente " + email + " criado.");
    }

    public void criarTecnico(String nome, String email, String senha, String cpf, String especialidade) throws Exception {
        //String senhaHash = PasswordUtil.hashSenha(senha); // Use seu utilitário
        String senhaHash = senha; // REMOVA ISSO E USE O HASH
        Tecnico tecnico = new Tecnico(0, nome, email, senhaHash, cpf, especialidade);
        tecnicoDAO.inserir(tecnico); // Assume que TecnicoDAO tem o método inserir
        logController.logar(0, LogController.TipoLog.CRIACAO, "Técnico " + email + " criado.");
    }

    public void excluirUsuario(int id) {
        // Adicionar lógica de verificação (ex: não excluir se tiver chamados)
        usuarioDAO.excluirUsuario(id);
        logController.logar(0, LogController.TipoLog.EXCLUSAO, "Usuário ID " + id + " excluído.");
    }

    public List<Usuario> listarTodosClientesETecnicos() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(clienteDAO.listarTodos()); // Assume que ClienteDAO tem listarTodos
        usuarios.addAll(tecnicoDAO.listarTodos()); // Assume que TecnicoDAO tem listarTodos
        return usuarios;
    }

    public List<Tecnico> listarTodosTecnicos() {
        return usuarioDAO.listarTodosTecnicos();
    }

    public int contarUsuariosPorPerfil(String perfil) {
        return usuarioDAO.contarUsuariosPorPerfil(perfil);
    }
}