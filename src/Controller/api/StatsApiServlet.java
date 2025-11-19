package Controller.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import DAO.mongo.LogDAO;
import com.google.gson.JsonObject;

import java.io.IOException;

@WebServlet(name = "StatsApiServlet", urlPatterns = {"/api/stats"})
public class StatsApiServlet extends HttpServlet {

    private LogDAO logDAO;

    @Override
    public void init() {
        logDAO = new LogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        JsonObject json = new JsonObject();
        json.addProperty("erros", logDAO.countByTipo("ERRO"));
        json.addProperty("avisos", logDAO.countByTipo("AVISO"));
        json.addProperty("info", logDAO.countByTipo("INFO"));

        response.getWriter().write(json.toString());
    }
}
