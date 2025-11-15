package DAO;

import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Definindo o CRUD do Cliente usando o override em cima da classe abstrata,
 * as classe especificas para a entidade Cliente e a conexão com o
 * arquivo de usuarioDAO para definir a entidade Cliente**/

public class ClienteDAO implements DAO<Cliente> {

    private Connection connection;
    private UsuarioDAO usuarioDAO;

    public ClienteDAO() {
        this.connection = DBConnection.getConnection();
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public void create(Cliente cliente) {
        // 1. Salva o Usuario base
        usuarioDAO.create(cliente);

        // 2. Salva o Cliente (com o mesmo ID do Usuario)
        String sql = "INSERT INTO cliente (id, cpf) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId());
            stmt.setString(2, cliente.getCpf());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao criar cliente: " + e.getMessage());
            // Em caso de falha, o ideal seria reverter a inserção do Usuario, mas
            // para simplificar o projeto acadêmico, vamos apenas reportar o erro.
            e.printStackTrace();
        }
    }

    @Override
    public Cliente read(int id) {
        // 1. Lê o Usuario base
        Usuario usuario = usuarioDAO.read(id);
        if (usuario == null) return null;

        // 2. Lê os dados específicos do Cliente
        String sql = "SELECT cpf FROM cliente WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId(usuario.getId());
                    cliente.setNome(usuario.getNome());
                    cliente.setEmail(usuario.getEmail());
                    cliente.setSenha(usuario.getSenha());
                    cliente.setCpf(rs.getString("cpf"));
                    return cliente;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Cliente cliente) {
        // 1. Atualiza o Usuario base
        usuarioDAO.update(cliente);

        // 2. Atualiza os dados específicos do Cliente
        String sql = "UPDATE cliente SET cpf = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getCpf());
            stmt.setInt(2, cliente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        // 1. Deleta o Cliente
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar cliente: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Deleta o Usuario base
        usuarioDAO.delete(id);
    }

    @Override
    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, u.email, u.senha, c.cpf FROM usuario u JOIN cliente c ON u.id = c.id";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEmail(rs.getString("email"));
                cliente.setSenha(rs.getString("senha"));
                cliente.setCpf(rs.getString("cpf"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os clientes: " + e.getMessage());
            e.printStackTrace();
        }
        return clientes;
    }
}

