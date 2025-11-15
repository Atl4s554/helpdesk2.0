package DAO.mongo;

import Utils.ConfigUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Classe que gerencia a conexão com o MongoDB
 * Singleton pattern para garantir uma única instância
 */
public class MongoConnection {

    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    // Construtor privado
    private MongoConnection() {}

    /**
     * Obtém o cliente MongoDB
     */
    public static MongoClient getClient() {
        if (mongoClient == null) {
            try {
                String host = ConfigUtil.getMongoHost();
                int port = ConfigUtil.getMongoPort();

                // Cria a connection string
                String connectionString = String.format("mongodb://%s:%d", host, port);

                // Se houver usuário e senha configurados, adiciona na string
                String user = ConfigUtil.getProperty("db.mongo.user");
                String password = ConfigUtil.getProperty("db.mongo.password");

                if (user != null && !user.isEmpty() && password != null && !password.isEmpty()) {
                    connectionString = String.format("mongodb://%s:%s@%s:%d", user, password, host, port);
                }

                mongoClient = MongoClients.create(connectionString);

                System.out.println("Conexão MongoDB estabelecida com sucesso!");
                System.out.println("Host: " + host + ":" + port);

            } catch (Exception e) {
                System.err.println("ERRO: Falha ao conectar com MongoDB.");
                System.err.println("Verifique se:");
                System.err.println("  1. O MongoDB está rodando");
                System.err.println("  2. As configurações em config.properties estão corretas");
                e.printStackTrace();
            }
        }
        return mongoClient;
    }

    /**
     * Obtém o banco de dados
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            MongoClient client = getClient();
            if (client != null) {
                String dbName = ConfigUtil.getMongoDatabase();
                database = client.getDatabase(dbName);
                System.out.println("Usando banco MongoDB: " + dbName);
            }
        }
        return database;
    }

    /**
     * Fecha a conexão com o MongoDB
     */
    public static void closeConnection() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                mongoClient = null;
                database = null;
                System.out.println("Conexão MongoDB fechada.");
            } catch (Exception e) {
                System.err.println("Erro ao fechar conexão MongoDB: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Testa a conexão
     */
    public static boolean testConnection() {
        try {
            MongoDatabase db = getDatabase();
            if (db != null) {
                // Tenta executar um comando simples
                db.runCommand(new org.bson.Document("ping", 1));
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Teste de conexão MongoDB falhou: " + e.getMessage());
            return false;
        }
    }
}