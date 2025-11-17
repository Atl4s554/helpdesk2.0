package Servlet;

import Controller.ChamadoController;
import Model.Cliente;
import Model.Tecnico;
import Model.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/ChamadoServlet")
public class ChamadoServlet extends HttpServlet {

    private ChamadoController chamadoController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.chamadoController = new ChamadoController();
        // Gson que lida com datas
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
    }

    // GET: Listar chamados
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        String action = request.getParameter("action");

        try {
            Object data = null;
            if ("listarTodos".equals(action) && "ADMIN".equals(usuario.getPerfil())) {
                // Admin: Lista tudo
                data = chamadoController.listarTodosChamadosView(); // Você DEVE criar este método no Controller
            } else if ("listarMeus".equals(action) && "CLIENTE".equals(usuario.getPerfil())) {
                // Cliente: Lista seus chamados
                data = chamadoController.listarChamadosPorClienteView(usuario.getId()); // Você DEVE criar este método no Controller
            } else if ("listarAtribuidos".equals(action) && "TECNICO".equals(usuario.getPerfil())) {
                // Tecnico: Lista chamados atribuídos a ele
                data = chamadoController.listarChamadosPorTecnicoView(usuario.getId()); // Você DEVE criar este método no Controller
            } else if ("listarAbertos".equals(action) && "TECNICO".equals(usuario.getPerfil())) {
                // Tecnico: Lista chamados sem técnico
                data = chamadoController.listarChamadosAbertosView(); // Você DEVE criar este método no Controller
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\":\"Ação GET inválida ou não permitida para seu perfil.\"}");
                out.flush();
                return;
            }

            out.print(gson.toJson(data));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Erro ao listar chamados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        out.flush();
    }

    // POST: Criar, Atribuir, Pegar
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        String action = request.getParameter("action");
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, String> data = gson.fromJson(body, HashMap.class);
        Map<String, Object> result = new HashMap<>();

        try {
            if ("criar".equals(action) && "CLIENTE".equals(usuario.getPerfil())) {
                // Cliente abre chamado
                chamadoController.abrirChamado(
                        data.get("titulo"),
                        data.get("descricao"),
                        data.get("prioridade"),
                        usuario.getId(),
                        Integer.parseInt(data.get("idEmpresa"))
                );
                result.put("success", true);
                result.put("message", "Chamado criado com sucesso!");

            } else if ("atribuir".equals(action) && "ADMIN".equals(usuario.getPerfil())) {
                // Admin atribui técnico
                int chamadoId = Integer.parseInt(data.get("chamadoId"));
                int tecnicoId = Integer.parseInt(data.get("tecnicoId"));
                chamadoController.atribuirTecnico(chamadoId, tecnicoId); // Implementar no Controller

                result.put("success", true);
                result.put("message", "Técnico atribuído com sucesso!");

            } else if ("pegar".equals(action) && "TECNICO".equals(usuario.getPerfil())) {
                // Técnico pega chamado (atribui a si mesmo)
                int chamadoId = Integer.parseInt(data.get("chamadoId"));
                int tecnicoId = usuario.getId();
                chamadoController.atribuirTecnico(chamadoId, tecnicoId); // Reutiliza o método

                result.put("success", true);
                result.put("message", "Chamado atribuído a você!");

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("message", "Ação POST inválida ou não permitida.");
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