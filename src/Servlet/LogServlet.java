package Servlet;

import Controller.LogController;
import Model.Usuario;
import Model.mongo.LogSistema;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/LogServlet")
public class LogServlet extends HttpServlet {

    private LogController logController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.logController = new LogController();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Proteção (só Admin)
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            return;
        }
        Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuarioLogado");
        if (!"ADMIN".equals(usuario.getPerfil())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"message\":\"Acesso negado.\"}");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("listarTodos".equals(action)) {
                // Pega todos os logs (pode ser lento, mas é o que a view pede)
                List<LogSistema> logs = logController.getLogs(0); // 0 = todos
                out.print(gson.toJson(logs));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\":\"Ação GET inválida.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Erro ao listar logs: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}