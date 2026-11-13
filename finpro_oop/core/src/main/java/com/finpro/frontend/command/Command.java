package com.finpro.frontend.command;

public interface Command {
    String getName();

    boolean canExecute();

    String execute();

    void undo();
}
