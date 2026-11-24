package com.finpro.frontend.story;

import com.finpro.frontend.model.Stat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Kumpulan scene. defaultBook() cuma contoh/placeholder, isi aslinya diganti sama konten cerita tim. */
public class StoryBook {
    public static final String WIN_INTERVIEW = "WIN_INTERVIEW";
    public static final String LOSS_LOCKER_ROOM = "LOSS_LOCKER_ROOM";
    public static final String HALFTIME_TALK = "HALFTIME_TALK";

    private final Map<String, StoryScene> scenes = new LinkedHashMap<>();

    public StoryBook add(StoryScene scene) {
        scenes.put(scene.id(), scene);
        return this;
    }

    public StoryScene get(String id) { return scenes.get(id); }

    public boolean contains(String id) { return scenes.containsKey(id); }

    public static StoryBook defaultBook() {
        return new StoryBook()
            .add(new StoryScene(WIN_INTERVIEW, "Wartawan menghadangmu setelah laga. \"Apa rahasia kemenangan hari ini?\"", List.of(
                StoryChoice.of("Puji rekan setim", "Rekan setim makin solid denganmu.", null)
                        .withStat(Stat.PASSING, 1),
                StoryChoice.of("Bilang kamu latihan ekstra", "Wartawan tertarik. Pelatih mendengar itu...", "COACH_NOTICE")
                        .withStat(Stat.SHOOTING, 1).withEnergy(-5))))
            .add(new StoryScene("COACH_NOTICE", "Pelatih memanggilmu. \"Kudengar kamu rajin latihan ekstra.\"", List.of(
                StoryChoice.of("Tetap rendah hati", "Pelatih mengangguk puas.", null)
                        .withStat(Stat.STAMINA, 1),
                StoryChoice.of("Minta dimainkan penuh", "Pelatih setuju memberi menit bermain lebih.", null)
                        .withStat(Stat.SHOOTING, 2).requiring(Stat.SHOOTING, 55))))
            .add(new StoryScene(LOSS_LOCKER_ROOM, "Ruang ganti sunyi setelah kekalahan.", List.of(
                StoryChoice.of("Salahkan wasit", "Suasana makin keruh.", null)
                        .withStat(Stat.PASSING, -1),
                StoryChoice.of("Ajak evaluasi bareng", "Tim mulai bangkit.", null)
                        .withStat(Stat.PASSING, 1))))
            .add(new StoryScene(HALFTIME_TALK, "Tim tertinggal. Pelatih menatapmu di ruang ganti.", List.of(
                StoryChoice.of("Janji main lebih agresif", "Kamu terbakar semangat.", null)
                        .withStat(Stat.SHOOTING, 1).withEnergy(-5),
                StoryChoice.of("Minta fokus ke umpan", "Rencana jelas, tim tenang.", null)
                        .withStat(Stat.PASSING, 1))));
    }
}
