package DAO;

import Model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Definindo o CRUD de Atendimento usando o override em cima da classe abstrata
 * e as classe especificas para a entidade Atendimento **/

public class AtendimentoDAO implements DAO<Atendimento> {

    private Connection connection;

    public AtendimentoDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public void create(Atendimento atendimento) {
        String sql = "INSERT INTO atendimento (descricao, data_atendimento, chamado_id, tecnico_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, atendimento.getDescricao());
            stmt.setTimestamp(2, Timestamp.valueOf(atendimento.getDataAtendimento()));
            stmt.setInt(3, atendimento.getChamadoId());
            stmt.setInt(4, atendimento.getTecnicoId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    atendimento.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Atendimento read(int id) {
        String sql = "SELECT id, descricao, data_atendimento, chamado_id, tecnico_id FROM atendimento WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAtendimento(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler atendimento: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Atendimento atendimento) {
        String sql = "UPDATE atendimento SET descricao = ?, data_atendimento = ?, chamado_id = ?, tecnico_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, atendimento.getDescricao());
            stmt.setTimestamp(2, Timestamp.valueOf(atendimento.getDataAtendimento()));
            stmt.setInt(3, atendimento.getChamadoId());
            stmt.setInt(4, atendimento.getTecnicoId());
            stmt.setInt(5, atendimento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM atendimento WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Atendimento> findAll() {
        List<Atendimento> atendimentos = new ArrayList<>();
        String sql = "SELECT id, descricao, data_atendimento, chamado_id, tecnico_id FROM atendimento";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                atendimentos.add(mapResultSetToAtendimento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os atendimentos: " + e.getMessage());
            e.printStackTrace();
        }
        return atendimentos;
    }

    public List<Atendimento> findByChamadoId(int chamadoId) {
        List<Atendimento> atendimentos = new ArrayList<>();
        String sql = "SELECT id, descricao, data_atendimento, chamado_id, tecnico_id FROM atendimento WHERE chamado_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chamadoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    atendimentos.add(mapResultSetToAtendimento(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar atendimentos por chamado: " + e.getMessage());
            e.printStackTrace();
        }
        return atendimentos;
    }

    private Atendimento mapResultSetToAtendimento(ResultSet rs) throws SQLException {
        Atendimento atendimento = new Atendimento();
        atendimento.setId(rs.getInt("id"));
        atendimento.setDescricao(rs.getString("descricao"));
        atendimento.setDataAtendimento(rs.getTimestamp("data_atendimento").toLocalDateTime());
        atendimento.setChamadoId(rs.getInt("chamado_id"));
        atendimento.setTecnicoId(rs.getInt("tecnico_id"));
        return atendimento;
    }
}

