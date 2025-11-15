package Servlet;

import Controller.UsuarioController;
import Model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet para logout
 * IMPORTANTE: Usa jakarta.servlet (Tomcat 10+)
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private UsuarioController usuarioController;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioController = new UsuarioController();
        System.out.println("✅ LogoutServlet inicializado!");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 LogoutServlet.doGet() chamado!");

        HttpSession session = request.getSession(false);

        if (session != null) {
            // Registra logout no log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                System.out.println("👋 Logout: " + usuario.getNome());
                usuarioController.logout(usuario);
            }

            // Invalida sessão
            session.invalidate();
        }

        // Redireciona para login
        response.sendRedirect("login.html?msg=logout_sucesso");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}