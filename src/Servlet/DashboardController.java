// src/Servlet/DashboardController.java

package Servlet;

import DAO.ChamadoDAO;
import DAO.UsuarioDAO;
import DAO.mongo.LogDAO;
import Model.mongo.LogSistema;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/dashboard") // Mapeamento novo e estável.
public class DashboardController extends HttpServlet {

    // Instâncias dos DAOs (garantindo que não haja erro de NullPointer)
    private final ChamadoDAO chamadoDAO = new ChamadoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final LogDAO logDAO = new LogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            // 1. Carregar e Injetar Estatísticas (MySQL)
            // Usamos setAttribute para que o JSP possa ler os dados.
            request.setAttribute("abertos", chamadoDAO.contarAbertos());
            request.setAttribute("emAtendimento", chamadoDAO.contarEmAtendimento());
            request.setAttribute("fechadosHoje", chamadoDAO.contarFechadosHoje());
            request.setAttribute("totalUsuarios", usuarioDAO.contarTotal());

            // 2. Carregar e Injetar Logs (MongoDB)
            List<LogSistema> logs = logDAO.findAll(5);
            request.setAttribute("logsRecentes", logs);

        } catch (Exception e) {
            // Se falhar, definimos valores seguros para evitar o erro 500 no JSP
            System.err.println("ERRO NA CONEXÃO OU BUSCA DE DADOS: " + e.getMessage());
            request.setAttribute("abertos", 0);
            request.setAttribute("emAtendimento", 0);
            request.setAttribute("fechadosHoje", 0);
            request.setAttribute("totalUsuarios", 0);
            request.setAttribute("logsRecentes", Collections.emptyList());
        }

        // 3. Encaminha (Forward) para a View JSP (o view final)
        request.getRequestDispatcher("/dashboard-admin.html").forward(request, response);
    }
}