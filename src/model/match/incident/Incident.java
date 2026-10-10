package model.match.incident;

import java.io.Serial;
import java.io.Serializable;

import model.match.Match;
import model.person.player.Player;
import model.reports.MatchReport.PlayerMark;
import model.reports.MatchReport.TimelineEvent;
import java.util.Optional;

public abstract class Incident implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int minute;

    public Incident(int minute) {
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String toString(){
        return "Min " + minute + "'";
    }

    // Cómo aparece esta incidencia en la línea de tiempo (vacío si no se muestra)
    public abstract Optional<TimelineEvent> toTimelineEvent(Match match);

    // Qué símbolo le corresponde a este jugador por esta incidencia (vacío si no está involucrado)
    public Optional<PlayerMark> markFor(Player player) {
        return Optional.empty();
    }
}
