package Servlet;

import Model.Usuario;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/SessionCheckServlet")
public class SessionCheckServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false); // Não cria uma nova sessão
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        if (session != null && session.getAttribute("usuarioLogado") != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

            // Sucesso: envia os dados do usuário
            Map<String, Usuario> result = new HashMap<>();
            result.put("usuario", usuario);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(result));
        } else {
            // Não autorizado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Sessão não encontrada ou expirada.");
            out.print(gson.toJson(error));
        }
        out.flush();
    }
}