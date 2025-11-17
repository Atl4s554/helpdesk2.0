package DAO;

import Model.*;
import Utils.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.DBConnection;
import Model.Tecnico;
import Model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO de Usuario com hash de senhas usando BCrypt
 */
public class UsuarioDAO implements DAO<Usuario> {

    private Connection connection;

    public UsuarioDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public void create(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());

            // Hash da senha antes de salvar
            String senhaHash = PasswordUtil.hashPassword(usuario.getSenha());
            stmt.setString(3, senhaHash);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }

            System.out.println("Usuário criado com senha hash BCrypt.");

        } catch (SQLException e) {
            System.err.println("Erro ao criar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Usuario read(int id) {
        String sql = "SELECT id, nome, email, senha FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Usuario findByEmail(String email) {
        String sql = "SELECT id, nome, email, senha FROM usuario WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Autentica usuário verificando o hash da senha
     */
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = findByEmail(email);

        if (usuario == null) {
            return null; // Usuário não existe
        }

        String senhaArmazenada = usuario.getSenha();

        // Verifica se é uma senha antiga (texto plano) - apenas para migração
        if (!PasswordUtil.isAlreadyHashed(senhaArmazenada)) {
            // Senha antiga em texto plano
            if (senhaArmazenada.equals(senha)) {
                // Atualiza para hash (migração automática)
                System.out.println("AVISO: Migrando senha de texto plano para hash BCrypt para usuário: " + email);
                usuario.setSenha(senha); // Temporariamente em texto plano
                update(usuario); // Vai fazer hash no update
                return usuario;
            }
            return null;
        }

        // Verifica hash BCrypt
        if (PasswordUtil.checkPassword(senha, senhaArmazenada)) {
            return usuario;
        }

        return null; // Senha incorreta
    }

    @Override
    public void update(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());

            // Se a senha não está hasheada, faz o hash
            String senha = usuario.getSenha();
            if (!PasswordUtil.isAlreadyHashed(senha)) {
                senha = PasswordUtil.hashPassword(senha);
            }
            stmt.setString(3, senha);
            stmt.setInt(4, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, email, senha FROM usuario";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os usuários: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    // Seu método de autenticar (adaptado para retornar Usuario)
    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Aqui você deve usar seu PasswordUtil para verificar a senha
                    // if (PasswordUtil.verificarSenha(senha, rs.getString("senha_hash"))) {
                    // Por simplicidade, vou comparar direto (TROQUE ISSO PELA SUA LÓGICA DE HASH)
                    if (senha.equals(rs.getString("senha_hash"))) {
                        return instanciarUsuario(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Seu método de buscar por ID
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instanciarUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // --- MÉTODOS NOVOS ADICIONADOS ---

    /**
     * Conta usuários por um perfil específico.
     * NOVO: Necessário para o DashboardServlet.
     */
    public int contarUsuariosPorPerfil(String perfil) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE perfil = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, perfil);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Exclui um usuário pelo ID.
     * NOVO: Necessário para o UsuarioServlet (action=excluir).
     */
    public void excluirUsuario(int id) {
        // CUIDADO: Isso pode falhar se o usuário tiver chamados (foreign key).
        // A lógica real deveria inativar o usuário (SET status = 'INATIVO')
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar exceção
        }
    }

    /**
     * Lista todos os usuários que são Técnicos.
     * NOVO: Necessário para o Admin atribuir chamados.
     */
    public List<Tecnico> listarTodosTecnicos() {
        List<Tecnico> tecnicos = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE perfil = 'TECNICO'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tecnicos.add((Tecnico) instanciarUsuario(rs)); // Faz o cast
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tecnicos;
    }

    // Helper para instanciar Cliente ou Tecnico com base no perfil
    private Usuario instanciarUsuario(ResultSet rs) throws SQLException {
        String perfil = rs.getString("perfil");
        if ("TECNICO".equals(perfil)) {
            return new Tecnico(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha_hash"),
                    rs.getString("cpf"),
                    rs.getString("especialidade")
            );
        } else {
            // Assume Cliente ou Admin (trate Admin se for separado)
            return new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha_hash"),
                    rs.getString("cpf"),
                    rs.getString("perfil")
            );
        }
    }
}