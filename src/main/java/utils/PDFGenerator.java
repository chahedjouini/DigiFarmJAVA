package utils;

import entities.Etude;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

    public static void generateEtudePDF(Etude etude, String outputPath) throws DocumentException {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            Paragraph title = new Paragraph("Rapport d'Étude Agricole", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add space

            addSection(document, "Informations Générales");
            addField(document, "Date de l'Étude", DATE_FORMATTER.format(etude.getDateR()));
            addField(document, "Expert", etude.getExpert().getNom() + " " + etude.getExpert().getPrenom());
            addField(document, "Culture", etude.getCulture().getNom());

            addSection(document, "Détails de la Culture");
            addField(document, "Type de Culture", etude.getCulture().getTypeCulture());
            addField(document, "Surface", etude.getCulture().getSurface() + " ha");
            addField(document, "Date de Plantation", etude.getCulture().getDatePlantation() != null ?
                    DATE_FORMATTER.format(etude.getCulture().getDatePlantation()) : "Non défini");
            addField(document, "Date de Récolte", etude.getCulture().getDateRecolte() != null ?
                    DATE_FORMATTER.format(etude.getCulture().getDateRecolte()) : "Non défini");
            addField(document, "Région", etude.getCulture().getRegion());

            addSection(document, "Conditions Environnementales");
            addField(document, "Climat", etude.getClimat().toString());
            addField(document, "Type de Sol", etude.getTypeSol().toString());
            addField(document, "Précipitations", etude.getPrecipitations() + " mm");
            addField(document, "Irrigation", etude.isIrrigation() ? "Oui" : "Non");
            addField(document, "Fertilisation", etude.isFertilisation() ? "Oui" : "Non");

            addSection(document, "Données Économiques");
            addField(document, "Prix", etude.getPrix() + " TND");
            addField(document, "Rendement", etude.getRendement() + " kg/ha");
            addField(document, "Main-d'œuvre", etude.getMainOeuvre() + " H");

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Document généré le " + DATE_FORMATTER.format(java.time.LocalDate.now()), NORMAL_FONT);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            throw new DocumentException("Error generating PDF: " + e.getMessage());
        }
    }

    private static void addSection(Document document, String title) throws DocumentException {
        if (title == null) title = "";
        Paragraph section = new Paragraph(title, HEADER_FONT);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setFixedHeight(2f);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setBorder(Rectangle.NO_BORDER);
        line.addCell(cell);
        document.add(line);
    }

    private static void addField(Document document, String label, String value) throws DocumentException {
        if (label == null) label = "";
        if (value == null) value = "Non défini";
        Paragraph field = new Paragraph();
        field.add(new Chunk(label + ": ", HEADER_FONT));
        field.add(new Chunk(value, NORMAL_FONT));
        field.setIndentationLeft(20);
        field.setSpacingAfter(5);
        document.add(field);
    }
}
