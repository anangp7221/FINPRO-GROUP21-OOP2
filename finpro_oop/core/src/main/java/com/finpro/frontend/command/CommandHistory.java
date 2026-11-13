package com.finpro.frontend.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class CommandHistory {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final List<String> log = new ArrayList<>();

    public CommandResult execute(Command command) {
        if (!command.canExecute()) {
            return new CommandResult(false, command.getName() + " tidak bisa dilakukan sekarang.");
        }
        String message = command.execute();
        undoStack.push(command);
        log.add(command.getName() + ": " + message);
        return new CommandResult(true, message);
    }

    public CommandResult undo() {
        if (undoStack.isEmpty()) {
            return new CommandResult(false, "Tidak ada aksi yang bisa dibatalkan.");
        }
        Command command = undoStack.pop();
        command.undo();
        log.add("UNDO " + command.getName());
        return new CommandResult(true, command.getName() + " dibatalkan.");
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }

    public int size() { return undoStack.size(); }

    public List<String> getLog() { return Collections.unmodifiableList(log); }

    public void clear() {
        undoStack.clear();
        log.clear();
    }
}
