package com.finpro.frontend.content;

import com.finpro.frontend.match.MatchStrategy;

public record Opponent(String name, MatchStrategy strategy, String description) { }
