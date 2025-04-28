package utils;

/**
 * Classe singleton pour gérer le code de réinitialisation et l'email
 * de l'utilisateur à travers les différentes vues du processus de
 * réinitialisation de mot de passe.
 */
public class ResetCodeManager {

    private static ResetCodeManager instance;

    private String resetCode;
    private String userEmail;
    private long codeGenerationTime;
    private static final long CODE_EXPIRATION_TIME = 10 * 60 * 1000; // 10 minutes en millisecondes

    private ResetCodeManager() {
        // Constructeur privé pour le singleton
    }

    public static synchronized ResetCodeManager getInstance() {
        if (instance == null) {
            instance = new ResetCodeManager();
        }
        return instance;
    }

    public String getResetCode() {
        // Vérifier si le code n'a pas expiré
        if (System.currentTimeMillis() - codeGenerationTime > CODE_EXPIRATION_TIME) {
            // Le code a expiré
            resetCode = null;
            return null;
        }
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
        this.codeGenerationTime = System.currentTimeMillis();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isCodeValid() {
        return resetCode != null &&
                (System.currentTimeMillis() - codeGenerationTime <= CODE_EXPIRATION_TIME);
    }

    public void clearData() {
        resetCode = null;
        userEmail = null;
    }
}