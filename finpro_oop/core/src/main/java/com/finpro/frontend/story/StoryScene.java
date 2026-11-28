package com.finpro.frontend.story;

import java.util.List;

/** Teks boleh memakai {nama} untuk nama pemain. */
public record StoryScene(String id, Speaker speaker, String text, List<StoryChoice> choices) {
    public StoryScene {
        if (choices == null || choices.isEmpty()) {
            throw new IllegalArgumentException("Scene " + id + " butuh minimal 1 pilihan");
        }
        choices = List.copyOf(choices);
    }

    public StoryScene(String id, String text, List<StoryChoice> choices) {
        this(id, Speaker.NARRATOR, text, choices);
    }
}
