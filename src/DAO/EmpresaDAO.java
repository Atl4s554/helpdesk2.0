package DAO;

import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Definindo o CRUD da Empresa usando o override em cima da classe abstrata
 * e as classe especificas para a entidade Empresa **/

public class EmpresaDAO implements DAO<Empresa> {

    private Connection connection;

    public EmpresaDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public void create(Empresa empresa) {
        String sql = "INSERT INTO empresa (nome, cnpj) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    empresa.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Empresa read(int id) {
        String sql = "SELECT id, nome, cnpj FROM empresa WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Empresa empresa = new Empresa();
                    empresa.setId(rs.getInt("id"));
                    empresa.setNome(rs.getString("nome"));
                    empresa.setCnpj(rs.getString("cnpj"));
                    return empresa;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Empresa empresa) {
        String sql = "UPDATE empresa SET nome = ?, cnpj = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setInt(3, empresa.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM empresa WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Empresa> findAll() {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT id, nome, cnpj FROM empresa";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setId(rs.getInt("id"));
                empresa.setNome(rs.getString("nome"));
                empresa.setCnpj(rs.getString("cnpj"));
                empresas.add(empresa);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as empresas: " + e.getMessage());
            e.printStackTrace();
        }
        return empresas;
    }
}

