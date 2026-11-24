package com.finpro.frontend.story;

import java.util.List;

public record StoryScene(String id, String text, List<StoryChoice> choices) {
    public StoryScene {
        if (choices == null || choices.isEmpty()) {
            throw new IllegalArgumentException("Scene " + id + " butuh minimal 1 pilihan");
        }
        choices = List.copyOf(choices);
    }
}
