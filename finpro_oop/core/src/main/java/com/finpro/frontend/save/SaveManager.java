package com.finpro.frontend.save;

import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.CareerRecord;
import com.finpro.frontend.model.Player;
import com.finpro.frontend.model.Stat;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * Simpan/muat Career ke file teks "key=value" + baris checksum CRC32.
 * Save ditulis ke file .tmp dulu lalu dipindah, jadi save lama tidak rusak kalau proses terputus.
 */
public class SaveManager {
    public static final int VERSION = 1;
    private static final String CHECKSUM_KEY = "checksum=";

    public static Path defaultPath() {
        return Path.of(System.getProperty("user.home"), ".finpro_oop", "career.sav");
    }

    public void save(Career career, Path path) throws SaveException {
        Map<String, String> data = new LinkedHashMap<>();
        Player p = career.getPlayer();
        CareerRecord r = career.getRecord();

        data.put("version", String.valueOf(VERSION));
        data.put("name", sanitizeName(p.getName()));
        data.put("week", String.valueOf(career.getWeek()));
        data.put("playTime", Double.toString(career.getPlayTimeSeconds()));
        data.put("energy", String.valueOf(p.getEnergy()));
        for (Stat s : Stat.values()) {
            data.put("stat." + s.name(), String.valueOf(p.getStat(s)));
        }
        data.put("record.matches", String.valueOf(r.getMatches()));
        data.put("record.wins", String.valueOf(r.getWins()));
        data.put("record.draws", String.valueOf(r.getDraws()));
        data.put("record.losses", String.valueOf(r.getLosses()));
        data.put("record.goals", String.valueOf(r.getGoals()));
        data.put("record.assists", String.valueOf(r.getAssists()));

        StringBuilder body = new StringBuilder();
        data.forEach((k, v) -> body.append(k).append('=').append(v).append('\n'));
        String content = body + CHECKSUM_KEY + checksum(body.toString()) + "\n";

        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
            Files.writeString(tmp, content, StandardCharsets.UTF_8);
            try {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new SaveException("Gagal menyimpan ke " + path + ": " + e.getMessage(), e);
        }
    }

    public Career load(Path path) throws SaveException {
        String text;
        try {
            text = Files.readString(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new SaveException("File save tidak ditemukan.", e);
        } catch (CharacterCodingException e) {
            throw new CorruptSaveException("Isi file bukan teks UTF-8 yang valid.", e);
        } catch (IOException e) {
            throw new SaveException("Gagal membaca save: " + e.getMessage(), e);
        }

        Map<String, String> data = parseAndVerify(text);

        int version = readInt(data, "version", 1, Integer.MAX_VALUE);
        if (version != VERSION) {
            throw new CorruptSaveException("Versi save " + version + " tidak didukung.");
        }

        String name = require(data, "name").trim();
        if (name.isEmpty()) {
            throw new CorruptSaveException("Nama pemain kosong.");
        }

        Player player = new Player(name);
        for (Stat s : Stat.values()) {
            int value = readInt(data, "stat." + s.name(), Player.MIN_VALUE, Player.MAX_STAT);
            player.changeStat(s, value - player.getStat(s));
        }
        int energy = readInt(data, "energy", Player.MIN_VALUE, Player.MAX_ENERGY);
        player.changeEnergy(energy - player.getEnergy());

        Career career = new Career(player);
        career.restoreProgress(readInt(data, "week", 1, 100_000), readPlayTime(data));
        career.getRecord().restore(
                readInt(data, "record.matches", 0, 1_000_000),
                readInt(data, "record.wins", 0, 1_000_000),
                readInt(data, "record.draws", 0, 1_000_000),
                readInt(data, "record.losses", 0, 1_000_000),
                readInt(data, "record.goals", 0, 1_000_000),
                readInt(data, "record.assists", 0, 1_000_000));
        return career;
    }

    // ---------- helper ----------

    private Map<String, String> parseAndVerify(String text) throws CorruptSaveException {
        int idx = text.lastIndexOf(CHECKSUM_KEY);
        if (idx < 0 || (idx > 0 && text.charAt(idx - 1) != '\n')) {
            throw new CorruptSaveException("Checksum tidak ditemukan.");
        }
        String body = text.substring(0, idx);
        String stored = text.substring(idx + CHECKSUM_KEY.length()).trim();
        if (!stored.equals(checksum(body))) {
            throw new CorruptSaveException("Checksum tidak cocok, file diubah atau rusak.");
        }

        Map<String, String> data = new LinkedHashMap<>();
        for (String line : body.split("\n")) {
            if (line.isEmpty()) continue;
            int eq = line.indexOf('=');
            if (eq <= 0) {
                throw new CorruptSaveException("Baris tidak valid: " + line);
            }
            data.put(line.substring(0, eq), line.substring(eq + 1));
        }
        return data;
    }

    private static String checksum(String body) {
        CRC32 crc = new CRC32();
        crc.update(body.getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(crc.getValue());
    }

    private static String require(Map<String, String> data, String key) throws CorruptSaveException {
        String v = data.get(key);
        if (v == null) throw new CorruptSaveException("Data '" + key + "' hilang.");
        return v;
    }

    private static int readInt(Map<String, String> data, String key, int min, int max) throws CorruptSaveException {
        String raw = require(data, key);
        try {
            int v = Integer.parseInt(raw.trim());
            if (v < min || v > max) {
                throw new CorruptSaveException("Nilai '" + key + "' di luar rentang: " + v);
            }
            return v;
        } catch (NumberFormatException e) {
            throw new CorruptSaveException("Nilai '" + key + "' bukan angka: " + raw, e);
        }
    }

    private static double readPlayTime(Map<String, String> data) throws CorruptSaveException {
        String raw = require(data, "playTime");
        try {
            double v = Double.parseDouble(raw.trim());
            if (Double.isNaN(v) || Double.isInfinite(v) || v < 0) {
                throw new CorruptSaveException("Nilai 'playTime' tidak valid: " + raw);
            }
            return v;
        } catch (NumberFormatException e) {
            throw new CorruptSaveException("Nilai 'playTime' bukan angka: " + raw, e);
        }
    }

    private static String sanitizeName(String name) {
        String clean = name == null ? "" : name.replace('\r', ' ').replace('\n', ' ').trim();
        return clean.isEmpty() ? "Pemain" : clean;
    }
}
