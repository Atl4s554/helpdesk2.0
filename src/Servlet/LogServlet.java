package Servlet;

import DAO.mongo.LogDAO;
import Model.mongo.LogSistema;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servlet responsável por consultar logs do MongoDB.
 */
@WebServlet("/api/logs")
public class LogServlet extends HttpServlet {

    private LogDAO logDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        logDAO = new LogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null || action.isEmpty()) {
                listarTodos(request, response);
                return;
            }

            switch (action) {

                case "buscarPorId":
                    buscarPorId(request, response);
                    break;

                case "buscarPorUsuario":
                    buscarPorUsuario(request, response);
                    break;

                case "buscarPorTipo":
                    buscarPorTipo(request, response);
                    break;

                case "buscarPorPeriodo":
                    buscarPorPeriodo(request, response);
                    break;

                default:
                    listarTodos(request, response);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erro", "Erro ao processar solicitação: " + e.getMessage());
            request.getRequestDispatcher("/pages/logs.jsp").forward(request, response);
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private void listarTodos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<LogSistema> logs = logDAO.findAll(200);

        request.setAttribute("logs", logs);
        request.getRequestDispatcher("/pages/logs.jsp").forward(request, response);
    }

    private void buscarPorId(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            request.setAttribute("erro", "ID do log não informado.");
            listarTodos(request, response);
            return;
        }

        try {
            LogSistema log = logDAO.read(new ObjectId(idParam));

            if (log == null) {
                request.setAttribute("erro", "Nenhum log encontrado para este ID.");
            } else {
                request.setAttribute("logUnico", log);
            }

        } catch (Exception e) {
            request.setAttribute("erro", "ID inválido.");
        }

        request.getRequestDispatcher("/pages/logs.jsp").forward(request, response);
    }

    private void buscarPorUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParam = request.getParameter("usuarioId");

        if (userIdParam == null) {
            request.setAttribute("erro", "ID de usuário não informado.");
            listarTodos(request, response);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);

            List<LogSistema> logs = logDAO.findByUsuarioId(userId, 200);

            request.setAttribute("logs", logs);

        } catch (NumberFormatException e) {
            request.setAttribute("erro", "ID do usuário inválido.");
        }

        request.getRequestDispatcher("/pages/logs.jsp").forward(request, response);
    }

    private void buscarPorTipo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipo");

        if (tipo == null || tipo.isEmpty()) {
            request.setAttribute("erro", "Tipo de log não informado.");
            listarTodos(request, response);
            return;
        }

        List<LogSistema> logs = logDAO.findByTipo(tipo, 200);

        request.setAttribute("logs", logs);
        request.getRequestDispatcher("/pages/logs.jsp").forward(request, response);
    }

    private void buscarPorPeriodo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ini = request.getParameter("inicio");
        String fim = request.getParameter("fim");

        if (ini == null || fim == null || ini.isEmpty() || fim.isEmpty()) {
            request.setAttribute("erro", "Período inválido.");
            listarTodos(request, response);
            return;
        }

        try {
            LocalDateTime inicio = LocalDateTime.parse(ini);
            LocalDateTime fimData = LocalDateTime.parse(fim);

            List<LogSistema> logs = logDAO.findByPeriodo(inicio, fimData, 200);

            request.setAttribute("logs", logs);

        } catch (Exception e) {
            request.setAttribute("erro", "Formato de data inválido. Use: yyyy-MM-ddTHH:mm:ss");
        }

        request.getRequestDispatcher("/pages/logs.jsp").forward(request, response);
    }
}
