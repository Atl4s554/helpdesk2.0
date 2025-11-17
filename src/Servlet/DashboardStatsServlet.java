package Servlet;

import DAO.ChamadoDAO;
import DAO.UsuarioDAO;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

// O JavaScript vai chamar esta URL: 'api/stats'
@WebServlet("/api/stats")
public class DashboardStatsServlet extends HttpServlet {

    private ChamadoDAO chamadoDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        this.chamadoDAO = new ChamadoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Responde a requisições GET para buscar as estatísticas.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Buscar os dados usando os DAOs
        int abertos = chamadoDAO.countByStatus("Aberto");
        int emAtendimento = chamadoDAO.countByStatus("Em Atendimento");
        int fechadosHoje = chamadoDAO.countFechadosHoje();
        int totalUsuarios = usuarioDAO.countTotalUsuarios();

        // 2. Montar um objeto (Map) para a resposta
        Map<String, Integer> stats = new HashMap<>();
        stats.put("abertos", abertos);
        stats.put("emAtendimento", emAtendimento);
        stats.put("fechadosHoje", fechadosHoje);
        stats.put("totalUsuarios", totalUsuarios);

        // 3. Converter o Map para JSON usando Gson
        String jsonResponse = new Gson().toJson(stats);

        // 4. Enviar a resposta JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}