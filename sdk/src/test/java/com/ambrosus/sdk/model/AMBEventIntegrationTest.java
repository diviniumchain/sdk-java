package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.EventSearchParamsBuilder;
import com.ambrosus.sdk.NetworkCall;
import com.ambrosus.sdk.SearchResult;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AMBEventIntegrationTest {

    private static AMBNetwork network;

    @BeforeClass
    public static void setUpNetwork(){
        network = new AMBNetwork();
    }

    @Test
    public void findEventsSimpleTest(){
        final String expectedAssetID = "0xfe7d80686adf18b2259a60d836104ab866a20b6eabf3bc4cde75ffc4aa8015a0";

        AMBEventSearchParamsBuilder searchParamsBuilder = new AMBEventSearchParamsBuilder().byDataObjectIdentifier(Identifier.EAN13, "6942507312009");

        NetworkCall<SearchResult<Event>> networkCall = network.findEvents(searchParamsBuilder.build());

        try {
            SearchResult<Event> result = networkCall.execute();
            for (Event event : result.getValues()) {
                if(expectedAssetID.equals(event.getAssetId()))
                    return;
            }
            fail("Wasn't able to find event which has specified identifier and belongs to asset with ID: " + expectedAssetID);
            System.out.println();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Test
    public void findAmbrosusEventsTest(){

        EventSearchParamsBuilder searchParamsBuilder = new EventSearchParamsBuilder();
        searchParamsBuilder.forAsset("0x602023f73ab25f0c95a3cf4e92c9cb2f4c9c09dbd3ca6e167d362de6e7f1eeae");

        NetworkCall<List<AMBEvent>> networkCall = network.findAMBEvents(searchParamsBuilder.build());

        try {
            List<AMBEvent> ambEvents = networkCall.execute();
            assertEquals(9, ambEvents.size());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}