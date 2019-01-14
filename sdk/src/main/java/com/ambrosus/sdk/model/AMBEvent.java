package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.utils.Assert;
import com.ambrosus.sdk.utils.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.internal.platform.Platform;

public class AMBEvent extends Event {

    private static final String ATTR_KEY_IMAGES = "images";
    private static final String ATTR_KEY_DOCUMENTS = "documents";
    private static final String ATTR_KEY_NAME = "name";

    private final String type;
    private final String name;

    private final Map<String, JsonObject> images;
    private final Map<String, JsonObject> documents;
    private final Map<String, JsonElement> attributes;

    private final Location location;

    /**
     *
     * @param source - any source event which contains at least one data object of "ambrosus" type (see AmbrosusData implementation)
     */
    public AMBEvent(Event source){
        super(source);

        List<String> ambrosusDataTypes = AmbrosusData.getAmbrosusDataTypes(source.getDataTypes());

        Assert.assertTrue(!ambrosusDataTypes.isEmpty(), IllegalArgumentException.class, "Source event is not valid Ambrosus event.");
        type = ambrosusDataTypes.get(0);

        JsonObject mainDataObject = getDataObject(type);

        name = getEventName(mainDataObject);

        images = Collections.unmodifiableMap(getEntityMap(ATTR_KEY_IMAGES, mainDataObject));
        documents = Collections.unmodifiableMap(getEntityMap(ATTR_KEY_DOCUMENTS, mainDataObject));
        attributes = Collections.unmodifiableMap(getAttributesMap(mainDataObject));

        JsonObject locationDataJson = getDataObject("ambrosus.event.location");
        location = locationDataJson != null ? Location.createFrom(locationDataJson) : null;
    }

    @NonNull
    public String getType(){
        return type;
    }

    @NonNull
    public String getName() {
        return name != null ? name : getSystemId();
    }

    public Map<String, JsonElement> getAttributes() {
        return attributes;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return Strings.defaultToString(this) + String.format(Locale.US, "(name: %s, type: %s)", getName(), getType());
    }

    private static String getEventName(JsonObject dataObject) {
        JsonElement jsonElement = dataObject.get(ATTR_KEY_NAME);
        return jsonElement != null ? jsonElement.getAsString() : null;
    }

    @NonNull
    //package-local for tests
    static Map<String, JsonObject> getEntityMap(String entityName, JsonObject dataObject){
        Map<String, JsonObject> result = new LinkedHashMap<>();
        try {
            JsonObject entityJson = dataObject.getAsJsonObject(entityName);
            if(entityJson != null) { //if we have this section
                for (String imageKey : entityJson.keySet()) {
                    JsonElement imageAttrsElement = entityJson.get(imageKey);
                    if(imageAttrsElement.isJsonObject()) {
                        result.put(imageKey, imageAttrsElement.getAsJsonObject());
                    }
                }
            }
        } catch(RuntimeException e) {
            Platform.get().log(Platform.WARN, "Can't parse Ambrosus event images", e);
        }
        return result;
    }

    //package-local for tests
    static Map<String, JsonElement> getAttributesMap(JsonObject dataObject){
        HashSet<String> reservedAttrs = new HashSet<>();

        reservedAttrs.add(ATTR_KEY_TYPE);
        reservedAttrs.add(ATTR_KEY_IMAGES);
        reservedAttrs.add(ATTR_KEY_DOCUMENTS);

        Map<String, JsonElement> result = new LinkedHashMap<>();
        for (String key : dataObject.keySet()) {
            if(!reservedAttrs.contains(key))
                result.put(key, dataObject.get(key));
        }

        return result;
    }
}
