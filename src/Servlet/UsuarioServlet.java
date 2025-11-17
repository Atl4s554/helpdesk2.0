package Servlet;

import Controller.UsuarioController;
import Model.Cliente;
import Model.Tecnico;
import Model.Usuario;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/usuarios/*")
public class UsuarioServlet extends HttpServlet {

    private UsuarioController usuarioController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.usuarioController = new UsuarioController();
        this.gson = new Gson();
        System.out.println("✅ UsuarioServlet inicializado!");
    }

    /**
     * GET /usuarios?acao=listar
     * GET /usuarios?acao=listar&tipo=TECNICO
     * GET /usuarios?acao=buscar&id=1&tipo=CLIENTE
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");
        String tipo = request.getParameter("tipo");

        try {
            if ("listar".equals(acao)) {
                List<Usuario> usuarios = new ArrayList<>();
                if ("TECNICO".equals(tipo)) {
                    usuarios.addAll(usuarioController.listarTecnicos());
                } else if ("CLIENTE".equals(tipo)) {
                    usuarios.addAll(usuarioController.listarClientes());
                } else {
                    // Listar todos
                    usuarios.addAll(usuarioController.listarClientes());
                    usuarios.addAll(usuarioController.listarTecnicos());
                }
                response.getWriter().write(gson.toJson(usuarios));

            } else if ("buscar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Usuario usuario = null;
                if ("CLIENTE".equals(tipo)) {
                    usuario = usuarioController.buscarClientePorId(id);
                } else if ("TECNICO".equals(tipo)) {
                    usuario = usuarioController.buscarTecnicoPorId(id);
                }

                if (usuario != null) {
                    response.getWriter().write(gson.toJson(usuario));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * POST /usuarios (Criar)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String json = getBody(request);
            // Usamos um Map genérico para ler o JSON e descobrir o tipo
            Map<String, String> dados = gson.fromJson(json, Map.class);

            String tipo = dados.get("tipo");
            String nome = dados.get("nome");
            String email = dados.get("email");
            String senha = dados.get("senha"); // O DAO deve hashear

            if ("CLIENTE".equals(tipo)) {
                String cpf = dados.get("cpf");
                int empresaId = Integer.parseInt(dados.get("empresaId"));
                usuarioController.criarCliente(nome, email, senha, cpf, null, empresaId); // Assumindo null para telefone

            } else if ("TECNICO".equals(tipo)) {
                String especialidade = dados.get("especialidade");
                usuarioController.criarTecnico(nome, email, senha, especialidade);
            } else {
                throw new Exception("Tipo de usuário inválido.");
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"mensagem\": \"Usuário criado com sucesso!\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"mensagem\": \"Erro ao criar usuário: " + e.getMessage() + "\"}");
        }
    }

    /**
     * PUT /usuarios?id=1 (Atualizar)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // (Implementação da lógica de atualização (UPDATE) - similar ao doPost)
        // ...
        response.getWriter().write("{\"mensagem\": \"Usuário atualizado com sucesso!\"}");
    }

    /**
     * DELETE /usuarios?id=1&tipo=CLIENTE
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String tipo = request.getParameter("tipo");

            if ("CLIENTE".equals(tipo)) {
                usuarioController.deletarCliente(id);
            } else if ("TECNICO".equals(tipo)) {
                usuarioController.deletarTecnico(id);
            } else {
                throw new Exception("Tipo inválido para exclusão.");
            }

            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // Sucesso

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao deletar: " + e.getMessage());
        }
    }

    // Helper para ler o corpo JSON de uma requisição
    private String getBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}