package com.calian.at.training.prometheus;

import java.util.HashMap;
import java.util.Map;

public enum SelectedSatellite {UNKNOWN(0),
    SATELLITE_A(1),
    SATELLITE_B(2),
    STARLINK(3),
    MY_SUPER_SECRET_SATELLITE(4);

    private int value;
    private static Map valueMap = new HashMap<>();

    private SelectedSatellite(int value) {
        this.value = value;
    }

    static {
        for (SelectedSatellite pageType : SelectedSatellite.values()) {
            valueMap.put(pageType.value, pageType);
        }
    }

    public static SelectedSatellite valueOf(int pageType) {
        return (SelectedSatellite) valueMap.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
