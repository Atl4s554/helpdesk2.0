package Controller;

import DAO.ClienteDAO;
import DAO.TecnicoDAO;
import DAO.UsuarioDAO;
import Model.Cliente;
import Model.Tecnico;
import Model.Usuario;
import Model.mongo.LogSistema;
import java.util.List;

/**
 * Controller de Usuario com integração de logs no MongoDB
 */
public class UsuarioController {

    private ClienteDAO clienteDAO;
    private TecnicoDAO tecnicoDAO;
    private UsuarioDAO usuarioDAO;
    private LogController logController;

    public UsuarioController() {
        this.clienteDAO = new ClienteDAO();
        this.tecnicoDAO = new TecnicoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.logController = new LogController();
    }

    public void criarCliente(Cliente cliente) {
        try {
            clienteDAO.create(cliente);
            System.out.println("Cliente " + cliente.getNome() + " criado com sucesso (ID: " + cliente.getId() + ").");

            // Registra log no MongoDB
            logController.registrarLogComDetalhes(
                    LogSistema.TipoLog.USUARIO_CRIADO,
                    cliente.getId(),
                    cliente.getNome(),
                    "CLIENTE",
                    "tipo_usuario",
                    "CLIENTE"
            );

        } catch (Exception e) {
            System.err.println("Erro ao criar cliente: " + e.getMessage());
        }
    }

    public void criarTecnico(Tecnico tecnico) {
        try {
            tecnicoDAO.create(tecnico);
            System.out.println("Técnico " + tecnico.getNome() + " criado com sucesso (ID: " + tecnico.getId() + ").");

            // Registra log no MongoDB
            logController.registrarLogComDetalhes(
                    LogSistema.TipoLog.USUARIO_CRIADO,
                    tecnico.getId(),
                    tecnico.getNome(),
                    "TECNICO",
                    "especialidade",
                    tecnico.getEspecialidade()
            );

        } catch (Exception e) {
            System.err.println("Erro ao criar técnico: " + e.getMessage());
        }
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.findAll();
    }

    public List<Tecnico> listarTecnicos() {
        return tecnicoDAO.findAll();
    }

    public Usuario autenticar(String email, String senha) {
        // Usa o método de autenticação com verificação de hash do UsuarioDAO
        Usuario usuario = usuarioDAO.autenticar(email, senha);

        if (usuario != null) {
            // Login bem-sucedido
            String tipoUsuario = "USUARIO";

            // Verifica se é Cliente ou Técnico
            Cliente cliente = clienteDAO.read(usuario.getId());
            if (cliente != null) {
                tipoUsuario = "CLIENTE";
                usuario = cliente; // Retorna o objeto Cliente completo
            } else {
                Tecnico tecnico = tecnicoDAO.read(usuario.getId());
                if (tecnico != null) {
                    tipoUsuario = tecnico.getEspecialidade().equals("Administrador") ? "ADMIN" : "TECNICO";
                    usuario = tecnico; // Retorna o objeto Tecnico completo
                }
            }

            // Registra log de login bem-sucedido
            logController.registrarLog(
                    LogSistema.TipoLog.LOGIN,
                    usuario.getId(),
                    usuario.getNome(),
                    tipoUsuario
            );

            System.out.println("Login bem-sucedido: " + usuario.getNome() + " (" + tipoUsuario + ")");

        } else {
            // Login falhou - registra tentativa
            logController.registrarLogComDetalhes(
                    LogSistema.TipoLog.LOGIN_FALHOU,
                    0,
                    "Desconhecido",
                    "DESCONHECIDO",
                    "email",
                    email
            );

            System.out.println("Tentativa de login falhou para: " + email);
        }

        return usuario;
    }

    /**
     * Registra logout do usuário
     */
    public void logout(Usuario usuario) {
        if (usuario != null) {
            String tipoUsuario = usuario instanceof Cliente ? "CLIENTE" :
                    (usuario instanceof Tecnico ? "TECNICO" : "USUARIO");

            logController.registrarLog(
                    LogSistema.TipoLog.LOGOUT,
                    usuario.getId(),
                    usuario.getNome(),
                    tipoUsuario
            );
        }
    }

    public Cliente buscarClientePorId(int id) {
        return clienteDAO.read(id);
    }

    public Tecnico buscarTecnicoPorId(int id) {
        return tecnicoDAO.read(id);
    }

    public void atualizarCliente(Cliente cliente) {
        try {
            clienteDAO.update(cliente);
            System.out.println("Cliente " + cliente.getNome() + " atualizado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    public void atualizarTecnico(Tecnico tecnico) {
        try {
            tecnicoDAO.update(tecnico);
            System.out.println("Técnico " + tecnico.getNome() + " atualizado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar técnico: " + e.getMessage());
        }
    }

    public void deletarCliente(int id) {
        try {
            clienteDAO.delete(id);
            System.out.println("Cliente com ID " + id + " deletado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao deletar cliente: " + e.getMessage());
        }
    }

    public void deletarTecnico(int id) {
        try {
            tecnicoDAO.delete(id);
            System.out.println("Técnico com ID " + id + " deletado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao deletar técnico: " + e.getMessage());
        }
    }
}