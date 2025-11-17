package Servlet;

import Controller.UsuarioController;
import Model.Cliente;
import Model.Tecnico;
import Model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet para autenticação de usuários
 * IMPORTANTE: Usa jakarta.servlet (Tomcat 10+)
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioController usuarioController;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioController = new UsuarioController();
        System.out.println("✅ LoginServlet inicializado!");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redireciona para página de login
        response.sendRedirect("login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 LoginServlet.doPost() chamado!");

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        System.out.println("Email recebido: " + email);

        // Valida entrada
        if (email == null || email.trim().isEmpty() ||
                senha == null || senha.trim().isEmpty()) {
            System.out.println("❌ Campos vazios");
            response.sendRedirect("login.html?erro=campos_vazios");
            return;
        }

        // Tenta autenticar
        Usuario usuario = usuarioController.autenticar(email, senha);

        if (usuario != null) {
            // Login bem-sucedido
            System.out.println("✅ Login bem-sucedido: " + usuario.getNome());

            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNome", usuario.getNome());

            // Define tipo de usuário
            String tipoUsuario;
            String paginaDashboard;

            if (usuario instanceof Cliente) {
                tipoUsuario = "CLIENTE";
                paginaDashboard = "dashboard-cliente.html";
            } else if (usuario instanceof Tecnico) {
                Tecnico tecnico = (Tecnico) usuario;
                if ("Administrador".equals(tecnico.getEspecialidade())) {
                    tipoUsuario = "ADMIN";
                    paginaDashboard = "dashboard.html";
                } else {
                    tipoUsuario = "TECNICO";
                    paginaDashboard = "dashboard-tecnico.html";
                }
            } else {
                tipoUsuario = "USUARIO";
                paginaDashboard = "dashboard.html";
            }

            session.setAttribute("tipoUsuario", tipoUsuario);

            System.out.println("🎯 Redirecionando para: " + paginaDashboard);

            // Redireciona para dashboard apropriado
            response.sendRedirect(paginaDashboard);

        } else {
            // Login falhou
            System.out.println("❌ Login falhou para: " + email);
            response.sendRedirect("login.html?erro=credenciais_invalidas");
        }
    }
}