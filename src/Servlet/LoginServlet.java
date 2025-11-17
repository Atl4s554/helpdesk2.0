package Servlet;

import Controller.UsuarioController;
import Model.Cliente;
import Model.Tecnico;
import Model.Usuario;
import com.google.gson.Gson; // IMPORTAR
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // IMPORTAR
import java.io.IOException;
import java.util.HashMap; // IMPORTAR
import java.util.Map; // IMPORTAR

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioController usuarioController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.usuarioController = new UsuarioController();
        this.gson = new Gson();
        System.out.println("✅ LoginServlet inicializado!");
    }

    /**
     * MODIFICADO: Agora lida com a verificação de sessão para o JavaScript.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        if ("verificar".equals(acao)) {
            HttpSession session = request.getSession(false);

            if (session != null && session.getAttribute("usuario") != null) {
                // Usuário está logado
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                String tipoUsuario = (String) session.getAttribute("tipoUsuario");

                // Usamos um Map para enviar o nome e o tipo
                Map<String, String> dadosSessao = new HashMap<>();
                dadosSessao.put("nome", usuario.getNome());
                dadosSessao.put("tipo", tipoUsuario);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(this.gson.toJson(dadosSessao));
            } else {
                // Usuário não está logado
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não autenticado.");
            }
        } else {
            response.sendRedirect("login.html");
        }
    }

    /**
     * MODIFICADO: Lógica de autenticação e redirecionamento.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 LoginServlet.doPost() chamado!");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            response.sendRedirect("login.html?erro=campos_vazios");
            return;
        }

        // Tenta autenticar (seu controller já faz a lógica de hash)
        Usuario usuario = usuarioController.autenticar(email, senha);

        if (usuario != null) {
            HttpSession session = request.getSession(); // Cria uma nova sessão
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());

            String tipoUsuario;
            String paginaDashboard;

            if (usuario instanceof Cliente) {
                tipoUsuario = "Cliente";
                paginaDashboard = "dashboard-cliente.html";
            } else if (usuario instanceof Tecnico) {
                Tecnico tecnico = (Tecnico) usuario;
                // Assumindo que seu Main.java usa a especialidade "Administrador"
                if ("Administrador".equalsIgnoreCase(tecnico.getEspecialidade())) {
                    tipoUsuario = "Admin";
                    paginaDashboard = "dashboard-admin.html";
                } else {
                    tipoUsuario = "Técnico";
                    paginaDashboard = "dashboard-tecnico.html";
                }
            } else {
                tipoUsuario = "Admin"; // Fallback
                paginaDashboard = "dashboard-admin.html";
            }

            session.setAttribute("tipoUsuario", tipoUsuario);
            System.out.println("🎯 Login OK. Redirecionando para: " + paginaDashboard);
            response.sendRedirect(paginaDashboard);

        } else {
            System.out.println("❌ Login falhou para: " + email);
            response.sendRedirect("login.html?erro=credenciais_invalidas");
        }
    }
}