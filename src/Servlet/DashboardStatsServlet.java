package Servlet;

import DAO.ChamadoDAO;
import DAO.UsuarioDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class DashboardStatsServlet extends HttpServlet {

    private final Gson gson = new Gson();
    // Instâncias dos DAOs necessários para buscar as contagens
    private final ChamadoDAO chamadoDAO = new ChamadoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 1. Busca os dados de contagem (Ajuste os nomes dos métodos se forem diferentes)
            int abertos = chamadoDAO.contarAbertos();
            int emAtendimento = chamadoDAO.contarEmAtendimento();
            int fechadosHoje = chamadoDAO.contarFechadosHoje();
            int totalUsuarios = usuarioDAO.contarTotal(); // Ou outro método que conte todos os usuários

            // 2. Cria o objeto Map com as chaves EXATAS esperadas pelo JavaScript
            Map<String, Integer> stats = new HashMap<>();
            stats.put("abertos", abertos);
            stats.put("emAtendimento", emAtendimento);
            stats.put("fechadosHoje", fechadosHoje);
            stats.put("totalUsuarios", totalUsuarios);

            // 3. Serializa para JSON e envia
            String jsonStats = gson.toJson(stats);
            response.getWriter().write(jsonStats);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"erro\":\"Erro interno ao carregar estatísticas.\"}");
            e.printStackTrace();
        }
    }
}