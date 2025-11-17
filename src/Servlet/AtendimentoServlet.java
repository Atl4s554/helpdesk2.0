package Servlet;

import Controller.AtendimentoController;
import Model.Atendimento;
import Model.Usuario;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/atendimentos")
public class AtendimentoServlet extends HttpServlet {

    private AtendimentoController atendimentoController;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.atendimentoController = new AtendimentoController();
        this.gson = new Gson();
        System.out.println("✅ AtendimentoServlet inicializado!");
    }

    /**
     * POST /atendimentos (Técnico: Registrar novo atendimento)
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
            int tecnicoId = (int) session.getAttribute("usuarioId");
            String json = getBody(request);

            // Usamos um DTO (ou Atendimento) para pegar os dados
            Atendimento dados = gson.fromJson(json, Atendimento.class);

            atendimentoController.criarAtendimento(
                    dados.getDescricao(),
                    dados.getChamadoId(),
                    tecnicoId
            );

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"mensagem\": \"Atendimento registrado com sucesso!\"}");

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro ao registrar atendimento: " + e.getMessage());
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