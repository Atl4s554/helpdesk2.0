package DAO.mongo;

import Model.mongo.LogSistema;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * DAO para manipular logs do sistema no MongoDB
 */
public class LogDAO {

    private MongoCollection<Document> collection;
    private static final String COLLECTION_NAME = "logs_sistema";

    public LogDAO() {
        MongoDatabase database = MongoConnection.getDatabase();
        if (database != null) {
            this.collection = database.getCollection(COLLECTION_NAME);
        }
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    /**
     * Cria um novo log no MongoDB
     */
    public void create(LogSistema log) {
        try {
            Document doc = logToDocument(log);
            collection.insertOne(doc);

            // Define o ID gerado no objeto
            log.setId(doc.getObjectId("_id"));

        } catch (Exception e) {
            System.err.println("Erro ao criar log no MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Busca um log por ID
     */
    public LogSistema read(ObjectId id) {
        try {
            Document doc = collection.find(eq("_id", id)).first();
            if (doc != null) {
                return documentToLog(doc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler log: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os logs (com limite)
     */
    public List<LogSistema> findAll(int limit) {
        List<LogSistema> logs = new ArrayList<>();
        try {
            collection.find()
                    .sort(descending("timestamp"))
                    .limit(limit)
                    .forEach(doc -> logs.add(documentToLog(doc)));
        } catch (Exception e) {
            System.err.println("Erro ao listar logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Busca logs por usuário
     */
    public List<LogSistema> findByUsuarioId(int usuarioId, int limit) {
        List<LogSistema> logs = new ArrayList<>();
        try {
            collection.find(eq("usuarioId", usuarioId))
                    .sort(descending("timestamp"))
                    .limit(limit)
                    .forEach(doc -> logs.add(documentToLog(doc)));
        } catch (Exception e) {
            System.err.println("Erro ao buscar logs por usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Busca logs por tipo
     */
    public List<LogSistema> findByTipo(String tipo, int limit) {
        List<LogSistema> logs = new ArrayList<>();
        try {
            collection.find(eq("tipo", tipo))
                    .sort(descending("timestamp"))
                    .limit(limit)
                    .forEach(doc -> logs.add(documentToLog(doc)));
        } catch (Exception e) {
            System.err.println("Erro ao buscar logs por tipo: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Busca logs por período
     */
    public List<LogSistema> findByPeriodo(LocalDateTime inicio, LocalDateTime fim, int limit) {
        List<LogSistema> logs = new ArrayList<>();
        try {
            Date dataInicio = Date.from(inicio.atZone(ZoneId.systemDefault()).toInstant());
            Date dataFim = Date.from(fim.atZone(ZoneId.systemDefault()).toInstant());

            collection.find(and(gte("timestamp", dataInicio), lte("timestamp", dataFim)))
                    .sort(descending("timestamp"))
                    .limit(limit)
                    .forEach(doc -> logs.add(documentToLog(doc)));
        } catch (Exception e) {
            System.err.println("Erro ao buscar logs por período: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Conta logs por tipo
     */
    public long countByTipo(String tipo) {
        try {
            return collection.countDocuments(eq("tipo", tipo));
        } catch (Exception e) {
            System.err.println("Erro ao contar logs: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Deleta logs antigos (ex: mais de 6 meses)
     */
    public long deleteOldLogs(LocalDateTime antes) {
        try {
            Date data = Date.from(antes.atZone(ZoneId.systemDefault()).toInstant());
            return collection.deleteMany(lt("timestamp", data)).getDeletedCount();
        } catch (Exception e) {
            System.err.println("Erro ao deletar logs antigos: " + e.getMessage());
            return 0;
        }
    }

    // Métodos auxiliares de conversão

    private Document logToDocument(LogSistema log) {
        Document doc = new Document();

        if (log.getId() != null) {
            doc.append("_id", log.getId());
        }

        doc.append("timestamp", Date.from(log.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()))
                .append("tipo", log.getTipo())
                .append("usuarioId", log.getUsuarioId())
                .append("usuarioNome", log.getUsuarioNome())
                .append("usuarioTipo", log.getUsuarioTipo());

        if (log.getDetalhes() != null && !log.getDetalhes().isEmpty()) {
            doc.append("detalhes", new Document(log.getDetalhes()));
        }

        if (log.getIpAddress() != null) {
            doc.append("ipAddress", log.getIpAddress());
        }

        return doc;
    }

    private LogSistema documentToLog(Document doc) {
        LogSistema log = new LogSistema();

        log.setId(doc.getObjectId("_id"));

        Date timestamp = doc.getDate("timestamp");
        log.setTimestamp(LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault()));

        log.setTipo(doc.getString("tipo"));
        log.setUsuarioId(doc.getInteger("usuarioId"));
        log.setUsuarioNome(doc.getString("usuarioNome"));
        log.setUsuarioTipo(doc.getString("usuarioTipo"));

        Document detalhes = (Document) doc.get("detalhes");
        if (detalhes != null) {
            log.setDetalhes(detalhes);
        }

        log.setIpAddress(doc.getString("ipAddress"));

        return log;
    }
}