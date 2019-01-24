/*
 * Copyright: Ambrosus Technologies GmbH
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.sdk;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.ambrosus.sdk.utils.GsonUtil;
import com.ambrosus.sdk.utils.Strings;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private final Service service;

    public Network(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                System.out.println(message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gateway-test.ambrosus.com/")
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(client)
                .build();

        service = retrofit.create(Service.class);
    }

    @NonNull
    public NetworkCall<Asset> getAsset(@NonNull String assetId) {
        return new NetworkCallWrapper<>(service.getAsset(assetId), new MissingEntityErrorHandler());
    }

    @NonNull
    public NetworkCall<Event> getEvent(@NonNull String eventId) {
        return new NetworkCallWrapper<>(service.getEvent(eventId), new MissingEntityErrorHandler());
    }

    @NonNull
    public NetworkCall<SearchResult<Asset>> findAssets(@NonNull AssetSearchParams searchParams) {
        return new NetworkCallWrapper<>(service.findAssets(searchParams.queryParams));
    }

    @NonNull
    public NetworkCall<SearchResult<Event>> findEvents(@NonNull EventSearchParams searchParams) {
        return new NetworkCallWrapper<>(service.findEvents(searchParams.queryParams));
    }

    @NonNull
    public NetworkCall<Asset> pushAsset(Asset asset, String privateKey) {
        return new NetworkCallWrapper<>(service.createAsset(getABMAuthHeader(privateKey), asset), new AccessDeniedErrorHandler());
    }

    private static String getABMAuthHeader(String privateKey){
        return "AMB " + Strings.getWithHexPrefix(privateKey);
    }

    /**
     *
     * @param duration - duration in milliseconds
     * @param privateKeyStr - private key as a hex string, can contain '0x' prefix
     * @return
     */
    static String createAMBTokenFor(long duration, String privateKeyStr) {
        return createAMBToken(privateKeyStr, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + duration));
    }

    /**
     *
     * @param privateKeyStr see {@link #createAMBTokenFor(long, String)}
     * @param validUntil - duration in seconds
     * @return
     */
    static String createAMBToken(String privateKeyStr, long validUntil) {
        BigInteger privateKey = Numeric.toBigInt(privateKeyStr/*can contain 0x prefix*/);
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        String address = Keys.toChecksumAddress(Keys.getAddress(keyPair)); // account address associated with private key

        JsonObject idData = new JsonObject();
        idData.addProperty("createdBy", address);
        idData.addProperty("validUntil", validUntil);

        String signature = Ethereum.computeSignature(idData.toString(), keyPair);

        JsonObject token = new JsonObject();
        token.add("idData", idData);
        token.addProperty("signature", signature);

        String tokenString = token.toString();
        //TODO use different encoder for pure java version
        return Base64.encodeToString(tokenString.getBytes(), Base64.DEFAULT);
    }

    static String getObjectHash(Object object){
        return Ethereum.computeHashString(GsonUtil.getLexNormalizedJsonStr(object));
    }

    static String getObjectSignature(Object object, String privateKey){
        return Ethereum.computeSignature(GsonUtil.getLexNormalizedJsonStr(object), Ethereum.getEcKeyPair(privateKey));
    }

}
