package com.finpro.frontend.tournament;

/** Satu pertandingan di turnamen. stage: 0 = perempat final, 1 = semi final, 2 = final. */
public record Round(int stage, String stageName, int leg, int legs, boolean homeVenue) {

    public String getName() {
        return legs > 1 ? stageName + " - Leg " + leg : stageName;
    }

    /** True kalau hasil ronde ini menentukan lolos/tersingkir. */
    public boolean isDeciding() { return leg == legs; }

    public String getVenueLabel() { return homeVenue ? "Kandang" : "Tandang"; }
}
