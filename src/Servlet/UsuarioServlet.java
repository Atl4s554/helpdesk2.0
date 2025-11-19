package Servlet;

import Model.Cliente;
import Model.Tecnico;
import DAO.ClienteDAO;
import DAO.TecnicoDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/api/clientes", "/api/tecnicos"})
public class UsuarioServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TecnicoDAO tecnicoDAO = new TecnicoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getRequestURI();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Verifica a URL requisitada para saber se deve retornar Clientes ou Técnicos
            if (pathInfo.endsWith("/api/clientes")) {
                // Assumindo que o ClienteDAO possui um método listarTodos()
                List<Cliente> clientes = clienteDAO.findAll();
                response.getWriter().write(gson.toJson(clientes));
            } else if (pathInfo.endsWith("/api/tecnicos")) {
                // Assumindo que o TecnicoDAO possui um método listarTodos()
                List<Tecnico> tecnicos = tecnicoDAO.findAll();
                response.getWriter().write(gson.toJson(tecnicos));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                response.getWriter().write("{\"erro\":\"Recurso não encontrado.\"}");
            }
        } catch (Exception e) {
            // Em caso de erro no banco de dados, retorna 500
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\":\"Erro interno do servidor ao carregar dados.\"}");
            e.printStackTrace();
        }
    }
}