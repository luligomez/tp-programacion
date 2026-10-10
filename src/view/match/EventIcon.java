package view.match;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class EventIcon implements Icon {

    public enum Kind { YELLOW_CARD, RED_CARD, GOAL, OWN_GOAL, SUBSTITUTION, SUB_IN, SUB_OUT }

    private static final int SIZE = 16;
    private static final Color YELLOW = new Color(249, 190, 0);
    private static final Color RED = new Color(211, 47, 47);
    private static final Color GREEN = new Color(46, 125, 50);
    private static final Color DARK = new Color(40, 40, 40);

    private final Kind kind;

    public EventIcon(Kind kind) {
        this.kind = kind;
    }

    @Override public int getIconWidth() { return SIZE; }
    @Override public int getIconHeight() { return SIZE; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        switch (kind) {
            case YELLOW_CARD -> card(g2, x, y, YELLOW);
            case RED_CARD -> card(g2, x, y, RED);
            case GOAL -> ball(g2, x, y, DARK);
            case OWN_GOAL -> ball(g2, x, y, RED);
            case SUB_IN -> triangle(g2, x, y, GREEN, true);
            case SUB_OUT -> triangle(g2, x, y, RED, false);
            case SUBSTITUTION -> {
                g2.setColor(GREEN);
                g2.fillPolygon(new int[]{x + 4, x + 1, x + 7}, new int[]{y + 2, y + 8, y + 8}, 3);
                g2.setColor(RED);
                g2.fillPolygon(new int[]{x + 12, x + 9, x + 15}, new int[]{y + 14, y + 8, y + 8}, 3);
            }
        }
        g2.dispose();
    }

    private void card(Graphics2D g2, int x, int y, Color color) {
        RoundRectangle2D shape = new RoundRectangle2D.Float(x + 3, y + 1, 10, 14, 3, 3);
        g2.setColor(color);
        g2.fill(shape);
        g2.setColor(color.darker());
        g2.draw(shape);
    }

    private void ball(Graphics2D g2, int x, int y, Color color) {
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 1, y + 1, 14, 14);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x + 1, y + 1, 14, 14);
        g2.fillOval(x + 5, y + 5, 6, 6);
    }

    private void triangle(Graphics2D g2, int x, int y, Color color, boolean up) {
        g2.setColor(color);
        int[] xs = {x + 8, x + 2, x + 14};
        int[] ys = up ? new int[]{y + 3, y + 13, y + 13} : new int[]{y + 13, y + 3, y + 3};
        g2.fillPolygon(xs, ys, 3);
    }
}
