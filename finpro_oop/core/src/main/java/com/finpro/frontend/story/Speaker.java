package com.finpro.frontend.story;

import com.finpro.frontend.content.Roster;

/** Siapa yang bicara di sebuah scene. imageName = nama file potret di assets/images/ (tanpa .png). */
public enum Speaker {
    NARRATOR("", null),
    COACH(Roster.COACH, "coach"),
    JOURNALIST(Roster.JOURNALIST, "journalist"),
    TEAMMATE(Roster.CAPTAIN, "teammate"),
    PLAYER("", "player");

    private final String displayName;
    private final String imageName;

    Speaker(String displayName, String imageName) {
        this.displayName = displayName;
        this.imageName = imageName;
    }

    public String getDisplayName() { return displayName; }
    public String getImageName() { return imageName; }
}
