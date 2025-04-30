package esprit.tn.demo.controllers.GestionVente;

import esprit.tn.demo.entities.GestionVente.Commande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


public class CommandeController {

    @FXML
    private TableView<Commande> commandeTableView;
    @FXML
    private TableColumn<Commande, Number> idColumn;
    @FXML
    private TableColumn<Commande, String> dateColumn;
    @FXML
    private TableColumn<Commande, Number> montantColumn;
    @FXML
    private TableColumn<Commande, String> statutColumn;

    @FXML
    private BarChart<String, Number> salesChart;

    @FXML
    private ComboBox<String> periodeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    private ObservableList<Commande> commandes = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        idColumn.setCellValueFactory(data -> data.getValue().idProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateCommandeProperty());
        montantColumn.setCellValueFactory(data -> data.getValue().montantTotalProperty());
        statutColumn.setCellValueFactory(data -> data.getValue().statutProperty());

        // Exemple de données initiales
        commandes.addAll(
                new Commande(1, "Payée", 120.5f, "2024-04-20"),
                new Commande(2, "En cours", 320.0f, "2024-04-21"),
                new Commande(3, "Annulée", 200.5f, "2024-04-25")
        );

        commandeTableView.setItems(commandes);

        // Initialiser les choix de période
        periodeComboBox.setItems(FXCollections.observableArrayList("Aujourd'hui", "Cette semaine", "Ce mois"));

        // Charger directement les données dans le graphique
        updateSalesChart(commandes);
    }

    @FXML
    private void handleFiltrer() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<Commande> filtered = commandes.stream()
                .filter(c -> {
                    LocalDate dateCommande = LocalDate.parse(c.getDateCommande(), formatter);
                    return (startDate == null || !dateCommande.isBefore(startDate)) &&
                            (endDate == null || !dateCommande.isAfter(endDate));
                })
                .collect(Collectors.toList());

        commandeTableView.setItems(FXCollections.observableArrayList(filtered));
        updateSalesChart(FXCollections.observableArrayList(filtered));
    }

    private void updateSalesChart(List<Commande> commandesList) {
        salesChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Commande commande : commandesList) {
            series.getData().add(new XYChart.Data<>(commande.getDateCommande(), commande.getMontantTotal()));
        }
        salesChart.getData().add(series);
    }

    @FXML
    private void handleExportPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer PDF");
            fileChooser.setInitialFileName("commandes.pdf");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = (Stage) commandeTableView.getScene().getWindow();
            java.io.File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Liste des Commandes"));
                document.add(new Paragraph(" ")); // Ligne vide

                PdfPTable table = new PdfPTable(4);
                table.addCell("ID");
                table.addCell("Date Commande");
                table.addCell("Montant Total");
                table.addCell("Statut");

                for (Commande commande : commandeTableView.getItems()) {
                    table.addCell(String.valueOf(commande.getId()));
                    table.addCell(commande.getDateCommande());
                    table.addCell(String.valueOf(commande.getMontantTotal()));
                    table.addCell(commande.getStatut());
                }

                document.add(table);
                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
