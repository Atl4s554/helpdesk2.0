package Controller;

import Model.Anexo;
import DAO.AnexoDAO;
import java.util.List;

// Classe que gerencia os anexos de um chamado
public class AnexoController {

    private AnexoDAO anexoDAO;

    // Inicializa a dependência com o AnexoDAO
    public AnexoController() {
        this.anexoDAO = new AnexoDAO();
    }

    // Função que processa a adição de um novo anexo a um chamado existente
    public void adicionarAnexo(Anexo anexo) {
        if (anexo.getChamadoId() <= 0 || anexo.getNomeArquivo() == null || anexo.getNomeArquivo().isEmpty()) {
            System.err.println("Controller: Dados do anexo são inválidos.");
            return;
        }

        try {
            anexoDAO.create(anexo);
            System.out.println("Anexo '" + anexo.getNomeArquivo() + "' adicionado ao chamado " + anexo.getChamadoId() + " com sucesso (ID: " + anexo.getId() + ").");
        } catch (Exception e) {
            System.err.println("Erro ao adicionar anexo: " + e.getMessage());
        }
    }

    public List<Anexo> listarAnexosPorChamado(int chamadoId) {
        return anexoDAO.findByChamadoId(chamadoId);
    }

}