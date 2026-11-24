package com.finpro.frontend.event;

public record GameEvent(GameEventType type, String message, int value) { }
