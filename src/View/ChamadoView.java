package View;

import java.util.List;
import java.util.Scanner;
import Controller.ChamadoController;
import Controller.EmpresaController;
import Controller.UsuarioController;
import Model.Chamado;
import Model.Cliente;
import Model.Empresa;
import Model.Tecnico;

//Classe View para as interações do usuário com os Chamados.
public class ChamadoView {

    private Scanner scanner;
    private ChamadoController chamadoController;
    private EmpresaController empresaController;
    private UsuarioController usuarioController;

    public ChamadoView(Scanner scanner) {
        this.scanner = scanner;
        this.chamadoController = new ChamadoController();
        this.empresaController = new EmpresaController();
        this.usuarioController = new UsuarioController();
    }

    //Menu para Administrador
    public void exibirMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- Menu de Chamados (ADMIN) ---");
            System.out.println("1. Abrir Novo Chamado");
            System.out.println("2. Listar Todos os Chamados");
            System.out.println("3. Atribuir Técnico a Chamado");
            System.out.println("4. Finalizar Chamado");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        abrirChamado(null);
                        break;
                    case 2:
                        listarChamados(chamadoController.listarTodosChamados());
                        break;
                    case 3:
                        atribuirTecnico();
                        break;
                    case 4:
                        finalizarChamado();
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

    //Menu para Cliente
    public void exibirMenuCliente(Cliente cliente) {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- Menu de Chamados (CLIENTE) ---");
            System.out.println("1. Abrir Novo Chamado");
            System.out.println("2. Acompanhar Meus Chamados");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        abrirChamado(cliente);
                        break;
                    case 2:
                        List<Chamado> todosChamados = chamadoController.listarTodosChamados();
                        List<Chamado> meusChamados = todosChamados.stream()
                                .filter(c -> c.getClienteId() == cliente.getId())
                                .toList();
                        listarChamados(meusChamados);
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

    //Menu para Técnico
    public void exibirMenuTecnico(Tecnico tecnico) {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- Menu de Chamados (TÉCNICO) ---");
            System.out.println("1. Visualizar Chamados Atribuídos a Mim");
            System.out.println("2. Visualizar Todos os Chamados Abertos");
            System.out.println("3. Atualizar Status de Chamado");
            System.out.println("4. Finalizar Chamado");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        List<Chamado> todosChamados = chamadoController.listarTodosChamados();
                        List<Chamado> atribuidos = todosChamados.stream()
                                .filter(c -> c.getTecnicoId() != null && c.getTecnicoId().equals(tecnico.getId()))
                                .toList();
                        listarChamados(atribuidos);
                        break;
                    case 2:
                        List<Chamado> abertos = chamadoController.listarTodosChamados().stream()
                                .filter(c -> c.getStatus().equals("Aberto") || c.getStatus().equals("Em Atendimento"))
                                .toList();
                        listarChamados(abertos);
                        break;
                    case 3:
                        atualizarStatus();
                        break;
                    case 4:
                        finalizarChamado();
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

    //Métodos de Ação
    private void abrirChamado(Cliente clienteLogado) {
        System.out.println("\n--- Abertura de Novo Chamado ---");
        Chamado chamado = new Chamado();

        System.out.print("Título do Chamado: ");
        chamado.setTitulo(scanner.nextLine());
        System.out.print("Descrição do Problema: ");
        chamado.setDescricao(scanner.nextLine());
        System.out.print("Prioridade (Baixa, Média, Alta, Urgente): ");
        chamado.setPrioridade(scanner.nextLine());

        // Define o ID do Cliente
        if (clienteLogado != null) {
            chamado.setClienteId(clienteLogado.getId());
        } else {
            System.out.print("ID do Cliente que está abrindo o chamado: ");
            try {
                chamado.setClienteId(Integer.parseInt(scanner.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("ID de Cliente inválido. Abortando.");
                return;
            }
        }

        // Seleção da Empresa
        List<Empresa> empresas = empresaController.listarEmpresas();
        if (empresas.isEmpty()) {
            System.out.println("Nenhuma empresa cadastrada. Cadastre uma empresa primeiro.");
            return;
        }
        System.out.println("\nEmpresas Cadastradas:");
        empresas.forEach(e -> System.out.println("ID: " + e.getId() + " - " + e.getNome()));
        System.out.print("ID da Empresa relacionada: ");
        try {
            int empresaId = Integer.parseInt(scanner.nextLine());
            if (empresaController.buscarEmpresaPorId(empresaId) == null) {
                System.out.println("Empresa com ID " + empresaId + " não encontrada. Abortando.");
                return;
            }
            chamado.setEmpresaId(empresaId);
        } catch (NumberFormatException e) {
            System.out.println("ID de Empresa inválido. Abortando.");
            return;
        }

        chamadoController.abrirChamado(chamado);
    }

    private void listarChamados(List<Chamado> chamados) {
        System.out.println("\n--- Lista de Chamados ---");
        if (chamados.isEmpty()) {
            System.out.println("Nenhum chamado encontrado.");
            return;
        }
        for (Chamado chamado : chamados) {
            System.out.println(chamado.toString());
        }
    }

    private void atribuirTecnico() {
        System.out.println("\n--- Atribuir Técnico a Chamado ---");
        System.out.print("ID do Chamado: ");
        try {
            int chamadoId = Integer.parseInt(scanner.nextLine());
            Chamado chamado = chamadoController.buscarChamadoPorId(chamadoId);
            if (chamado == null) {
                System.out.println("Chamado não encontrado.");
                return;
            }

            List<Tecnico> tecnicos = usuarioController.listarTecnicos();
            if (tecnicos.isEmpty()) {
                System.out.println("Nenhum técnico cadastrado.");
                return;
            }

            System.out.println("\nTécnicos Disponíveis:");
            tecnicos.forEach(t -> System.out.println("ID: " + t.getId() + " - " + t.getNome() + " (" + t.getEspecialidade() + ")"));
            System.out.print("ID do Técnico para atribuição: ");
            int tecnicoId = Integer.parseInt(scanner.nextLine());

            if (usuarioController.buscarTecnicoPorId(tecnicoId) == null) {
                System.out.println("Técnico não encontrado. Abortando.");
                return;
            }

            chamadoController.atribuirTecnico(chamadoId, tecnicoId);

        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
        }
    }

    private void atualizarStatus() {
        System.out.println("\n--- Atualizar Status de Chamado ---");
        System.out.print("ID do Chamado: ");
        try {
            int chamadoId = Integer.parseInt(scanner.nextLine());
            Chamado chamado = chamadoController.buscarChamadoPorId(chamadoId);
            if (chamado == null) {
                System.out.println("Chamado não encontrado.");
                return;
            }

            System.out.println("Status atual: " + chamado.getStatus());
            System.out.print("Novo Status (Ex: Em Atendimento, Aguardando Cliente, Resolvido): ");
            String novoStatus = scanner.nextLine();

            chamadoController.atualizarStatusChamado(chamadoId, novoStatus);

        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
        }
    }

    private void finalizarChamado() {
        System.out.println("\n--- Finalizar Chamado ---");
        System.out.print("ID do Chamado a ser finalizado: ");
        try {
            int chamadoId = Integer.parseInt(scanner.nextLine());
            chamadoController.finalizarChamado(chamadoId);
        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
        }
    }
}

