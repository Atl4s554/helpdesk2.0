package DAO.mongo;

import Utils.ConfigUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Classe que gerencia a conexão com o MongoDB
 * Utiliza Singleton para garantir apenas uma instância.
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
                String dbName = ConfigUtil.getMongoDatabase();

                String user = ConfigUtil.getProperty("db.mongo.user");
                String password = ConfigUtil.getProperty("db.mongo.password");

                String connectionString;

                // === CONEXÃO SEM AUTENTICAÇÃO ===
                if (user == null || user.isEmpty() || password == null || password.isEmpty()) {
                    connectionString = String.format("mongodb://%s:%d", host, port);

                } else {
                    // === CONEXÃO COM AUTENTICAÇÃO CORRETA ===
                    // URI deve incluir o DB para autenticação funcionar
                    connectionString = String.format(
                            "mongodb://%s:%s@%s:%d/%s?authSource=%s",
                            user, password, host, port, dbName, dbName
                    );
                }

                mongoClient = MongoClients.create(connectionString);

                System.out.println("Conexão MongoDB estabelecida com sucesso!");
                System.out.println("Host: " + host + ":" + port);

            } catch (Exception e) {
                System.err.println("ERRO: Falha ao conectar com MongoDB.");
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
     * NOVO MÉTODO — Obtém uma coleção do banco
     */
    public static MongoCollection<Document> getCollection(String collectionName) {
        MongoDatabase db = getDatabase();
        if (db == null) {
            throw new IllegalStateException(
                    "ERRO: Banco MongoDB não inicializado antes de solicitar coleção: " + collectionName
            );
        }
        return db.getCollection(collectionName);
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
     * Testa a conexão usando ping
     */
    public static boolean testConnection() {
        try {
            MongoDatabase db = getDatabase();
            if (db != null) {
                db.runCommand(new Document("ping", 1));
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Teste de conexão MongoDB falhou: " + e.getMessage());
            return false;
        }
    }
}
