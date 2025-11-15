package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilitário para carregar configurações do arquivo config.properties
 * Evita credenciais hardcoded no código
 */
public class ConfigUtil {

    private static Properties properties = null;
    private static final String CONFIG_FILE = "config.properties";

    // Carrega o arquivo de configuração apenas uma vez
    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();

        try {
            // Tenta carregar do classpath primeiro
            InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

            // Se não encontrar, tenta carregar do diretório raiz
            if (input == null) {
                input = new FileInputStream(CONFIG_FILE);
            }

            properties.load(input);
            System.out.println("Configurações carregadas com sucesso de: " + CONFIG_FILE);

        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar " + CONFIG_FILE);
            System.err.println("Certifique-se de que o arquivo existe no classpath ou no diretório raiz.");
            e.printStackTrace();

            // Define valores padrão de fallback (não recomendado para produção)
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        System.out.println("Usando configurações padrão (INSEGURO - apenas para desenvolvimento)");
        properties.setProperty("db.mysql.url", "jdbc:mysql://localhost:3306/helpdesk");
        properties.setProperty("db.mysql.user", "root");
        properties.setProperty("db.mysql.password", "potato123");
        properties.setProperty("db.mysql.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("db.mongo.host", "localhost");
        properties.setProperty("db.mongo.port", "27017");
        properties.setProperty("db.mongo.database", "helpdesk_logs");
        properties.setProperty("security.bcrypt.rounds", "12");
    }

    /**
     * Obtém uma propriedade do arquivo de configuração
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Obtém uma propriedade com valor padrão
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // Métodos específicos para facilitar o acesso

    public static String getMySQLUrl() {
        return getProperty("db.mysql.url");
    }

    public static String getMySQLUser() {
        return getProperty("db.mysql.user");
    }

    public static String getMySQLPassword() {
        return getProperty("db.mysql.password");
    }

    public static String getMySQLDriver() {
        return getProperty("db.mysql.driver");
    }

    public static String getMongoHost() {
        return getProperty("db.mongo.host");
    }

    public static int getMongoPort() {
        return Integer.parseInt(getProperty("db.mongo.port", "27017"));
    }

    public static String getMongoDatabase() {
        return getProperty("db.mongo.database");
    }

    public static int getBCryptRounds() {
        return Integer.parseInt(getProperty("security.bcrypt.rounds", "12"));
    }

    public static int getSessionTimeout() {
        return Integer.parseInt(getProperty("app.session.timeout", "3600"));
    }
}