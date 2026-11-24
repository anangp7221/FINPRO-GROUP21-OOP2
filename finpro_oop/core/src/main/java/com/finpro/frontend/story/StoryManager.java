package com.finpro.frontend.story;

import com.finpro.frontend.command.CommandHistory;
import com.finpro.frontend.command.CommandResult;
import com.finpro.frontend.event.EventBus;
import com.finpro.frontend.event.GameEvent;
import com.finpro.frontend.event.GameEventListener;
import com.finpro.frontend.event.GameEventType;
import com.finpro.frontend.model.Career;
import java.util.List;

/**
 * Observer: mendengarkan event match lalu memicu scene cerita.
 * Pakai: bus.subscribe(new StoryManager(career, bus, StoryBook.defaultBook()));
 */
public class StoryManager implements GameEventListener {
    private final Career career;
    private final EventBus bus;
    private final StoryBook book;
    private final CommandHistory history = new CommandHistory();

    private StoryScene current;

    public StoryManager(Career career, EventBus bus, StoryBook book) {
        this.career = career;
        this.bus = bus;
        this.book = book;
    }

    @Override
    public void onEvent(GameEvent e) {
        if (current != null) return; // lagi ada scene aktif
        switch (e.type()) {
            case FULL_TIME -> {
                if (e.value() > 0) start(StoryBook.WIN_INTERVIEW);
                else if (e.value() < 0) start(StoryBook.LOSS_LOCKER_ROOM);
            }
            case HALF_TIME -> {
                if (e.value() < 0) start(StoryBook.HALFTIME_TALK);
            }
            default -> { }
        }
    }

    public boolean start(String sceneId) {
        StoryScene scene = book.get(sceneId);
        if (scene == null) return false;
        current = scene;
        bus.publish(GameEventType.STORY_SCENE, scene.text(), 0);
        return true;
    }

    public boolean hasActiveScene() { return current != null; }

    public StoryScene getCurrentScene() { return current; }

    public boolean canChoose(int index) {
        if (current == null || index < 0 || index >= current.choices().size()) return false;
        return new StoryChoiceCommand(career, current.choices().get(index)).canExecute();
    }

    public CommandResult choose(int index) {
        if (current == null) {
            return new CommandResult(false, "Tidak ada cerita aktif.");
        }
        List<StoryChoice> choices = current.choices();
        if (index < 0 || index >= choices.size()) {
            return new CommandResult(false, "Pilihan tidak valid.");
        }
        StoryChoice picked = choices.get(index);
        CommandResult result = history.execute(new StoryChoiceCommand(career, picked));
        if (!result.success()) {
            return result;
        }
        if (picked.stat() != null && picked.statDelta() != 0) {
            bus.publish(GameEventType.STAT_CHANGED,
                    picked.stat() + (picked.statDelta() > 0 ? " +" : " ") + picked.statDelta(), picked.statDelta());
        }
        current = null;
        if (picked.nextSceneId() != null) {
            start(picked.nextSceneId());
        }
        return result;
    }

    public List<String> getChoiceLog() { return history.getLog(); }
}
