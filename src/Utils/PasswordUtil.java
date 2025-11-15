package Utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitário para hash e verificação de senhas usando BCrypt
 * BCrypt é resistente a rainbow tables e brute force
 */
public class PasswordUtil {

    /**
     * Gera um hash BCrypt da senha
     * @param plainPassword Senha em texto plano
     * @return Hash BCrypt da senha
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }

        int rounds = ConfigUtil.getBCryptRounds();
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(rounds));
    }

    /**
     * Verifica se a senha em texto plano corresponde ao hash
     * @param plainPassword Senha em texto plano
     * @param hashedPassword Hash BCrypt armazenado
     * @return true se a senha corresponde, false caso contrário
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Hash inválido ou corrompido
            System.err.println("Erro ao verificar senha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se um hash precisa ser atualizado (se o número de rounds mudou)
     * @param hashedPassword Hash atual
     * @return true se precisa rehash
     */
    public static boolean needsRehash(String hashedPassword) {
        if (hashedPassword == null) {
            return true;
        }

        try {
            // Extrai o número de rounds do hash atual
            // Format: $2a$[rounds]$[salt+hash]
            String[] parts = hashedPassword.split("\\$");
            if (parts.length < 3) {
                return true;
            }

            int currentRounds = Integer.parseInt(parts[2]);
            int configRounds = ConfigUtil.getBCryptRounds();

            return currentRounds != configRounds;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Método auxiliar para migração de senhas antigas (texto plano)
     * Use este método APENAS durante a migração inicial
     */
    public static boolean isAlreadyHashed(String password) {
        // BCrypt hash sempre começa com $2a$, $2b$, ou $2y$
        return password != null && password.startsWith("$2");
    }
}