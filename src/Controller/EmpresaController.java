package Controller;

import Model.Empresa;
import DAO.EmpresaDAO;
import java.util.List;

// Função que gerencia as operações de CRUD para a entidade Empresa
public class EmpresaController {

    private EmpresaDAO empresaDAO;
    private LogController logController;

    public EmpresaController() {
        this.empresaDAO = new EmpresaDAO();
        this.logController = new LogController();
    }

    // Função que processa a requisição para cadastrar uma nova empresa
    public void cadastrarEmpresa(Empresa empresa) {
        try {
            empresaDAO.create(empresa);
            System.out.println("Empresa " + empresa.getNome() + " cadastrada com sucesso (ID: " + empresa.getId() + ").");
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar empresa: " + e.getMessage());
        }
    }

    // Funções que fazem todo o CRUD da parte da empresa
    public List<Empresa> listarEmpresas() {
        return empresaDAO.listarTodas(); // Assume que seu DAO tem 'listarTodas()'
    }

    public void criarEmpresa(String nome, String razaoSocial, String cnpj) {
        Empresa empresa = new Empresa(0, nome, razaoSocial, cnpj);
        empresaDAO.inserir(empresa); // Assume que seu DAO tem 'inserir(empresa)'
        logController.logar(0, LogController.TipoLog.CRIACAO, "Empresa criada: " + nome);
    }

    public Empresa buscarEmpresaPorId(int id) {
        return empresaDAO.read(id);
    }

    public void atualizarEmpresa(Empresa empresa) {
        try {
            empresaDAO.update(empresa);
            System.out.println("Empresa " + empresa.getNome() + " atualizada com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar empresa: " + e.getMessage());
        }
    }

    public void deletarEmpresa(int id) {
        try {
            empresaDAO.delete(id);
            System.out.println("Empresa com ID " + id + " deletada com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao deletar empresa: " + e.getMessage());
        }
    }
}