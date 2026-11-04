package service;

import model.person.Coach;
import model.person.Person;
import model.person.Referee;
import model.person.player.Player;
import model.place.Country;
import model.team.Team;
import model.tournament.Tournament;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.Barcode128;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import view.theme.Theme;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Generates a PDF with the identification card of every person taking part in the tournament.
 * Order: one section per team (alphabetical), each with its coach first and then its players
 * grouped by position; the referees go in the last section.
 */
public class CredentialPdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float CARD_HEIGHT = 140f;

    // Everything a card needs, already turned into text
    private record Credential(String name, String role, Color color, List<String> details, String barcodeValue) {
    }

    public void generate(Tournament tournament, Path destination) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        try (OutputStream out = Files.newOutputStream(destination)) {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            PdfContentByte canvas = writer.getDirectContent();
            Image photo = createGenericPhoto();
            photo.scaleToFit(56f, 64f);

            List<Team> teams = new ArrayList<>(tournament.getTeams());
            teams.sort(Comparator.comparing(Team::getName));

            boolean firstSection = true;
            for (Team team : teams) {
                if (!firstSection) {
                    document.newPage();
                }
                firstSection = false;
                addSection(document, canvas, photo,
                        team.getName() + " (" + countryName(team.getCountry()) + ")",
                        teamCredentials(team));
            }

            List<Referee> referees = new ArrayList<>(tournament.getReferees());
            referees.sort(Comparator.comparing(Referee::getName));
            if (!referees.isEmpty()) {
                if (!firstSection) {
                    document.newPage();
                }
                List<Credential> cards = new ArrayList<>();
                for (Referee referee : referees) {
                    cards.add(refereeCredential(referee));
                }
                addSection(document, canvas, photo, "Referees", cards);
            }

            document.close();
        }
    }

    // ---------- Data -> cards ----------

    private List<Credential> teamCredentials(Team team) {
        List<Credential> cards = new ArrayList<>();

        Coach coach = team.getCoach();
        if (coach != null) {
            cards.add(coachCredential(coach, team));
        }

        List<Player> players = new ArrayList<>(team.getPlayers());
        players.sort(Comparator.comparing(Player::getPosition).thenComparing(Player::getName));
        for (Player player : players) {
            cards.add(playerCredential(player, team));
        }
        return cards;
    }

    private Credential playerCredential(Player player, Team team) {
        List<String> details = new ArrayList<>();
        details.add("Team: " + team.getName());
        details.add("Document: " + document(player));
        details.add("Born: " + birthDate(player));
        details.add("Rating: " + player.getRating());
        return new Credential(player.getName(), "PLAYER · " + player.getPosition(),
                Theme.PRIMARY, details, player.getDocumentNumber());
    }

    private Credential coachCredential(Coach coach, Team team) {
        List<String> details = new ArrayList<>();
        details.add("Team: " + team.getName());
        details.add("Document: " + document(coach));
        details.add("Born: " + birthDate(coach));
        details.add("Nationality: " + countryName(coach.getNationality()));
        details.add("Titles: " + coach.getTitlesWon());
        return new Credential(coach.getName(), "COACH", Theme.COACH_COLOR, details, coach.getDocumentNumber());
    }

    private Credential refereeCredential(Referee referee) {
        List<String> details = new ArrayList<>();
        details.add("Document: " + document(referee));
        details.add("Born: " + birthDate(referee));
        details.add("Nationality: " + countryName(referee.getNationality()));
        details.add("Years as referee: " + referee.getYearsOfExperience());
        return new Credential(referee.getName(), "REFEREE", Theme.REFEREE_COLOR, details, referee.getDocumentNumber());
    }

    private String document(Person person) {
        return person.getDocumentType() + " " + person.getDocumentNumber();
    }

    private String birthDate(Person person) {
        return person.getBirthDate() == null ? "-" : person.getBirthDate().format(DATE_FORMAT);
    }

    private String countryName(Country country) {
        return country == null ? "-" : country.getName();
    }

    // ---------- PDF layout ----------

    private void addSection(Document document, PdfContentByte canvas, Image photo,
                            String title, List<Credential> cards) throws DocumentException {
        Paragraph heading = new Paragraph(title,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.NORMAL, Color.BLACK));
        heading.setSpacingAfter(10f);
        document.add(heading);

        PdfPTable grid = new PdfPTable(2);
        grid.setWidthPercentage(100);
        grid.setSplitRows(false);

        for (Credential card : cards) {
            grid.addCell(buildCard(card, canvas, photo));
        }
        if (cards.size() % 2 != 0) {
            PdfPCell empty = new PdfPCell();
            empty.setBorder(Rectangle.NO_BORDER);
            grid.addCell(empty);
        }
        document.add(grid);
    }

    private PdfPCell buildCard(Credential card, PdfContentByte canvas, Image photo) throws DocumentException {
        PdfPTable cardTable = new PdfPTable(2);
        cardTable.setWidthPercentage(100);
        cardTable.setWidths(new float[]{30f, 70f});

        // Role band
        PdfPCell band = new PdfPCell(new Phrase(card.role(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.NORMAL, Color.WHITE)));
        band.setColspan(2);
        band.setBackgroundColor(card.color());
        band.setBorderColor(card.color());
        band.setPadding(3f);
        cardTable.addCell(band);

        // Photo
        PdfPCell photoCell = new PdfPCell(photo, false);
        photoCell.setBorder(Rectangle.LEFT);
        photoCell.setBorderColor(Theme.CARD_BORDER);
        photoCell.setPadding(5f);
        photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cardTable.addCell(photoCell);

        // Name and details
        Phrase info = new Phrase();
        info.add(new Phrase(card.name() + "\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.NORMAL, Color.BLACK)));
        Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Theme.TEXT_PDF);
        for (String detail : card.details()) {
            info.add(new Phrase(detail + "\n", detailFont));
        }
        PdfPCell infoCell = new PdfPCell(info);
        infoCell.setLeading(0f, 1.4f);
        infoCell.setBorder(Rectangle.RIGHT);
        infoCell.setBorderColor(Theme.CARD_BORDER);
        infoCell.setPadding(5f);
        cardTable.addCell(infoCell);

        // Barcode (document number)
        PdfPCell barcodeCell = barcodeCell(card.barcodeValue(), canvas);
        cardTable.addCell(barcodeCell);

        PdfPCell outer = new PdfPCell(cardTable);
        outer.setBorder(Rectangle.NO_BORDER);
        outer.setPadding(4f);
        outer.setFixedHeight(CARD_HEIGHT);
        return outer;
    }

    private PdfPCell barcodeCell(String value, PdfContentByte canvas) {
        PdfPCell cell;
        if (value == null || value.isBlank()) {
            cell = new PdfPCell(new Phrase(" "));
        } else {
            Barcode128 barcode = new Barcode128();
            barcode.setCode(value.trim());
            barcode.setBarHeight(18f);
            barcode.setSize(7f);
            cell = new PdfPCell(barcode.createImageWithBarcode(canvas, Color.BLACK, Color.BLACK), false);
        }
        cell.setColspan(2);
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        cell.setBorderColor(Theme.CARD_BORDER);
        cell.setPadding(4f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    // Placeholder picture (grey silhouette), drawn in code so no image file is needed
    private Image createGenericPhoto() throws IOException, DocumentException {
        BufferedImage picture = new BufferedImage(112, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = picture.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Theme.BACKGROUND);
        g.fillRect(0, 0, 112, 128);
        g.setColor(Theme.GRAY);
        g.fillOval(36, 22, 40, 40);   // head
        g.fillOval(14, 70, 84, 90);   // shoulders
        g.dispose();
        return Image.getInstance(picture, (Color) null);
    }
}