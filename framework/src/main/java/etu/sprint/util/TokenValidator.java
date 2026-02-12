package etu.sprint.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Utilitaire du framework pour valider un token API.
 * Vérifie dans la table "token" en base de données :
 * 1. Le token existe
 * 2. Sa date d'expiration (datetime_exp) n'est pas dépassée
 * 
 * Utilise le DataSource Spring via réflexion pour obtenir une connexion JDBC.
 * Aucune dépendance sur le projet utilisateur.
 */
public class TokenValidator {

    private static final String QUERY = "SELECT datetime_exp FROM token WHERE token = ?";

    /**
     * Valide un token en vérifiant dans la table "token" en base de données.
     * 
     * @param appContext le Spring ApplicationContext (obtenu via réflexion)
     * @param tokenValue la valeur du token envoyé dans le header Authorization
     * @return true si le token existe et n'est pas expiré, false sinon
     */
    public static boolean isTokenValid(Object appContext, String tokenValue) {
        if (appContext == null || tokenValue == null || tokenValue.isEmpty()) {
            return false;
        }

        try {
            // Récupérer le DataSource depuis le contexte Spring via réflexion
            Class<?> dataSourceClass = Class.forName("javax.sql.DataSource");
            java.lang.reflect.Method getBeanMethod = appContext.getClass().getMethod("getBean", Class.class);
            Object dataSource = getBeanMethod.invoke(appContext, dataSourceClass);

            // Obtenir une connexion JDBC
            java.lang.reflect.Method getConnectionMethod = dataSource.getClass().getMethod("getConnection");
            Connection conn = (Connection) getConnectionMethod.invoke(dataSource);

            try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {
                stmt.setString(1, tokenValue);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Timestamp expTimestamp = rs.getTimestamp("datetime_exp");
                    LocalDateTime expDateTime = expTimestamp.toLocalDateTime();
                    return expDateTime.isAfter(LocalDateTime.now());
                }
                return false;
            } finally {
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("[TokenValidator] Erreur lors de la validation du token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
