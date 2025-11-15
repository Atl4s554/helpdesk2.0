package Servlet;

import Controller.ChamadoController;
import Model.Chamado;
import Model.Usuario;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet para operações CRUD de Chamados
 * IMPORTANTE: Usa jakarta.servlet (Tomcat 10+)
 */
@WebServlet("/chamados/*")
public class ChamadoServlet extends HttpServlet {

    private ChamadoController chamadoController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.chamadoController = new ChamadoController();
        this.gson = new Gson();
        System.out.println("✅ ChamadoServlet inicializado!");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 ChamadoServlet.doGet() chamado!");

        // Verifica autenticação
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Listar todos os chamados
            List<Chamado> chamados = chamadoController.listarTodosChamados();
            out.print(gson.toJson(chamados));
            System.out.println("📋 Listando " + chamados.size() + " chamados");

        } else {
            // Buscar chamado específico por ID
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                Chamado chamado = chamadoController.buscarChamadoPorId(id);

                if (chamado != null) {
                    out.print(gson.toJson(chamado));
                    System.out.println("✅ Chamado " + id + " encontrado");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Chamado não encontrado");
                    System.out.println("❌ Chamado " + id + " não encontrado");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
            }
        }

        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 ChamadoServlet.doPost() chamado!");

        // Verifica autenticação
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado");
            return;
        }

        // Criar novo chamado
        String titulo = request.getParameter("titulo");
        String descricao = request.getParameter("descricao");
        String prioridade = request.getParameter("prioridade");
        String empresaIdStr = request.getParameter("empresaId");

        // Validação
        if (titulo == null || titulo.trim().isEmpty() ||
                descricao == null || descricao.trim().isEmpty() ||
                empresaIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dados incompletos");
            return;
        }

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            int empresaId = Integer.parseInt(empresaIdStr);

            Chamado chamado = new Chamado();
            chamado.setTitulo(titulo);
            chamado.setDescricao(descricao);
            chamado.setPrioridade(prioridade != null ? prioridade : "Média");
            chamado.setClienteId(usuario.getId());
            chamado.setEmpresaId(empresaId);

            chamadoController.abrirChamado(chamado);

            System.out.println("✅ Chamado criado: " + chamado.getId());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(chamado));
            out.flush();

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de empresa inválido");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 ChamadoServlet.doPut() chamado!");

        // Verifica autenticação
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do chamado não fornecido");
            return;
        }

        try {
            int chamadoId = Integer.parseInt(pathInfo.substring(1));
            String acao = request.getParameter("acao");

            if ("atribuir".equals(acao)) {
                // Atribuir técnico
                int tecnicoId = Integer.parseInt(request.getParameter("tecnicoId"));
                chamadoController.atribuirTecnico(chamadoId, tecnicoId);
                System.out.println("✅ Técnico " + tecnicoId + " atribuído ao chamado " + chamadoId);

            } else if ("atualizar_status".equals(acao)) {
                // Atualizar status
                String novoStatus = request.getParameter("status");
                chamadoController.atualizarStatusChamado(chamadoId, novoStatus);
                System.out.println("✅ Status do chamado " + chamadoId + " atualizado para " + novoStatus);

            } else if ("finalizar".equals(acao)) {
                // Finalizar chamado
                chamadoController.finalizarChamado(chamadoId);
                System.out.println("✅ Chamado " + chamadoId + " finalizado");
            }

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros inválidos");
        }
    }
}