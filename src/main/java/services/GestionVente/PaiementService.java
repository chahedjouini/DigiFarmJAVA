package services.GestionVente;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


import java.util.List;

public class PaiementService {

    private final String STRIPE_SECRET_KEY = "sk_test_51QzFecJkRKrjEKFTVbmlm6UpLvMOH52BzxjvUQX329L0OLN16KtTlgg76GL4CUf3og29HqXDYok95BYdI9L6uWoT00dFW8MAvx";

    public PaiementService() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    public String creerSessionPaiement(List<LigneCommande> articles, double fraisLivraisonUSD) throws StripeException {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:9090/success")
                .setCancelUrl("http://localhost:9090/cancel");


        for (LigneCommande article : articles) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) article.getQuantite())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setUnitAmount((long) (article.getPrixUnitaire() * 100))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(article.getNomProduit())
                                                            .build()
                                            )
                                            .build()
                            ).build()
            );
        }

        // ✅ Ajout des frais de livraison
        paramsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount((long) (fraisLivraisonUSD * 100))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Frais de livraison")
                                                        .build()
                                        )
                                        .build()
                        ).build()
        );

        Session session = Session.create(paramsBuilder.build());
        return session.getUrl(); // 🔁 Redirection vers l'URL Stripe
    }

    // ✅ Classe interne pour représenter chaque article à payer
    public static class LigneCommande {
        private final String nomProduit;
        private final double prixUnitaire;
        private final int quantite;

        public LigneCommande(String nomProduit, double prixUnitaire, int quantite) {
            this.nomProduit = nomProduit;
            this.prixUnitaire = prixUnitaire;
            this.quantite = quantite;
        }

        public String getNomProduit() { return nomProduit; }
        public double getPrixUnitaire() { return prixUnitaire; }
        public int getQuantite() { return quantite; }
    }
}
