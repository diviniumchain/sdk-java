package com.ambrosus.sdk;

import java.util.Objects;

public class Identifier {

    static final String TYPE_ASSET = "ambrosus.asset.identifiers";
    static final String TYPE_EVENTS = "ambrosus.event.identifiers";

    public static final String GTIN = "gtin";
    public static final String EAN13 = "ean13";
    public static final String EAN8 = "ean8";

    public final String type;
    public final String value;

    public Identifier(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}