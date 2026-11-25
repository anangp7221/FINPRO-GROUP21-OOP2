package com.finpro.frontend.engine;

import com.finpro.frontend.command.CommandResult;
import com.finpro.frontend.event.EventBus;
import com.finpro.frontend.event.EventLogListener;
import com.finpro.frontend.event.StatisticsListener;
import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.Player;
import com.finpro.frontend.save.CorruptSaveException;
import com.finpro.frontend.save.SaveException;
import com.finpro.frontend.save.SaveManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class GameManager {
    private static GameManager instance;

    private final EventBus eventBus = new EventBus();
    private final EventLogListener eventLog = new EventLogListener();
    private final SaveManager saveManager = new SaveManager();
    private Path savePath = SaveManager.defaultPath();

    private Career career;

    private GameManager() {
        eventBus.subscribe(new StatisticsListener(() -> career == null ? null : career.getRecord()));
        eventBus.subscribe(eventLog);
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public Career newCareer(String playerName) {
        career = new Career(new Player(playerName));
        eventLog.clear();
        return career;
    }

    public boolean hasCareer() { return career != null; }

    public Career getCareer() {
        if (career == null) {
            throw new IllegalStateException("Belum ada karier. Panggil newCareer() dulu.");
        }
        return career;
    }

    public void reset() { career = null; }

    // ---------- event ----------

    public EventBus getEventBus() { return eventBus; }
    public EventLogListener getEventLog() { return eventLog; }

    // ---------- save / load ----------

    public void setSavePath(Path path) { this.savePath = path; }
    public Path getSavePath() { return savePath; }
    public boolean hasSave() { return Files.isRegularFile(savePath); }

    public CommandResult saveCareer() {
        if (career == null) {
            return new CommandResult(false, "Belum ada karier untuk disimpan.");
        }
        try {
            saveManager.save(career, savePath);
            return new CommandResult(true, "Karier disimpan.");
        } catch (SaveException e) {
            return new CommandResult(false, e.getMessage());
        }
    }

    /** Tidak pernah melempar exception: kalau gagal, karier yang sedang berjalan tidak berubah. */
    public CommandResult loadCareer() {
        try {
            career = saveManager.load(savePath);
            eventLog.clear();
            return new CommandResult(true, "Karier dimuat (pekan " + career.getWeek() + ").");
        } catch (CorruptSaveException e) {
            quarantine();
            return new CommandResult(false, "Save rusak (" + e.getMessage() + "). File dipindah ke .corrupt, mulai karier baru.");
        } catch (SaveException e) {
            return new CommandResult(false, e.getMessage());
        }
    }

    private void quarantine() {
        try {
            Files.move(savePath, savePath.resolveSibling(savePath.getFileName() + ".corrupt"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            // gagal memindahkan bukan alasan untuk crash
        }
    }
}
