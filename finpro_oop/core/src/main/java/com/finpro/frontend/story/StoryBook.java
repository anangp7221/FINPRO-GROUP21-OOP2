package com.finpro.frontend.story;

import com.finpro.frontend.model.Stat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Kumpulan scene cerita. Cara menambah scene: add(new StoryScene(id, speaker, teks, pilihan)).
 * Format pilihan: StoryChoice.of(label, teksHasil, idSceneBerikutnya /* null = selesai *\/)
 *                  .withStat(Stat.X, +n).withEnergy(+/-n).requiring(Stat.X, minimal)
 * Pemicu scene ada di StoryManager.onEvent().
 */
public class StoryBook {
    public static final String INTRO_QF = "INTRO_QF";
    public static final String INTRO_SF = "INTRO_SF";
    public static final String INTRO_FINAL = "INTRO_FINAL";
    public static final String HALFTIME_TALK = "HALFTIME_TALK";
    public static final String WIN_INTERVIEW = "WIN_INTERVIEW";
    public static final String COACH_NOTICE = "COACH_NOTICE";
    public static final String LOSS_LOCKER_ROOM = "LOSS_LOCKER_ROOM";
    public static final String QF_ADVANCE = "QF_ADVANCE";
    public static final String SF_ADVANCE = "SF_ADVANCE";
    public static final String TROPHY = "TROPHY";
    public static final String TROPHY_COACH = "TROPHY_COACH";
    public static final String FINAL_LOST = "FINAL_LOST";
    public static final String ELIMINATED = "ELIMINATED";

    private final Map<String, StoryScene> scenes = new LinkedHashMap<>();

    public StoryBook add(StoryScene scene) {
        scenes.put(scene.id(), scene);
        return this;
    }

    public StoryScene get(String id) { return scenes.get(id); }

    public boolean contains(String id) { return scenes.containsKey(id); }

    public int size() { return scenes.size(); }

    public Iterable<StoryScene> all() { return scenes.values(); }

    public static StoryBook defaultBook() {
        return new StoryBook()
            // ---------- pembuka ronde ----------
            .add(new StoryScene(INTRO_QF, Speaker.COACH,
                "{nama}, ini perempat final Liga Champions. Lawan kita Rhein Eagles, juara Jerman yang rapi dan disiplin. Apa rencanamu?", List.of(
                StoryChoice.of("Main sabar, kuasai bola", "Bima mengangguk. \"Kontrol permainan, itu kuncinya.\"", null)
                        .withStat(Stat.PASSING, 2),
                StoryChoice.of("Serang dari menit pertama", "\"Berani! Tapi jaga napasmu.\"", null)
                        .withStat(Stat.SHOOTING, 2).withEnergy(-5),
                StoryChoice.of("Saya siap lari 90 menit", "\"Stamina itu modal besar.\"", null)
                        .withStat(Stat.STAMINA, 2))))
            .add(new StoryScene(INTRO_SF, Speaker.TEAMMATE,
                "Semi final lawan Costa Azul. Mereka suka menyerang, jadi ruang di belakang mereka terbuka. Kamu mau ambil peran apa, {nama}?", List.of(
                StoryChoice.of("Jadi pemberi umpan terakhir", "Rafi tersenyum. \"Aku akan lari ke ruang kosong.\"", null)
                        .withStat(Stat.PASSING, 2),
                StoryChoice.of("Jadi pemburu gol", "\"Oke, kuberi kamu bolanya.\"", null)
                        .withStat(Stat.SHOOTING, 2),
                StoryChoice.of("Bantu bertahan dulu", "\"Kerja keras seperti itu yang bikin tim ini kuat.\"", null)
                        .withStat(Stat.STAMINA, 2).requiring(Stat.STAMINA, 52))))
            .add(new StoryScene(INTRO_FINAL, Speaker.COACH,
                "Final. Nordkap United, juara bertahan. Seluruh dunia menonton. Ini momen yang kamu tunggu seumur hidup, {nama}.", List.of(
                StoryChoice.of("Saya nggak takut. Kita menang!", "Bima tertawa. \"Itu semangat yang kucari.\"", null)
                        .withStat(Stat.SHOOTING, 2).withEnergy(-5),
                StoryChoice.of("Kita main seperti biasa", "\"Tenang dan rapi. Bagus.\"", null)
                        .withStat(Stat.PASSING, 2),
                StoryChoice.of("Saya butuh waktu menenangkan diri", "Bima menepuk bahumu. Napasmu kembali teratur.", null)
                        .withEnergy(10))))
            // ---------- di dalam match ----------
            .add(new StoryScene(HALFTIME_TALK, Speaker.COACH,
                "Kita tertinggal. Bima menatapmu di ruang ganti. \"Aku butuh kamu bangkit di babak kedua.\"", List.of(
                StoryChoice.of("Janji main lebih agresif", "Kamu terbakar semangat.", null)
                        .withStat(Stat.SHOOTING, 1).withEnergy(-5),
                StoryChoice.of("Minta fokus ke umpan", "Rencana jelas, tim kembali tenang.", null)
                        .withStat(Stat.PASSING, 1))))
            // ---------- setelah leg pertama ----------
            .add(new StoryScene(WIN_INTERVIEW, Speaker.JOURNALIST,
                "Maya menghadangmu usai laga. \"Menang di leg pertama! Apa rahasianya, {nama}?\"", List.of(
                StoryChoice.of("Puji rekan setim", "Rekan setim makin solid denganmu.", null)
                        .withStat(Stat.PASSING, 1),
                StoryChoice.of("Bilang kamu latihan ekstra", "Maya tertarik. Pelatih mendengar itu...", COACH_NOTICE)
                        .withStat(Stat.SHOOTING, 1).withEnergy(-5))))
            .add(new StoryScene(COACH_NOTICE, Speaker.COACH,
                "Bima memanggilmu. \"Kudengar kamu rajin latihan ekstra.\"", List.of(
                StoryChoice.of("Tetap rendah hati", "Bima mengangguk puas.", null)
                        .withStat(Stat.STAMINA, 1),
                StoryChoice.of("Minta main penuh 90 menit", "Bima setuju memberimu kepercayaan penuh.", null)
                        .withStat(Stat.SHOOTING, 2).requiring(Stat.SHOOTING, 55))))
            .add(new StoryScene(LOSS_LOCKER_ROOM, Speaker.TEAMMATE,
                "Ruang ganti sunyi. Rafi menatap lantai. \"Kita masih punya leg kedua...\"", List.of(
                StoryChoice.of("Salahkan wasit", "Suasana makin keruh.", null)
                        .withStat(Stat.PASSING, -1),
                StoryChoice.of("Ajak evaluasi bareng", "Tim mulai bangkit.", null)
                        .withStat(Stat.PASSING, 1),
                StoryChoice.of("Janji menebusnya di leg kedua", "Rafi menepuk punggungmu.", null)
                        .withStat(Stat.SHOOTING, 1))))
            // ---------- lolos ----------
            .add(new StoryScene(QF_ADVANCE, Speaker.JOURNALIST,
                "Garuda FC lolos ke semi final! Maya mengejarmu. \"Kamu bikin kejutan, {nama}. Semua orang membicarakan klub ini.\"", List.of(
                StoryChoice.of("Kami baru mulai", "Maya mencatat kalimat itu dengan senyum.", null)
                        .withStat(Stat.SHOOTING, 1),
                StoryChoice.of("Ini kerja tim", "Rafi di belakangmu ikut tersenyum.", null)
                        .withStat(Stat.PASSING, 1),
                StoryChoice.of("Maaf, saya mau istirahat", "Kamu pulang lebih awal dan tidur nyenyak.", null)
                        .withEnergy(10))))
            .add(new StoryScene(SF_ADVANCE, Speaker.TEAMMATE,
                "FINAL! Rafi memelukmu sambil berteriak. \"Kita ke final, {nama}!\"", List.of(
                StoryChoice.of("Teriak bareng sekuat tenaga", "Seluruh ruang ganti ikut bernyanyi.", null)
                        .withStat(Stat.STAMINA, 1),
                StoryChoice.of("Tetap fokus, belum selesai", "Rafi mengangguk. \"Benar. Satu laga lagi.\"", null)
                        .withStat(Stat.PASSING, 1))))
            // ---------- akhir cerita ----------
            .add(new StoryScene(TROPHY, Speaker.NARRATOR,
                "Peluit panjang berbunyi. Garuda FC juara Liga Champions! Trofi terangkat tinggi dan kamu ada di tengah kerumunan itu.", List.of(
                StoryChoice.of("Angkat trofi bersama tim", "Konfeti memenuhi langit stadion.", null),
                StoryChoice.of("Cari Coach Bima", "Kamu menembus kerumunan mencarinya.", TROPHY_COACH))))
            .add(new StoryScene(TROPHY_COACH, Speaker.COACH,
                "Bima memelukmu erat. \"Aku selalu tahu kamu bisa, {nama}.\"", List.of(
                StoryChoice.of("Terima kasih, Coach", "Kalian berdua tertawa sambil menangis.", null))))
            .add(new StoryScene(FINAL_LOST, Speaker.COACH,
                "Kita kalah di final. Bima menepuk punggungmu. \"Kita akan bangkit lagi.\"", List.of(
                StoryChoice.of("Saya akan kembali lebih kuat", "\"Itu yang kuharapkan darimu.\"", null),
                StoryChoice.of("Maaf, Coach", "\"Kamu sudah memberikan segalanya.\"", null))))
            .add(new StoryScene(ELIMINATED, Speaker.TEAMMATE,
                "Kita tersingkir. Rafi duduk lemas di bangku. \"Tinggal sedikit lagi...\"", List.of(
                StoryChoice.of("Kita bangkit musim depan", "Rafi mengangkat kepala dan mengangguk.", null),
                StoryChoice.of("Diam dan duduk di sebelahnya", "Kalian berdua tak bicara. Itu cukup.", null))));
    }
}
