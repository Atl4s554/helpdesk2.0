package Servlet;

import Controller.AtendimentoController;
import Controller.HistoricoAtendimentoController;
import Model.Tecnico;
import Model.Usuario;
import Model.mongo.HistoricoAtendimento;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/AtendimentoServlet")
public class AtendimentoServlet extends HttpServlet {

    private AtendimentoController atendimentoController;
    private HistoricoAtendimentoController historicoController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.atendimentoController = new AtendimentoController();
        this.historicoController = new HistoricoAtendimentoController();
        // Gson que lida com datas do MongoDB
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
    }

    // GET: Listar Histórico (Mongo)
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
            if ("listarHistorico".equals(action)) {
                int chamadoId = Integer.parseInt(request.getParameter("chamadoId"));
                HistoricoAtendimento historico = historicoController.getHistorico(chamadoId);
                out.print(gson.toJson(historico));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\":\"Ação GET inválida.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Erro ao buscar histórico: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    // POST: Registrar Atendimento
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            return;
        }

        // Apenas Técnicos podem registrar atendimento
        Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuarioLogado");
        if (!"TECNICO".equals(usuario.getPerfil())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"message\":\"Apenas técnicos podem registrar atendimento.\"}");
            return;
        }

        String action = request.getParameter("action");
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, String> data = gson.fromJson(body, HashMap.class);
        Map<String, Object> result = new HashMap<>();

        try {
            if ("registrar".equals(action)) {
                int chamadoId = Integer.parseInt(data.get("chamadoId"));
                int tecnicoId = usuario.getId();
                String descricao = data.get("descricao");
                String novoStatus = data.get("novoStatus");
                int tempoGasto = Integer.parseInt(data.get("tempoGasto"));

                // O método registrarAtendimento no Controller deve:
                // 1. Criar um Atendimento no MySQL
                // 2. Adicionar uma Entrada no HistoricoAtendimentoDAO (Mongo)
                // 3. Atualizar o status do Chamado
                atendimentoController.registrarAtendimento(chamadoId, tecnicoId, descricao, tempoGasto, novoStatus);

                result.put("success", true);
                result.put("message", "Atendimento registrado!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("message", "Ação POST inválida.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("message", "Erro: " + e.getMessage());
            e.printStackTrace();
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}