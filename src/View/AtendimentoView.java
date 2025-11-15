package View;

import java.util.List;
import java.util.Scanner;
import Controller.AtendimentoController;
import Controller.ChamadoController;
import Model.Atendimento;
import Model.Chamado;
import Model.Tecnico;

// Classe View para interações relacionadas ao Atendimento de chamados
public class AtendimentoView {

    private Scanner scanner;
    private AtendimentoController atendimentoController;
    private ChamadoController chamadoController;

    public AtendimentoView(Scanner scanner) {
        this.scanner = scanner;
        this.atendimentoController = new AtendimentoController();
        this.chamadoController = new ChamadoController();
    }

    //Menu para Administrador
    public void exibirMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- Menu de Atendimento (ADMIN) ---");
            System.out.println("1. Registrar Progresso/Solução em Chamado");
            System.out.println("2. Listar Histórico de Atendimentos de um Chamado");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        registrarAtendimento(null);
                        break;
                    case 2:
                        listarAtendimentosPorChamado();
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
            System.out.println("\n--- Menu de Atendimento (TÉCNICO) ---");
            System.out.println("1. Registrar Progresso/Solução em Chamado Atribuído");
            System.out.println("2. Listar Histórico de Atendimentos de um Chamado");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                switch (opcao) {
                    case 1:
                        registrarAtendimento(tecnico);
                        break;
                    case 2:
                        listarAtendimentosPorChamado();
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
    private void registrarAtendimento(Tecnico tecnicoLogado) {
        System.out.println("\n--- Registrar Progresso/Solução ---");
        System.out.print("ID do Chamado: ");
        try {
            int chamadoId = Integer.parseInt(scanner.nextLine());
            Chamado chamado = chamadoController.buscarChamadoPorId(chamadoId);

            if (chamado == null) {
                System.out.println("Chamado não encontrado.");
                return;
            }

            int tecnicoId;
            if (tecnicoLogado != null) {
                tecnicoId = tecnicoLogado.getId();
                // Verifica se o chamado está atribuído a este técnico
                if (chamado.getTecnicoId() == null || !chamado.getTecnicoId().equals(tecnicoId)) {
                    System.out.println("Erro: Este chamado não está atribuído a você. Atribua-o primeiro.");
                    return;
                }
            } else {
                // Modo Admin: permite registrar por qualquer técnico
                System.out.print("ID do Técnico que realizou o atendimento: ");
                tecnicoId = Integer.parseInt(scanner.nextLine());
            }

            System.out.print("Descrição do Atendimento (Progresso ou Solução): ");
            String descricao = scanner.nextLine();

            Atendimento atendimento = new Atendimento();
            atendimento.setChamadoId(chamadoId);
            atendimento.setTecnicoId(tecnicoId);
            atendimento.setDescricao(descricao);

            atendimentoController.registrarAtendimento(atendimento);

        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
        }
    }

    private void listarAtendimentosPorChamado() {
        System.out.println("\n--- Histórico de Atendimentos ---");
        System.out.print("ID do Chamado: ");
        try {
            int chamadoId = Integer.parseInt(scanner.nextLine());
            List<Atendimento> atendimentos = atendimentoController.listarAtendimentosPorChamado(chamadoId);

            if (atendimentos.isEmpty()) {
                System.out.println("Nenhum atendimento registrado para o Chamado " + chamadoId + ".");
                return;
            }

            System.out.println("\nHistórico do Chamado " + chamadoId + ":");
            for (Atendimento atendimento : atendimentos) {
                System.out.println(atendimento.toString());
            }

        } catch (NumberFormatException e) {
            System.out.println("Erro: ID inválido.");
        }
    }
}

