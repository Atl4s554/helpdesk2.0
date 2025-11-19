package Controller.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import DAO.mongo.LogDAO;
import Model.mongo.LogSistema;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogsApiServlet", urlPatterns = {"/api/logs"})
public class LogsApiServlet extends HttpServlet {

    private LogDAO logDAO;

    @Override
    public void init() {
        logDAO = new LogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        // Verifica se o LogDAO conseguiu conectar ao MongoDB
        if (logDAO == null || logDAO.getCollection() == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\":\"Falha ao conectar ao MongoDB.\"}");
            return;
        }

        int limit = 10;
        try {
            if (request.getParameter("limit") != null) {
                limit = Integer.parseInt(request.getParameter("limit"));
            }
        } catch (Exception ignored) {}

        try {
            List<LogSistema> logs = logDAO.findAll(limit);
            Gson gson = new Gson();
            response.getWriter().write(gson.toJson(logs));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"erro\":\"Erro ao buscar logs: " + e.getMessage() + "\"}");
        }
    }

}
