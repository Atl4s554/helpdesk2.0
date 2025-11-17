package Servlet;

import Controller.ChamadoController;
import Controller.LogController;
import Controller.UsuarioController;
import Model.Usuario;
import Model.mongo.LogSistema;
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

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    private ChamadoController chamadoController;
    private UsuarioController usuarioController;
    private LogController logController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.chamadoController = new ChamadoController();
        this.usuarioController = new UsuarioController();
        this.logController = new LogController();
        // Configura o Gson para formatar datas (importante para os logs)
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"message\":\"Sessão expirada.\"}");
            out.flush();
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        Map<String, Object> stats = new HashMap<>();

        try {
            // Carrega estatísticas com base no perfil
            switch (usuario.getPerfil()) {
                case "ADMIN":
                    stats.put("totalChamadosAbertos", chamadoController.contarChamadosPorStatus("ABERTO"));
                    stats.put("totalChamadosAtendimento", chamadoController.contarChamadosPorStatus("EM_ATENDIMENTO"));
                    stats.put("totalChamadosFechados", chamadoController.contarChamadosPorStatus("FECHADO"));
                    stats.put("totalClientes", usuarioController.contarUsuariosPorPerfil("CLIENTE"));
                    stats.put("totalTecnicos", usuarioController.contarUsuariosPorPerfil("TECNICO"));

                    // Conforme 2.1: Últimos 10 logs
                    List<LogSistema> logs = logController.getLogs(10);
                    stats.put("ultimosLogs", logs);
                    break;

                case "TECNICO":
                    // Conforme 2.2: Estatísticas Pessoais (requer novos métodos nos controllers)
                    // stats.put("atendimentosHoje", atendimentoController.contarAtendimentosPorTecnico(usuario.getId(), "HOJE"));
                    // stats.put("atendimentosMes", atendimentoController.contarAtendimentosPorTecnico(usuario.getId(), "MES"));
                    // stats.put("mediaTempo", atendimentoController.getTempoMedioPorTecnico(usuario.getId()));

                    // Valores mockados (você precisa implementar a lógica no Controller):
                    stats.put("atendimentosHoje", 5);
                    stats.put("atendimentosMes", 38);
                    stats.put("mediaTempo", 42);
                    break;

                case "CLIENTE":
                    // Conforme 2.3: Dashboard do cliente não tem stats, apenas as listas
                    // (que são carregadas em outra view)
                    stats.put("mensagem", "Bem-vindo, Cliente!");
                    break;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(stats));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Erro ao buscar estatísticas: " + e.getMessage() + "\"}");
        }
        out.flush();
    }
}