package com.finpro.frontend.story;

import com.finpro.frontend.model.Stat;

/**
 * Satu pilihan di scene cerita. Contoh bikin konten:
 *   StoryChoice.of("Puji rekan", "Rekan senang.", null).withStat(Stat.PASSING, 1)
 *   StoryChoice.of("Minta jatah main", "Pelatih setuju.", "NEXT").requiring(Stat.SHOOTING, 60)
 * nextSceneId = null artinya cerita selesai.
 */
public record StoryChoice(String label, String resultText, String nextSceneId,
                          Stat stat, int statDelta, int energyDelta,
                          Stat requiredStat, int requiredValue) {

    public static StoryChoice of(String label, String resultText, String nextSceneId) {
        return new StoryChoice(label, resultText, nextSceneId, null, 0, 0, null, 0);
    }

    public StoryChoice withStat(Stat s, int delta) {
        return new StoryChoice(label, resultText, nextSceneId, s, delta, energyDelta, requiredStat, requiredValue);
    }

    public StoryChoice withEnergy(int delta) {
        return new StoryChoice(label, resultText, nextSceneId, stat, statDelta, delta, requiredStat, requiredValue);
    }

    public StoryChoice requiring(Stat s, int minValue) {
        return new StoryChoice(label, resultText, nextSceneId, stat, statDelta, energyDelta, s, minValue);
    }
}
