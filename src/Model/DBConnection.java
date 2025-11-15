package Model;

import Utils.ConfigUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe que faz a conexão com o banco de dados MySQL
 * Agora usando configurações externas (config.properties)
 * Esta classe garante que apenas uma instância da conexão seja criada (Singleton)
 */
public class DBConnection {

    private static Connection connection = null;

    // Construtor privado para impedir instanciação
    private DBConnection() {}

    /**
     * Obtém a conexão com o banco de dados
     * Se não existir, cria uma nova
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Carrega o driver JDBC do MySQL em tempo de execução
                Class.forName(ConfigUtil.getMySQLDriver());

                // Cria a conexão usando as configurações do arquivo properties
                connection = DriverManager.getConnection(
                        ConfigUtil.getMySQLUrl(),
                        ConfigUtil.getMySQLUser(),
                        ConfigUtil.getMySQLPassword()
                );

                System.out.println("Conexão MySQL estabelecida com sucesso!");
                System.out.println("Banco: " + ConfigUtil.getMySQLUrl());
                System.out.println("Usuário: " + ConfigUtil.getMySQLUser());

            } catch (ClassNotFoundException e) {
                System.err.println("ERRO: Driver JDBC do MySQL não encontrado.");
                System.err.println("Certifique-se de que mysql-connector-j está no classpath.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("ERRO: Falha ao conectar com o banco de dados MySQL.");
                System.err.println("Verifique se:");
                System.err.println("  1. O MySQL está rodando");
                System.err.println("  2. O banco 'helpdesk' existe");
                System.err.println("  3. O usuário '" + ConfigUtil.getMySQLUser() + "' tem permissões");
                System.err.println("  4. As configurações em config.properties estão corretas");
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados
     * Útil para testes ou shutdown da aplicação
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexão MySQL fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Verifica se a conexão está ativa
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}