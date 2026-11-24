package com.finpro.frontend.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Menyimpan log teks semua event untuk ditampilkan di UI. */
public class EventLogListener implements GameEventListener {
    private final List<String> lines = new ArrayList<>();

    @Override
    public void onEvent(GameEvent event) {
        lines.add(event.message());
    }

    public List<String> getLines() { return Collections.unmodifiableList(lines); }

    public List<String> last(int n) {
        int from = Math.max(0, lines.size() - n);
        return Collections.unmodifiableList(lines.subList(from, lines.size()));
    }

    public void clear() { lines.clear(); }
}
