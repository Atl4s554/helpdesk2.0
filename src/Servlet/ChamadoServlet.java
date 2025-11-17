package Servlet;

import Controller.ChamadoController;
import Model.Chamado;
import Model.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/chamados/*")
public class ChamadoServlet extends HttpServlet {

    private ChamadoController chamadoController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.chamadoController = new ChamadoController();
        // Configura o GSON para formatar a data
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        System.out.println("✅ ChamadoServlet (CRUD) inicializado!");
    }

    /**
     * GET /chamados?acao=listarTodos (Admin)
     * GET /chamados?acao=listarPorCliente (Cliente)
     * GET /chamados?acao=listarPorTecnico (Tecnico)
     * GET /chamados?acao=listarAbertos (Tecnico)
     * GET /chamados?acao=buscar&id=1 (Todos)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado.");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");
        String acao = request.getParameter("acao");

        try {
            List<Chamado> chamados;
            switch (acao) {
                case "listarTodos":
                    chamados = chamadoController.listarTodosChamados();
                    break;
                case "listarPorCliente":
                    chamados = chamadoController.listarChamadosPorCliente(usuarioLogado.getId());
                    break;
                case "listarPorTecnico":
                    chamados = chamadoController.listarChamadosPorTecnico(usuarioLogado.getId());
                    break;
                case "listarAbertos":
                    chamados = chamadoController.listarChamadosAbertos();
                    break;
                case "buscar":
                    int id = Integer.parseInt(request.getParameter("id"));
                    Chamado chamado = chamadoController.buscarChamadoPorId(id);
                    if (chamado != null) {
                        response.getWriter().write(gson.toJson(chamado));
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Chamado não encontrado");
                    }
                    return; // Retorna pois não é uma lista
                default:
                    throw new Exception("Ação inválida.");
            }
            response.getWriter().write(gson.toJson(chamados));

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao buscar chamados: " + e.getMessage());
        }
    }

    /**
     * POST /chamados (Cliente: Abrir novo chamado)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado.");
            return;
        }

        try {
            int clienteId = (int) session.getAttribute("usuarioId");
            String json = getBody(request);
            Chamado chamado = gson.fromJson(json, Chamado.class);

            chamadoController.abrirChamado(
                    chamado.getTitulo(),
                    chamado.getDescricao(),
                    clienteId,
                    chamado.getAnexoId() // Assumindo que o anexoId pode ser nulo
            );

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"mensagem\": \"Chamado aberto com sucesso!\"}");

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro ao abrir chamado: " + e.getMessage());
        }
    }

    /**
     * PUT /chamados?acao=atribuir&id=1&tecnicoId=2 (Admin)
     * PUT /chamados?acao=mudarStatus&id=1&status=EM_ANDAMENTO (Tecnico: Aceitar)
     * PUT /chamados?acao=mudarStatus&id=1&status=CONCLUIDO (Tecnico: Concluir)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado.");
            return;
        }

        String acao = request.getParameter("acao");
        int chamadoId = Integer.parseInt(request.getParameter("id"));

        try {
            if ("atribuir".equals(acao)) {
                int tecnicoId = Integer.parseInt(request.getParameter("tecnicoId"));
                chamadoController.atribuirTecnico(chamadoId, tecnicoId);
                response.getWriter().write("{\"mensagem\": \"Técnico atribuído com sucesso!\"}");

            } else if ("mudarStatus".equals(acao)) {
                String status = request.getParameter("status"); // "EM_ANDAMENTO" ou "CONCLUIDO"
                int tecnicoId = (int) session.getAttribute("usuarioId"); // Pega o técnico logado

                // Se o técnico está aceitando (vindo de ABERTO), atribui a ele primeiro
                if ("EM_ANDAMENTO".equals(status)) {
                    chamadoController.atribuirTecnico(chamadoId, tecnicoId);
                }

                chamadoController.atualizarStatusChamado(chamadoId, status);
                response.getWriter().write("{\"mensagem\": \"Status atualizado com sucesso!\"}");

            } else {
                throw new Exception("Ação PUT inválida.");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro ao atualizar chamado: " + e.getMessage());
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