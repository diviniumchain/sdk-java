package com.ambrosus.sdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambrosus.sdk.utils.Assert;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Event implements Serializable {

    public static final String DATA_OBJECT_ATTR_TYPE = "type";

    private final String systemId;
    private final String assetId;
    private final String createdBy;

    private final long timeStamp;

    private final MetaData metaData;
    private final List<JsonObject> rawData;

    Event(String systemId, String assetId, String createdBy, long timeStamp, MetaData metaData, List<JsonObject> rawData) {
        this.systemId = Assert.assertNotNull(systemId, "systemId == null");
        this.assetId = Assert.assertNotNull(assetId, "assetId == null");
        this.createdBy = Assert.assertNotNull(createdBy, "createdBy == null");
        this.timeStamp = timeStamp;
        this.metaData = Assert.assertNotNull(metaData, "metaData == null");

        ensureRawDataObjectTypes(rawData);

        this.rawData = Collections.unmodifiableList(rawData);
    }

    protected Event(Event source){
        this(source.systemId, source.assetId, source.createdBy, source.timeStamp, source.metaData, source.rawData);
    }

    public String getSystemId() {
        return systemId;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAuthorId() {
        return createdBy;
    }

    public long getGMTTimeStamp() {
        return timeStamp;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public List<JsonObject> getRawData() {
        return rawData;
    }

    //we need to be sure about the order of dataTypes in some cases, so result is list
    @NonNull
    public List<String> getDataTypes() {
        List<String> result = new ArrayList<>();
        for (JsonObject dataObject : rawData) {
            result.add(getDataObjectType(dataObject));
        }
        return result;
    }

    @Nullable
    public JsonObject getDataObject(String type) {
        for (JsonObject dataObject : getRawData()) {
            if(type.equals(getDataObjectType(dataObject)))
                return dataObject;
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(Locale.US, "(%s)", getSystemId());
    }

    private static void ensureRawDataObjectTypes(List<JsonObject> rawData){
        for (JsonObject dataObject : rawData) {
            getDataObjectType(dataObject);
        }
    }

    public static String getDataObjectType(JsonObject dataObject) {
        if(dataObject.has(DATA_OBJECT_ATTR_TYPE))
            return dataObject.get(DATA_OBJECT_ATTR_TYPE).getAsString();
        throw new IllegalArgumentException("Invalid data object: " + dataObject.toString() + " (missing type key)");
    }
}
