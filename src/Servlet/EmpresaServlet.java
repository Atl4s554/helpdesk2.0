package Servlet;

import Controller.EmpresaController;
import Model.Empresa;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/empresas/*")
public class EmpresaServlet extends HttpServlet {

    private EmpresaController empresaController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.empresaController = new EmpresaController();
        this.gson = new Gson();
        System.out.println("✅ EmpresaServlet inicializado!");
    }

    /**
     * GET /empresas?acao=listar
     * GET /empresas?acao=buscar&id=1
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");

        try {
            if ("listar".equals(acao)) {
                List<Empresa> empresas = empresaController.listarEmpresas();
                response.getWriter().write(gson.toJson(empresas));

            } else if ("buscar".equals(acao)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Empresa empresa = empresaController.buscarEmpresaPorId(id);
                if (empresa != null) {
                    response.getWriter().write(gson.toJson(empresa));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Empresa não encontrada");
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * POST /empresas (Criar)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String json = getBody(request);
            Empresa empresa = gson.fromJson(json, Empresa.class);

            empresaController.criarEmpresa(empresa.getNome(), empresa.getCnpj(), empresa.getEndereco(), empresa.getTelefone());

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"mensagem\": \"Empresa criada com sucesso!\"}");

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro ao criar empresa: " + e.getMessage());
        }
    }

    /**
     * PUT /empresas?id=1 (Atualizar)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String json = getBody(request);
            Empresa empresa = gson.fromJson(json, Empresa.class);

            empresaController.atualizarEmpresa(empresa); // Seu controller precisa deste método

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"mensagem\": \"Empresa atualizada com sucesso!\"}");

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro ao atualizar empresa: " + e.getMessage());
        }
    }

    /**
     * DELETE /empresas?id=1
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            empresaController.deletarEmpresa(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // Sucesso

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao deletar: " + e.getMessage());
        }
    }

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