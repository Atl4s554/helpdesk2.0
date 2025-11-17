package Servlet;

import Controller.EmpresaController;
import Model.Empresa;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/EmpresaServlet")
public class EmpresaServlet extends HttpServlet {

    private EmpresaController empresaController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.empresaController = new EmpresaController();
        this.gson = new Gson();
    }

    // GET: Listar
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("listar".equals(action)) {
                List<Empresa> empresas = empresaController.listarEmpresas();
                out.print(gson.toJson(empresas));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\":\"Ação GET inválida.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Erro ao listar empresas: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // POST: Criar
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            return;
        }

        String action = request.getParameter("action");
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, String> data = gson.fromJson(body, HashMap.class);
        Map<String, Object> result = new HashMap<>();

        try {
            if ("criar".equals(action)) {
                empresaController.criarEmpresa(
                        data.get("nome"), data.get("razaoSocial"), data.get("cnpj")
                );
                result.put("success", true);
                result.put("message", "Empresa criada com sucesso!");
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