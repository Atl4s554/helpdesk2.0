package View;

import java.util.List;
import java.util.Scanner;
import Controller.EmpresaController;
import Model.Empresa;

//Classe View para interações relacionadas à entidade Empresa.
public class EmpresaView {

    private Scanner scanner;
    private EmpresaController empresaController;

    public EmpresaView(Scanner scanner) {
        this.scanner = scanner;
        this.empresaController = new EmpresaController();
    }

    public void exibirMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- Menu de Empresas ---");
            System.out.println("1. Cadastrar Nova Empresa");
            System.out.println("2. Listar Empresas");
            System.out.println("3. Atualizar Empresa");
            System.out.println("4. Deletar Empresa");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        cadastrarEmpresa();
                        break;
                    case 2:
                        listarEmpresas();
                        break;
                    case 3:
                        atualizarEmpresa();
                        break;
                    case 4:
                        deletarEmpresa();
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: Entrada inválida. Digite um número.");
            }
        }
    }

    private void cadastrarEmpresa() {
        System.out.println("\n--- Cadastro de Nova Empresa ---");
        System.out.print("Nome (Razão Social): ");
        String nome = scanner.nextLine();
        System.out.print("CNPJ: ");
        String cnpj = scanner.nextLine();

        Empresa empresa = new Empresa(nome, cnpj);
        empresaController.cadastrarEmpresa(empresa);
    }

    private void listarEmpresas() {
        List<Empresa> empresas = empresaController.listarEmpresas();
        System.out.println("\n--- Lista de Empresas ---");
        if (empresas.isEmpty()) {
            System.out.println("Nenhuma empresa cadastrada.");
            return;
        }
        for (Empresa empresa : empresas) {
            System.out.println(empresa.toString());
        }
    }

    private void atualizarEmpresa() {
        System.out.println("\n--- Atualizar Empresa ---");
        System.out.print("ID da Empresa a ser atualizada: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Empresa empresa = empresaController.buscarEmpresaPorId(id);

            if (empresa == null) {
                System.out.println("Empresa com ID " + id + " não encontrada.");
                return;
            }

            System.out.println("Empresa atual: " + empresa.toString());
            System.out.print("Novo Nome (deixe em branco para manter '" + empresa.getNome() + "'): ");
            String novoNome = scanner.nextLine();
            if (!novoNome.isEmpty()) {
                empresa.setNome(novoNome);
            }

            System.out.print("Novo CNPJ (deixe em branco para manter '" + empresa.getCnpj() + "'): ");
            String novoCnpj = scanner.nextLine();
            if (!novoCnpj.isEmpty()) {
                empresa.setCnpj(novoCnpj);
            }

            empresaController.atualizarEmpresa(empresa);

        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
        }
    }

    private void deletarEmpresa() {
        System.out.println("\n--- Deletar Empresa ---");
        System.out.print("ID da Empresa a ser deletada: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            empresaController.deletarEmpresa(id);
        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
        }
    }
}

