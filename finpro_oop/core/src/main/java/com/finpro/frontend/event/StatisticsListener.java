package com.finpro.frontend.event;

import com.finpro.frontend.model.CareerRecord;
import java.util.function.Supplier;

/** Update CareerRecord otomatis dari event, tanpa Match tahu soal statistik. */
public class StatisticsListener implements GameEventListener {
    private final Supplier<CareerRecord> recordSupplier;

    public StatisticsListener(Supplier<CareerRecord> recordSupplier) {
        this.recordSupplier = recordSupplier;
    }

    @Override
    public void onEvent(GameEvent event) {
        CareerRecord record = recordSupplier.get();
        if (record == null) return;
        switch (event.type()) {
            case PLAYER_GOAL -> record.addGoals(event.value());
            case PLAYER_ASSIST -> record.addAssists(event.value());
            case FULL_TIME -> record.addResult(event.value());
            default -> { }
        }
    }
}
