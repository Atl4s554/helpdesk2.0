package Servlet;

import Controller.UsuarioController;
import Model.Cliente;
import Model.Tecnico;
import Model.Usuario;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    private UsuarioController usuarioController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.usuarioController = new UsuarioController();
        this.gson = new Gson();
    }

    // GET: Usado para LISTAR
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Proteção simples
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            out.flush();
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("listar".equals(action)) {
                // Lista todos (para o Admin)
                List<Usuario> usuarios = usuarioController.listarTodosClientesETecnicos(); // Você precisa criar este método no Controller
                out.print(gson.toJson(usuarios));
            } else if ("listarTecnicos".equals(action)) {
                // Lista apenas técnicos (para atribuir chamado)
                List<Tecnico> tecnicos = usuarioController.listarTodosTecnicos(); // Você precisa criar este método
                out.print(gson.toJson(tecnicos));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\":\"Ação GET inválida.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Erro ao listar usuários: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // POST: Usado para CRIAR, EDITAR, EXCLUIR
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            out.flush();
            return;
        }

        String action = request.getParameter("action");
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, String> data = gson.fromJson(body, HashMap.class);
        Map<String, Object> result = new HashMap<>();

        try {
            if ("criar".equals(action)) {
                String perfil = data.get("perfil");
                if ("CLIENTE".equals(perfil)) {
                    usuarioController.criarCliente(
                            data.get("nome"), data.get("email"), data.get("senha"), data.get("cpf")
                    );
                } else if ("TECNICO".equals(perfil)) {
                    usuarioController.criarTecnico(
                            data.get("nome"), data.get("email"), data.get("senha"), data.get("cpf"), data.get("especialidade")
                    );
                } else {
                    throw new Exception("Perfil de usuário inválido.");
                }
                result.put("success", true);
                result.put("message", "Usuário criado com sucesso!");

            } else if ("excluir".equals(action)) {
                int id = Integer.parseInt(data.get("id"));
                usuarioController.excluirUsuario(id); // Implementar no Controller

                result.put("success", true);
                result.put("message", "Usuário excluído com sucesso!");

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("message", "Ação POST inválida.");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("message", "Erro: " + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}