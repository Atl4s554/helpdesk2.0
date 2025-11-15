package DAO;

import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Definindo o CRUD do Tecnico usando o override em cima da classe abstrata,
 * as classe especificas para a entidade Tecnico e a conexão com o
 * arquivo de usuarioDAO para definir a entidade Tecnico**/

public class TecnicoDAO implements DAO<Tecnico> {

    private Connection connection;
    private UsuarioDAO usuarioDAO;

    public TecnicoDAO() {
        this.connection = DBConnection.getConnection();
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public void create(Tecnico tecnico) {
        // 1. Salva o Usuario base
        usuarioDAO.create(tecnico);

        // 2. Salva o Tecnico (com o mesmo ID do Usuario)
        String sql = "INSERT INTO tecnico (id, especialidade) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tecnico.getId());
            stmt.setString(2, tecnico.getEspecialidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao criar técnico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Tecnico read(int id) {
        // 1. Lê o Usuario base
        Usuario usuario = usuarioDAO.read(id);
        if (usuario == null) return null;

        // 2. Lê os dados específicos do Tecnico
        String sql = "SELECT especialidade FROM tecnico WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Tecnico tecnico = new Tecnico();
                    tecnico.setId(usuario.getId());
                    tecnico.setNome(usuario.getNome());
                    tecnico.setEmail(usuario.getEmail());
                    tecnico.setSenha(usuario.getSenha());
                    tecnico.setEspecialidade(rs.getString("especialidade"));
                    return tecnico;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler técnico: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Tecnico tecnico) {
        // 1. Atualiza o Usuario base
        usuarioDAO.update(tecnico);

        // 2. Atualiza os dados específicos do Tecnico
        String sql = "UPDATE tecnico SET especialidade = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tecnico.getEspecialidade());
            stmt.setInt(2, tecnico.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar técnico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        // 1. Deleta o Tecnico
        String sql = "DELETE FROM tecnico WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar técnico: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Deleta o Usuario base
        usuarioDAO.delete(id);
    }

    @Override
    public List<Tecnico> findAll() {
        List<Tecnico> tecnicos = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, u.email, u.senha, t.especialidade FROM usuario u JOIN tecnico t ON u.id = t.id";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tecnico tecnico = new Tecnico();
                tecnico.setId(rs.getInt("id"));
                tecnico.setNome(rs.getString("nome"));
                tecnico.setEmail(rs.getString("email"));
                tecnico.setSenha(rs.getString("senha"));
                tecnico.setEspecialidade(rs.getString("especialidade"));
                tecnicos.add(tecnico);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os técnicos: " + e.getMessage());
            e.printStackTrace();
        }
        return tecnicos;
    }
}

