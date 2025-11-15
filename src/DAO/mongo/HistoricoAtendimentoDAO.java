package DAO.mongo;

import Model.mongo.HistoricoAtendimento;
import Model.mongo.HistoricoAtendimento.EntradaAtendimento;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

/**
 * DAO para manipular histórico de atendimentos no MongoDB
 */
public class HistoricoAtendimentoDAO {

    private MongoCollection<Document> collection;
    private static final String COLLECTION_NAME = "historico_atendimentos";

    public HistoricoAtendimentoDAO() {
        MongoDatabase database = MongoConnection.getDatabase();
        if (database != null) {
            this.collection = database.getCollection(COLLECTION_NAME);
        }
    }

    /**
     * Cria um novo histórico
     */
    public void create(HistoricoAtendimento historico) {
        try {
            Document doc = historicoToDocument(historico);
            collection.insertOne(doc);
            historico.setId(doc.getObjectId("_id"));
        } catch (Exception e) {
            System.err.println("Erro ao criar histórico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Busca histórico por ID do chamado
     */
    public HistoricoAtendimento findByChamadoId(int chamadoId) {
        try {
            Document doc = collection.find(eq("chamadoId", chamadoId)).first();
            if (doc != null) {
                return documentToHistorico(doc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adiciona uma nova entrada de atendimento ao histórico
     */
    public void adicionarAtendimento(int chamadoId, EntradaAtendimento entrada) {
        try {
            HistoricoAtendimento historico = findByChamadoId(chamadoId);

            if (historico == null) {
                System.err.println("Histórico não encontrado para chamado " + chamadoId);
                return;
            }

            // Adiciona a entrada
            historico.adicionarAtendimento(entrada);

            // Atualiza no MongoDB
            Document entradaDoc = entradaToDocument(entrada);

            collection.updateOne(
                    eq("chamadoId", chamadoId),
                    combine(
                            push("atendimentos", entradaDoc),
                            set("totalTempoMinutos", historico.getTotalTempoMinutos())
                    )
            );

        } catch (Exception e) {
            System.err.println("Erro ao adicionar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fecha o histórico (marca data de fechamento)
     */
    public void fecharHistorico(int chamadoId) {
        try {
            Date dataFechamento = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

            collection.updateOne(
                    eq("chamadoId", chamadoId),
                    set("dataFechamento", dataFechamento)
            );

        } catch (Exception e) {
            System.err.println("Erro ao fechar histórico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lista históricos por cliente
     */
    public List<HistoricoAtendimento> findByClienteId(int clienteId) {
        List<HistoricoAtendimento> historicos = new ArrayList<>();
        try {
            collection.find(eq("clienteId", clienteId))
                    .forEach(doc -> historicos.add(documentToHistorico(doc)));
        } catch (Exception e) {
            System.err.println("Erro ao buscar históricos por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return historicos;
    }

    /**
     * Calcula tempo médio de resolução
     */
    public double calcularTempoMedioResolucao() {
        try {
            List<Document> pipeline = List.of(
                    new Document("$match", new Document("dataFechamento", new Document("$ne", null))),
                    new Document("$group", new Document("_id", null)
                            .append("mediaMinutos", new Document("$avg", "$totalTempoMinutos")))
            );

            Document resultado = collection.aggregate(pipeline).first();
            if (resultado != null) {
                return resultado.getDouble("mediaMinutos");
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular tempo médio: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    // Métodos auxiliares de conversão

    private Document historicoToDocument(HistoricoAtendimento historico) {
        Document doc = new Document();

        if (historico.getId() != null) {
            doc.append("_id", historico.getId());
        }

        doc.append("chamadoId", historico.getChamadoId())
                .append("chamadoTitulo", historico.getChamadoTitulo())
                .append("dataAbertura", Date.from(historico.getDataAbertura().atZone(ZoneId.systemDefault()).toInstant()))
                .append("clienteId", historico.getClienteId())
                .append("clienteNome", historico.getClienteNome())
                .append("totalTempoMinutos", historico.getTotalTempoMinutos());

        if (historico.getDataFechamento() != null) {
            doc.append("dataFechamento", Date.from(historico.getDataFechamento().atZone(ZoneId.systemDefault()).toInstant()));
        }

        // Converte lista de atendimentos
        List<Document> atendimentosDoc = new ArrayList<>();
        for (EntradaAtendimento entrada : historico.getAtendimentos()) {
            atendimentosDoc.add(entradaToDocument(entrada));
        }
        doc.append("atendimentos", atendimentosDoc);

        return doc;
    }

    private Document entradaToDocument(EntradaAtendimento entrada) {
        Document doc = new Document();
        doc.append("data", Date.from(entrada.getData().atZone(ZoneId.systemDefault()).toInstant()))
                .append("tecnicoId", entrada.getTecnicoId())
                .append("tecnicoNome", entrada.getTecnicoNome())
                .append("descricao", entrada.getDescricao())
                .append("tempoGastoMinutos", entrada.getTempoGastoMinutos());

        if (entrada.getStatus() != null) {
            doc.append("status", entrada.getStatus());
        }

        return doc;
    }

    private HistoricoAtendimento documentToHistorico(Document doc) {
        HistoricoAtendimento historico = new HistoricoAtendimento();

        historico.setId(doc.getObjectId("_id"));
        historico.setChamadoId(doc.getInteger("chamadoId"));
        historico.setChamadoTitulo(doc.getString("chamadoTitulo"));

        Date dataAbertura = doc.getDate("dataAbertura");
        historico.setDataAbertura(LocalDateTime.ofInstant(dataAbertura.toInstant(), ZoneId.systemDefault()));

        Date dataFechamento = doc.getDate("dataFechamento");
        if (dataFechamento != null) {
            historico.setDataFechamento(LocalDateTime.ofInstant(dataFechamento.toInstant(), ZoneId.systemDefault()));
        }

        historico.setClienteId(doc.getInteger("clienteId"));
        historico.setClienteNome(doc.getString("clienteNome"));
        historico.setTotalTempoMinutos(doc.getInteger("totalTempoMinutos", 0));

        // Converte atendimentos
        List<Document> atendimentosDoc = (List<Document>) doc.get("atendimentos");
        if (atendimentosDoc != null) {
            for (Document entradaDoc : atendimentosDoc) {
                historico.getAtendimentos().add(documentToEntrada(entradaDoc));
            }
        }

        return historico;
    }

    private EntradaAtendimento documentToEntrada(Document doc) {
        EntradaAtendimento entrada = new EntradaAtendimento();

        Date data = doc.getDate("data");
        entrada.setData(LocalDateTime.ofInstant(data.toInstant(), ZoneId.systemDefault()));

        entrada.setTecnicoId(doc.getInteger("tecnicoId"));
        entrada.setTecnicoNome(doc.getString("tecnicoNome"));
        entrada.setDescricao(doc.getString("descricao"));
        entrada.setTempoGastoMinutos(doc.getInteger("tempoGastoMinutos", 0));
        entrada.setStatus(doc.getString("status"));

        return entrada;
    }
}