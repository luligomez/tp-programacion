package view.knockout;

import model.match.Match;
import model.match.knockout.FinalMatch;
import model.match.knockout.KnockoutTie;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class BracketPanel extends JPanel {

    private static final int CARD_W = 260;
    private static final int CARD_H = 96;
    private static final int COL_GAP = 70;
    private static final int SLOT_H = 110;
    private static final int TITLE_H = 45;
    private static final int MARGIN = 30;
    private static final int EXPAND_X = 60; // cuánto se ensancha al expandirse

    private static final Color BLUE = new Color(35, 95, 190);
    private static final Color LINE = new Color(170, 170, 170);
    private static final String[] TITLES = {"Quarter Finals", "Semi Finals", "Final"};

    private final MatchCardPanel[] quarterCards = new MatchCardPanel[4];
    private final MatchCardPanel[] semiCards = new MatchCardPanel[2];
    private final MatchCardPanel finalCard = new MatchCardPanel();

    public BracketPanel() {
        setLayout(null); // las posiciones las calcula doLayout()

        for (int i = 0; i < quarterCards.length; i++) {
            quarterCards[i] = new MatchCardPanel();
            add(quarterCards[i]);
        }
        for (int i = 0; i < semiCards.length; i++) {
            semiCards[i] = new MatchCardPanel();
            add(semiCards[i]);
        }
        add(finalCard);
        for (MatchCardPanel card : allCards()) {
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggle(card);
                }
            });
        }
    }

    public void update(List<KnockoutTie> quarters, List<KnockoutTie> semis, FinalMatch finalMatch) {
        for (int i = 0; i < quarterCards.length; i++) {
            quarterCards[i].showTie(i < quarters.size() ? quarters.get(i) : null);
        }
        for (int i = 0; i < semiCards.length; i++) {
            semiCards[i].showTie(i < semis.size() ? semis.get(i) : null);
        }
        finalCard.showFinal(finalMatch);
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(2 * MARGIN + 3 * CARD_W + 2 * COL_GAP, TITLE_H + 4 * SLOT_H + MARGIN);
    }

    // Altura del centro de cada tarjeta: las semis quedan entre sus dos cuartos, y la final entre las semis
    private int quarterCenter(int i) { return TITLE_H + i * SLOT_H + SLOT_H / 2; }
    private int semiCenter(int j) { return (quarterCenter(2 * j) + quarterCenter(2 * j + 1)) / 2; }
    private int finalCenter() { return (semiCenter(0) + semiCenter(1)) / 2; }

    // Centra la llave si la ventana es más ancha que lo necesario
    private int columnX(int col) {
        int offset = Math.max(0, (getWidth() - getPreferredSize().width) / 2);
        return offset + MARGIN + col * (CARD_W + COL_GAP);
    }

    @Override
    public void doLayout() {
        for (int i = 0; i < quarterCards.length; i++) place(quarterCards[i], 0, quarterCenter(i));
        for (int j = 0; j < semiCards.length; j++) place(semiCards[j], 1, semiCenter(j));
        place(finalCard, 2, finalCenter());
    }

    private void place(MatchCardPanel card, int col, int centerY) {
        int w = CARD_W;
        int h = CARD_H;
        int x = columnX(col);
        if (card.isExpanded()) {
            w = CARD_W + 2 * EXPAND_X;
            h = card.getPreferredSize().height;
            x -= EXPAND_X;
        }
        int y = centerY - h / 2;
        y = Math.max(TITLE_H, Math.min(y, getPreferredSize().height - h));
        x = Math.max(0, Math.min(x, getWidth() - w));
        card.setBounds(x, y, w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Titulos
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(BLUE);
        FontMetrics fm = g2.getFontMetrics();
        for (int col = 0; col < TITLES.length; col++) {
            int x = columnX(col) + (CARD_W - fm.stringWidth(TITLES[col])) / 2;
            g2.drawString(TITLES[col], x, 22);
        }

        // Llaves que unen las columnas
        g2.setColor(LINE);
        g2.setStroke(new BasicStroke(2f));
        connect(g2, 0, quarterCenter(0), quarterCenter(1), semiCenter(0));
        connect(g2, 0, quarterCenter(2), quarterCenter(3), semiCenter(1));
        connect(g2, 1, semiCenter(0), semiCenter(1), finalCenter());

        g2.dispose();
    }

    // Une dos tarjetas de una columna con la tarjeta de la columna siguiente
    private void connect(Graphics2D g2, int fromCol, int y1, int y2, int yTo) {
        int xRight = columnX(fromCol) + CARD_W;
        int xMid = xRight + COL_GAP / 2;
        int xNext = columnX(fromCol + 1);

        g2.drawLine(xRight, y1, xMid, y1);
        g2.drawLine(xRight, y2, xMid, y2);
        g2.drawLine(xMid, y1, xMid, y2);
        g2.drawLine(xMid, yTo, xNext, yTo);
    }

    private List<MatchCardPanel> allCards() {
        List<MatchCardPanel> all = new ArrayList<>(Arrays.asList(quarterCards));
        all.addAll(Arrays.asList(semiCards));
        all.add(finalCard);
        return all;
    }

    // Una sola tarjeta expandida a la vez, y la expandida se dibuja arriba de las demás
    private void toggle(MatchCardPanel clicked) {
        boolean wasExpanded = clicked.isExpanded();
        for (MatchCardPanel card : allCards()) card.collapse();
        if (!wasExpanded) {
            clicked.toggleExpanded();
            setComponentZOrder(clicked, 0);
        }
        revalidate();
        repaint();
    }

    public void setOnMatchSelectedListener(Consumer<Match> listener) {
        for (MatchCardPanel card : allCards()) {
            card.setOnMatchSelectedListener(listener);
        }
    }
}