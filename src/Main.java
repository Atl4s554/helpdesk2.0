import java.util.Scanner;
import View.UsuarioView;
import View.EmpresaView;
import View.ChamadoView;
import View.AtendimentoView;
import Controller.UsuarioController;
import Model.Usuario;
import Model.Cliente;
import Model.Tecnico;

/**
 * Esse é o nosso arquivo Main.java ele é como uma porta de entrada do nosso sistema de chamados.
 * É aqui onde a aplicação é inicializada e o usuário tem o primeiro contato.
 */

public class Main {

    // Essa variável usuarioLogado é super importante. Ela guarda quem tá usando o sistema no momento.
    // Começa como null porque ninguém logou ainda.
    private static Usuario usuarioLogado = null;
    // E esse usuarioController é tipo o cérebro que lida com tudo de usuário (login, criar, etc.).
    // A gente cria ele uma vez só e usa pra sempre (por isso o final).
    private static final UsuarioController usuarioController = new UsuarioController();

    /**
     * Essa é a função main.
     * @param args Argumentos que a gente pode passar pro programa, mas aqui não estamos usando muito.
     */
    public static void main(String[] args) {
        // O Scanner é pra gente conseguir ler o que o usuário digita no teclado.
        Scanner scanner = new Scanner(System.in);

        // Esse while é um loop de autenticação. Ele fica rodando até alguém conseguir logar (usuarioLogado deixar de ser null).
        // Ninguém entra sem se identificar
        while (usuarioLogado == null) {
            System.out.println("\n--- AUTENTICAÇÃO DO SISTEMA ---");
            System.out.println("1. Fazer Login");
            System.out.println("2. Criar Usuário Admin (Apenas para primeiro uso)");
            System.out.println("0. Sair do Sistema");
            System.out.print("Escolha uma opção: ");

            try {
                // A gente tenta ler a opção que o usuário digitou e converter pra número.
                int opcao = Integer.parseInt(scanner.nextLine());

                // O switch verifica qual opção o usuário escolheu.
                switch (opcao) {
                    case 1:
                        // Se for 1, chama a função pra fazer login.
                        fazerLogin(scanner);
                        break;
                    case 2:
                        // Se for 2, chama a função pra criar um admin (bom pra primeira vez que usa o sistema).
                        criarAdmin(scanner);
                        break;
                    case 0:
                        // Se for 0, o usuário quer sair. A gente se despede e encerra o programa.
                        System.out.println("Encerrando o sistema. Até logo!");
                        scanner.close();
                        return; // Sai da função main e o programa acaba.
                    default:
                        // Se digitar algo que não é 0, 1 ou 2, a gente avisa que a opção é inválida.
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            } catch (NumberFormatException e) {
                // Se o usuário digitou texto em vez de número, a gente pega o erro e avisa.
                System.out.println("Erro: Por favor, digite um número válido.");
            }
        }

        // Se o loop de autenticação terminou, significa que alguém logou com sucesso.
        // Aí a gente chama a função pra mostrar o menu principal do sistema.
        exibirMenuPrincipal(scanner);

        // Depois que tudo termina (o usuário fez logout ou saiu), a gente fecha o scanner de novo.
        scanner.close();
    }

    /**
     * Essa função é pra quando o usuário quer entrar no sistema.
     * Ela pede o email e a senha e tenta autenticar.
     * @param scanner O Scanner que a gente usa pra ler as entradas do usuário.
     */
    private static void fazerLogin(Scanner scanner) {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine(); // Pega o email digitado.
        System.out.print("Senha: ");
        String senha = scanner.nextLine(); // Pega a senha digitada.

        // Chama o usuarioController pra ver se o email e a senha batem com algum usuário cadastrado.
        Usuario usuario = usuarioController.autenticar(email, senha);

        if (usuario != null) {
            // Se o usuario não é null, significa que o login deu certo.
            usuarioLogado = usuario; // Guarda quem logou.
            System.out.println("\nLogin bem-sucedido! Bem-vindo(a), " + usuario.getNome() + ".");
        } else {
            // Se for null, deu ruim. Email ou senha errados.
            System.out.println("\nFalha na autenticação. Email ou senha incorretos.");
        }
    }

    /**
     * Essa função é especial, pra criar o primeiro usuário admin do sistema.
     * Só usa ela se o sistema estiver zerado de usuários.
     * @param scanner O Scanner pra pegar os dados do novo admin.
     */
    private static void criarAdmin(Scanner scanner) {
        System.out.println("\n--- CRIAÇÃO DE USUÁRIO ADMINISTRADOR ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        // Cria um novo técnico (que é um tipo de usuário) e já define a especialidade como "Administrador".
        Tecnico admin = new Tecnico(nome, email, senha, "Administrador");
        // Pede pro usuarioController salvar esse novo admin.
        usuarioController.criarTecnico(admin);
        System.out.println("Usuário Administrador criado. Por favor, faça login.");
    }

    /**
     * Depois que o usuário loga, essa função entra em ação pra mostrar o menu principal.
     * Dependendo se é Cliente, Técnico ou Admin, as opções mudam.
     * @param scanner O Scanner pra continuar interagindo com o usuário.
     */
    private static void exibirMenuPrincipal(Scanner scanner) {
        // Aqui a gente cria as "telas" (Views) de cada parte do sistema (Usuário, Empresa, Chamado, Atendimento).
        // É importante passar o mesmo scanner pra todas pra elas poderem ler a entrada do usuário.
        UsuarioView usuarioView = new UsuarioView(scanner, usuarioController);
        EmpresaView empresaView = new EmpresaView(scanner);
        ChamadoView chamadoView = new ChamadoView(scanner);
        AtendimentoView atendimentoView = new AtendimentoView(scanner);

        int opcao = -1; // Começa com -1 pra garantir que o loop vai rodar pelo menos uma vez.

        // Esse loop continua até o usuário escolher a opção de logout (0).
        while (opcao != 0) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            // Mostra quem tá logado e qual o tipo de usuário (Cliente, Técnico ou Admin).
            System.out.println("Usuário Logado: " + usuarioLogado.getNome() + " (" + (usuarioLogado instanceof Cliente ? "Cliente" : (usuarioLogado instanceof Tecnico ? "Técnico" : "Usuário")) + ")");

            // Mostra opções diferentes dependendo do tipo de usuário!
            if (usuarioLogado instanceof Tecnico && ((Tecnico) usuarioLogado).getEspecialidade().equals("Administrador")) {
                // Se for um Técnico Administrador, ele vê todas as opções de gerenciamento.
                System.out.println("1. Gerenciar Usuários (Clientes/Técnicos)");
                System.out.println("2. Gerenciar Empresas");
                System.out.println("3. Gerenciar Chamados (Admin)");
                System.out.println("4. Gerenciar Atendimentos (Admin)");
            } else if (usuarioLogado instanceof Tecnico) {
                // Se for um Técnico comum, ele vê as opções de chamados e atendimentos dele.
                System.out.println("3. Gerenciar Chamados (Técnico)");
                System.out.println("4. Gerenciar Atendimentos (Técnico)");
            } else if (usuarioLogado instanceof Cliente) {
                // Se for um Cliente, ele só pode abrir e acompanhar os próprios chamados.
                System.out.println("3. Abrir/Acompanhar Chamados (Cliente)");
            }

            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());

                // Outro if/else if pra chamar a função certa da View, dependendo do usuário e da opção.
                if (usuarioLogado instanceof Tecnico && ((Tecnico) usuarioLogado).getEspecialidade().equals("Administrador")) {
                    switch (opcao) {
                        case 1:
                            usuarioView.exibirMenu(); // Menu de usuários.
                            break;
                        case 2:
                            empresaView.exibirMenu(); // Menu de empresas.
                            break;
                        case 3:
                            chamadoView.exibirMenu(); // Menu de chamados (admin).
                            break;
                        case 4:
                            atendimentoView.exibirMenu(); // Menu de atendimentos (admin).
                            break;
                        case 0:
                            usuarioLogado = null; // Desloga o usuário.
                            System.out.println("Logout realizado.");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                            break;
                    }
                } else if (usuarioLogado instanceof Tecnico) {
                    switch (opcao) {
                        case 3:
                            chamadoView.exibirMenuTecnico((Tecnico) usuarioLogado); // Menu de chamados (técnico).
                            break;
                        case 4:
                            atendimentoView.exibirMenuTecnico((Tecnico) usuarioLogado); // Menu de atendimentos (técnico).
                            break;
                        case 0:
                            usuarioLogado = null;
                            System.out.println("Logout realizado.");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                            break;
                    }
                } else if (usuarioLogado instanceof Cliente) {
                    switch (opcao) {
                        case 3:
                            chamadoView.exibirMenuCliente((Cliente) usuarioLogado); // Menu de chamados (cliente).
                            break;
                        case 0:
                            usuarioLogado = null;
                            System.out.println("Logout realizado.");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                // De novo, se o usuário digitar algo que não é número, a gente avisa.
                System.out.println("Erro: Por favor, digite um número válido.");
                opcao = -1; // Reseta a opção pra manter o loop ativo.
            }
        }
    }
}

