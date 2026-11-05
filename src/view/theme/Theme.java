package view.theme;

import java.awt.Color;
import java.awt.Font;

public final class Theme {

    private Theme() { }   // utility class: not instantiated

    // ---------- Colores de marca ----------
    public static final Color PRIMARY = new Color(35, 95, 190);
    public static final Color PRIMARY_LIGHT = new Color(235, 240, 250);   // pastillas y tags
    public static final Color GRAY = new Color(130, 130, 130);

    // ---------- Fondos ----------
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color SURFACE = Color.WHITE;                   // cards, tablas
    public static final Color SURFACE_ALT = new Color(246, 248, 252);   // filas alternadas
    public static final Color SELECTION = new Color(222, 234, 252);
    public static final Color DARK = new Color(40, 40, 40);
    public static final Color LINE = new Color(170, 170, 170);


    // ---------- Texto ----------
    public static final Color TEXT = new Color(30, 40, 50);
    public static final Color TEXT_PDF = new Color(60, 60, 60);
    public static final Color TEXT_MUTED = GRAY;

    // ---------- Bordes y separadores ----------
    public static final Color BORDER = new Color(220, 224, 230);
    public static final Color SEPARATOR = new Color(232, 235, 241);

    // ---------- Estados ----------
    public static final Color SUCCESS = new Color(46, 125, 50);
    public static final Color DANGER = new Color(198, 40, 40);
    public static final Color WARNING = new Color(249, 168, 37);
    public static final Color CARD_YELLOW = new Color(249, 190, 0);

    // ---------- Posiciones de jugadores ----------
    public static final Color POS_GOALKEEPER = WARNING;
    public static final Color POS_DEFENDER = PRIMARY;
    public static final Color POS_MIDFIELDER = SUCCESS;
    public static final Color POS_FORWARD = DANGER;

    public static final Color COACH_COLOR = new Color(30, 130, 80);
    public static final Color REFEREE_COLOR = new Color(190, 90, 30);
    public static final Color CARD_BORDER = new Color(190, 190, 190);

    // ---------- Navigation bars ----------
    public static final Color TOPBAR_BG = new Color(30, 30, 30);
    public static final Color SIDEBAR_BG = new Color(45, 45, 45);
    public static final Color SIDEBAR_ACTIVE = new Color(70, 70, 70);

    // ---------- Fuentes ----------
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font BIG_BOLD16 = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BIG_BOLD15 = new Font("SansSerif", Font.BOLD, 15);
    public static final Font BIG_BOLD18 = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SUBTITLE = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font LABEL_BOLD = new Font("SansSerif", Font.BOLD, 12);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font SMALL12 = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font SMALL_BOLD13 = new Font("SansSerif", Font.BOLD, 13);
    public static final Font SMALL_BOLD = new Font("SansSerif", Font.BOLD, 11);


}