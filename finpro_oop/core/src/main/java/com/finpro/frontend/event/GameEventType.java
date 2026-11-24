package com.finpro.frontend.event;

public enum GameEventType {
    MATCH_START,
    PLAYER_CHANCE,
    PLAYER_GOAL,
    PLAYER_ASSIST,
    TEAM_GOAL,
    GOAL_CONCEDED,
    HALF_TIME,   // value = selisih skor (home - away)
    FULL_TIME,   // value = 1 menang, 0 seri, -1 kalah
    STAT_CHANGED,
    STORY_SCENE
}
