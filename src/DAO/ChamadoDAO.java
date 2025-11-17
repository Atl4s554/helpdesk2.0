package DAO;

import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.Chamado;
import Model.DBConnection;
import Model.dto.ChamadoViewDTO; // Importe o novo DTO
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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

    // Seu método de inserir (abrir chamado)
    public void abrirChamado(Chamado chamado) {
        String sql = "INSERT INTO chamado (titulo, descricao, status, prioridade, data_abertura, id_cliente, id_empresa) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getStatus());
            stmt.setString(4, chamado.getPrioridade());
            stmt.setTimestamp(5, new Timestamp(chamado.getDataAbertura().getTime()));
            stmt.setInt(6, chamado.getIdCliente());
            stmt.setInt(7, chamado.getIdEmpresa());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar exceção
        }
    }

    // --- MÉTODOS NOVOS ADICIONADOS ---

    /**
     * Conta o número de chamados com um status específico.
     * NOVO: Necessário para o DashboardServlet.
     */
    public int contarChamadosPorStatus(String status) {
        String sql = "SELECT COUNT(*) FROM chamado WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
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
     * Atribui um técnico a um chamado e muda o status para "EM_ATENDIMENTO".
     * NOVO: Necessário para o ChamadoServlet (ações 'atribuir' e 'pegar').
     */
    public void atribuirTecnico(int chamadoId, int tecnicoId) {
        String sql = "UPDATE chamado SET id_tecnico = ?, status = 'EM_ATENDIMENTO' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tecnicoId);
            stmt.setInt(2, chamadoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar exceção
        }
    }

    /**
     * Atualiza o status de um chamado.
     * NOVO: Necessário para o AtendimentoController.
     */
    public void atualizarStatus(int chamadoId, String novoStatus) {
        String sql = "UPDATE chamado SET status = ? WHERE id = ?";

        // Se o status for FECHADO, atualiza também a data_fechamento
        if ("FECHADO".equalsIgnoreCase(novoStatus)) {
            sql = "UPDATE chamado SET status = ?, data_fechamento = ? WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            if ("FECHADO".equalsIgnoreCase(novoStatus)) {
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.setInt(3, chamadoId);
            } else {
                stmt.setInt(2, chamadoId);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar exceção
        }
    }

    // Query base para buscar ChamadoViewDTO
    private final String BASE_VIEW_QUERY =
            "SELECT c.*, cli.nome as nome_cliente, tec.nome as nome_tecnico, e.nome as nome_empresa " +
                    "FROM chamado c " +
                    "JOIN usuario cli ON c.id_cliente = cli.id " +
                    "JOIN empresa e ON c.id_empresa = e.id " +
                    "LEFT JOIN usuario tec ON c.id_tecnico = tec.id "; // LEFT JOIN para técnico (pode ser nulo)

    /**
     * Lista TODOS os chamados com nomes (Admin).
     * NOVO: Necessário para o ChamadoServlet (action=listarTodos).
     */
    public List<ChamadoViewDTO> listarTodosChamadosView() {
        List<ChamadoViewDTO> chamados = new ArrayList<>();
        String sql = BASE_VIEW_QUERY + "ORDER BY c.data_abertura DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                chamados.add(instanciarChamadoViewDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chamados;
    }

    /**
     * Lista chamados de um cliente específico (Cliente).
     * NOVO: Necessário para o ChamadoServlet (action=listarMeus).
     */
    public List<ChamadoViewDTO> listarChamadosPorClienteView(int clienteId) {
        List<ChamadoViewDTO> chamados = new ArrayList<>();
        String sql = BASE_VIEW_QUERY + "WHERE c.id_cliente = ? ORDER BY c.data_abertura DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chamados.add(instanciarChamadoViewDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chamados;
    }

    /**
     * Lista chamados atribuídos a um técnico (Técnico).
     * NOVO: Necessário para o ChamadoServlet (action=listarAtribuidos).
     */
    public List<ChamadoViewDTO> listarChamadosPorTecnicoView(int tecnicoId) {
        List<ChamadoViewDTO> chamados = new ArrayList<>();
        String sql = BASE_VIEW_QUERY + "WHERE c.id_tecnico = ? AND c.status != 'FECHADO' ORDER BY c.prioridade, c.data_abertura";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tecnicoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chamados.add(instanciarChamadoViewDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chamados;
    }

    /**
     * Lista chamados ABERTOS e sem técnico (Técnico).
     * NOVO: Necessário para o ChamadoServlet (action=listarAbertos).
     */
    public List<ChamadoViewDTO> listarChamadosAbertosView() {
        List<ChamadoViewDTO> chamados = new ArrayList<>();
        String sql = BASE_VIEW_QUERY + "WHERE c.status = 'ABERTO' AND c.id_tecnico IS NULL ORDER BY c.prioridade, c.data_abertura";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                chamados.add(instanciarChamadoViewDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chamados;
    }

    /**
     * Helper para criar o DTO a partir do ResultSet.
     * NOVO: Usado pelos métodos ...View().
     */
    private ChamadoViewDTO instanciarChamadoViewDTO(ResultSet rs) throws SQLException {
        // 1. Cria o objeto Chamado base
        Chamado chamado = new Chamado(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("status"),
                rs.getString("prioridade"),
                rs.getTimestamp("data_abertura"),
                rs.getTimestamp("data_fechamento"),
                rs.getInt("id_cliente"),
                rs.getInt("id_tecnico"), // id_tecnico pode ser 0 se for nulo, o rs.getInt() trata isso
                rs.getInt("id_empresa")
        );

        // 2. Pega os campos do JOIN
        String nomeCliente = rs.getString("nome_cliente");
        String nomeTecnico = rs.getString("nome_tecnico"); // Pode ser nulo
        String nomeEmpresa = rs.getString("nome_empresa");

        // 3. Retorna o DTO
        return new ChamadoViewDTO(chamado, nomeCliente, nomeTecnico, nomeEmpresa);
    }
}
