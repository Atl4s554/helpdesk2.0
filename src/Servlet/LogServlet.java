package Servlet;

import DAO.mongo.LogDAO;
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

// O JavaScript vai chamar esta URL: 'api/logs'
@WebServlet("/api/logs")
public class LogServlet extends HttpServlet {

    private LogDAO logDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.logDAO = new LogDAO();
        // Configura o Gson para formatar a data corretamente
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
    }

    /**
     * Responde a requisições GET para buscar os logs.
     * Aceita parâmetros de query: ?limite=X&tipo=Y
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Pegar parâmetros da URL
        String tipoLog = req.getParameter("tipo"); // ex: "LOGIN"
        String limiteParam = req.getParameter("limite"); // ex: "5"

        int limite = 50; // Limite padrão
        if (limiteParam != null && !limiteParam.isEmpty()) {
            try {
                limite = Integer.parseInt(limiteParam);
            } catch (NumberFormatException e) {
                // Ignora limite inválido, usa o padrão
            }
        }

        // 2. Buscar os dados do MongoDB
        List<LogSistema> logs;
        if (tipoLog != null && !tipoLog.isEmpty()) {
            // Busca com filtro de tipo
            logs = logDAO.findByTipo(tipoLog, limite);
        } else {
            // Busca os mais recentes sem filtro
            logs = logDAO.findAll(limite);
        }

        // 3. Converter a Lista para JSON
        String jsonResponse = this.gson.toJson(logs);

        // 4. Enviar a resposta JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}