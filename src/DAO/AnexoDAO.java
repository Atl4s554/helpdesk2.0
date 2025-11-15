package DAO;

import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Definindo o CRUD do Anexo usando o override em cima da classe abstrata
 * e as classe especificas para a entidade anexo **/

public class AnexoDAO implements DAO<Anexo> {

    private Connection connection;

    public AnexoDAO() {
        this.connection = DBConnection.getConnection();
    }



    @Override
    public void create(Anexo anexo) {
        String sql = "INSERT INTO anexo (nome_arquivo, caminho, chamado_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, anexo.getNomeArquivo());
            stmt.setString(2, anexo.getCaminho());
            stmt.setInt(3, anexo.getChamadoId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    anexo.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar anexo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Anexo read(int id) {
        String sql = "SELECT id, nome_arquivo, caminho, chamado_id FROM anexo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnexo(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler anexo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Anexo anexo) {
        String sql = "UPDATE anexo SET nome_arquivo = ?, caminho = ?, chamado_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, anexo.getNomeArquivo());
            stmt.setString(2, anexo.getCaminho());
            stmt.setInt(3, anexo.getChamadoId());
            stmt.setInt(4, anexo.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar anexo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM anexo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar anexo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Anexo> findAll() {
        List<Anexo> anexos = new ArrayList<>();
        String sql = "SELECT id, nome_arquivo, caminho, chamado_id FROM anexo";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                anexos.add(mapResultSetToAnexo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os anexos: " + e.getMessage());
            e.printStackTrace();
        }
        return anexos;
    }

    public List<Anexo> findByChamadoId(int chamadoId) {
        List<Anexo> anexos = new ArrayList<>();
        String sql = "SELECT id, nome_arquivo, caminho, chamado_id FROM anexo WHERE chamado_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chamadoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    anexos.add(mapResultSetToAnexo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar anexos por chamado: " + e.getMessage());
            e.printStackTrace();
        }
        return anexos;
    }

    private Anexo mapResultSetToAnexo(ResultSet rs) throws SQLException {
        Anexo anexo = new Anexo();
        anexo.setId(rs.getInt("id"));
        anexo.setNomeArquivo(rs.getString("nome_arquivo"));
        anexo.setCaminho(rs.getString("caminho"));
        anexo.setChamadoId(rs.getInt("chamado_id"));
        return anexo;
    }
}

