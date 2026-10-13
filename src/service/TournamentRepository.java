package service;

import model.tournament.Tournament;

import java.io.*;
import java.nio.file.*;

public class TournamentRepository {
    private static final Path FILE = Path.of("tournament.ser");
    private static final Path TMP = Path.of("tournament.ser.tmp");

    public boolean exists() {
        return Files.exists(FILE);
    }

    // Se escribe a un temporal y después se reemplaza: si la app se cierra a mitad de escritura,
    // el guardado anterior queda intacto
    public void save(Tournament tournament) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(TMP)))) {
            out.writeObject(tournament);
        }
        Files.move(TMP, FILE, StandardCopyOption.REPLACE_EXISTING);
    }

    public Tournament load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(FILE)))) {
            return (Tournament) in.readObject();
        }
    }

    public void delete() throws IOException {
        Files.deleteIfExists(FILE);
    }
}