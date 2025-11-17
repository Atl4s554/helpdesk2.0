package DAO;

import Model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Definindo o CRUD do Chamado usando o override em cima da classe abstrata
 * e as classe especificas para a entidade Chamado **/

public class ChamadoDAO implements DAO<Chamado> {

    private Connection connection;

    public ChamadoDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public void create(Chamado chamado) {
        // O script SQL não tem o campo 'prioridade'. Vou ignorá-lo na inserção.
        String sql = "INSERT INTO chamado (titulo, descricao, status, data_abertura, cliente_id, tecnico_id, empresa_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(chamado.getDataAbertura()));
            stmt.setInt(5, chamado.getClienteId());
            if (chamado.getTecnicoId() != null) {
                stmt.setInt(6, chamado.getTecnicoId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setInt(7, chamado.getEmpresaId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    chamado.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar chamado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Chamado read(int id) {
        // O script SQL não tem o campo 'prioridade'. Vou ignorá-lo na leitura.
        String sql = "SELECT id, titulo, descricao, status, data_abertura, data_fechamento, cliente_id, tecnico_id, empresa_id FROM chamado WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChamado(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler chamado: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Chamado chamado) {
        // O script SQL não tem o campo 'prioridade'. Vou ignorá-lo na atualização.
        String sql = "UPDATE chamado SET titulo = ?, descricao = ?, status = ?, data_fechamento = ?, tecnico_id = ?, empresa_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getStatus());
            stmt.setTimestamp(4, chamado.getDataFechamento() != null ? Timestamp.valueOf(chamado.getDataFechamento()) : null);
            if (chamado.getTecnicoId() != null) {
                stmt.setInt(5, chamado.getTecnicoId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, chamado.getEmpresaId());
            stmt.setInt(7, chamado.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar chamado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM chamado WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar chamado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Chamado> findAll() {
        List<Chamado> chamados = new ArrayList<>();
        String sql = "SELECT id, titulo, descricao, status, data_abertura, data_fechamento, cliente_id, tecnico_id, empresa_id FROM chamado";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                chamados.add(mapResultSetToChamado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os chamados: " + e.getMessage());
            e.printStackTrace();
        }
        return chamados;
    }

    private Chamado mapResultSetToChamado(ResultSet rs) throws SQLException {
        Chamado chamado = new Chamado();
        chamado.setId(rs.getInt("id"));
        chamado.setTitulo(rs.getString("titulo"));
        chamado.setDescricao(rs.getString("descricao"));
        chamado.setStatus(rs.getString("status"));
        chamado.setDataAbertura(rs.getTimestamp("data_abertura").toLocalDateTime());
        Timestamp dataFechamentoTs = rs.getTimestamp("data_fechamento");
        chamado.setDataFechamento(dataFechamentoTs != null ? dataFechamentoTs.toLocalDateTime() : null);
        chamado.setClienteId(rs.getInt("cliente_id"));
        int tecnicoId = rs.getInt("tecnico_id");
        chamado.setTecnicoId(rs.wasNull() ? null : tecnicoId);
        chamado.setEmpresaId(rs.getInt("empresa_id"));
        // Prioridade é ignorada pois não está no script SQL
        return chamado;
    }

    /**
     * Conta o número de chamados com um status específico.
     * @param status O status para contar (ex: "Aberto", "Em Atendimento")
     * @return A contagem de chamados.
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM chamado WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Conta o número de chamados fechados hoje.
     * @return A contagem de chamados.
     */
    public int countFechadosHoje() {
        // Usa CURDATE() do MySQL para pegar a data atual
        String sql = "SELECT COUNT(*) FROM chamado WHERE status = 'Fechado' AND DATE(data_fechamento) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

