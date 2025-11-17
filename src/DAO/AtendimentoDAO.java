package DAO;

import Model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Import java.util.Date

/** Definindo o CRUD de Atendimento usando o override em cima da classe abstrata
 * e as classe especificas para a entidade Atendimento **/
public class AtendimentoDAO implements DAO<Atendimento> {

    // O ideal é não manter a conexão como variável de instância.
    // private Connection connection;

    public AtendimentoDAO() {
        // this.connection = DBConnection.getConnection(); // Removido
    }

    /**
     * CORRIGIDO: Este método agora insere todos os campos, incluindo tempo_gasto_min.
     * Ele é o único método de inserção.
     */
    @Override
    public void create(Atendimento atendimento) {
        String sql = "INSERT INTO atendimento (id_chamado, id_tecnico, data_atendimento, descricao, tempo_gasto_min) VALUES (?, ?, ?, ?, ?)";

        // Pega uma conexão nova e fecha automaticamente
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, atendimento.getIdChamado());
            stmt.setInt(2, atendimento.getIdTecnico());
            // CORREÇÃO: Pega o LocalDateTime do modelo e converte para Timestamp
            stmt.setTimestamp(3, Timestamp.valueOf(atendimento.getDataAtendimento()));
            stmt.setString(4, atendimento.getDescricao());
            stmt.setInt(5, atendimento.getTempoGastoMin());
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
        String sql = "SELECT * FROM atendimento WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "UPDATE atendimento SET descricao = ?, data_atendimento = ?, chamado_id = ?, tecnico_id = ?, tempo_gasto_min = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, atendimento.getDescricao());
            stmt.setTimestamp(2, Timestamp.valueOf(atendimento.getDataAtendimento()));
            stmt.setInt(3, atendimento.getIdChamado());
            stmt.setInt(4, atendimento.getIdTecnico());
            stmt.setInt(5, atendimento.getTempoGastoMin());
            stmt.setInt(6, atendimento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM atendimento WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM atendimento";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
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
        String sql = "SELECT * FROM atendimento WHERE chamado_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        // CORREÇÃO: Converte java.sql.Timestamp para java.time.LocalDateTime
        atendimento.setDataAtendimento(rs.getTimestamp("data_atendimento").toLocalDateTime());
        atendimento.setChamadoId(rs.getInt("chamado_id"));
        atendimento.setTecnicoId(rs.getInt("tecnico_id"));

        try {
            atendimento.setTempoGastoMin(rs.getInt("tempo_gasto_min"));
        } catch (SQLException e) {
            atendimento.setTempoGastoMin(0); // Coluna pode não existir
        }
        return atendimento;
    }

    // O método 'inserir' duplicado foi removido.
}