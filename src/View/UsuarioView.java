package View;

import java.util.List;
import java.util.Scanner;
import Controller.UsuarioController;
import Model.Cliente;
import Model.Tecnico;

//Classe View responsável pela interação do usuário com as funcionalidades de gerenciamento de Clientes e Técnicos
public class UsuarioView {

    private Scanner scanner;
    private UsuarioController usuarioController;

    public UsuarioView(Scanner scanner, UsuarioController usuarioController) {
        this.scanner = scanner;
        this.usuarioController = usuarioController;
    }

    public void exibirMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- Menu de Usuários ---");
            System.out.println("1. Cadastrar Novo Cliente");
            System.out.println("2. Cadastrar Novo Técnico");
            System.out.println("3. Listar Clientes");
            System.out.println("4. Listar Técnicos");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        cadastrarCliente();
                        break;
                    case 2:
                        cadastrarTecnico();
                        break;
                    case 3:
                        listarClientes();
                        break;
                    case 4:
                        listarTecnicos();
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

    private void cadastrarCliente() {
        System.out.println("\n--- Cadastro de Novo Cliente ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        Cliente cliente = new Cliente(nome, email, senha, cpf);
        usuarioController.criarCliente(cliente);
    }

    private void cadastrarTecnico() {
        System.out.println("\n--- Cadastro de Novo Técnico ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("Especialidade: ");
        String especialidade = scanner.nextLine();

        Tecnico tecnico = new Tecnico(nome, email, senha, especialidade);
        usuarioController.criarTecnico(tecnico);
    }

    private void listarClientes() {
        List<Cliente> clientes = usuarioController.listarClientes();
        System.out.println("\n--- Lista de Clientes ---");
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        for (Cliente cliente : clientes) {
            System.out.println(cliente.toString());
        }
    }

    private void listarTecnicos() {
        List<Tecnico> tecnicos = usuarioController.listarTecnicos();
        System.out.println("\n--- Lista de Técnicos ---");
        if (tecnicos.isEmpty()) {
            System.out.println("Nenhum técnico cadastrado.");
            return;
        }
        for (Tecnico tecnico : tecnicos) {
            System.out.println(tecnico.toString());
        }
    }
}

