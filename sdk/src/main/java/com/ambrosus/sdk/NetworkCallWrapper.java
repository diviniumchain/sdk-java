package com.ambrosus.sdk;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class NetworkCallWrapper<T> implements AMBNetworkCall<T> {

    private static final String TAG = NetworkCallWrapper.class.getName();

    private final Call<T> retrofitCall;
    private final List<NetworkErrorHandler> errorHandlers;

    NetworkCallWrapper(Call<T> retrofitCall) {
        this(retrofitCall, (NetworkErrorHandler) null);
    }


    NetworkCallWrapper(Call<T> retrofitCall, NetworkErrorHandler ... errorHandlers) {
        this.retrofitCall = retrofitCall;

        this.errorHandlers = new ArrayList<>();

        if(errorHandlers != null)
            this.errorHandlers.addAll(Arrays.asList(errorHandlers));

        this.errorHandlers.add(new CommonNetworkErrorHandler());
    }

    @Override
    public T execute() throws Throwable {
        return getResponseResult(retrofitCall.execute());
    }

    @Override
    public void enqueue(final AMBNetworkCallback<T> callback) {
        retrofitCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                T responseResult;
                try {
                    responseResult = getResponseResult(response);
                } catch (Exception e) {
                    onFailure(call, e);
                    return;
                }
                callback.onSuccess(NetworkCallWrapper.this, responseResult);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailure(NetworkCallWrapper.this, t);
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return retrofitCall.isExecuted();
    }

    @Override
    public void cancel() {
        retrofitCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return retrofitCall.isCanceled();
    }

    @Override
    public AMBNetworkCall<T> clone() {
        return new NetworkCallWrapper<>(retrofitCall.clone());
    }

    <T> T getResponseResult(Response<T> response) throws Exception{
        checkForNetworkError(response);
        return response.body();
    }

    private void checkForNetworkError(Response response) throws Exception {
        if(!response.isSuccessful()) {
            String responseString =  response.errorBody().string();

            String message = null;

            try {
                JsonParser parser = new JsonParser();
                JsonElement responseJSON = parser.parse(responseString);

                if(responseJSON.isJsonObject()) {
                    JsonElement reason = responseJSON.getAsJsonObject().get("reason");
                    if(reason != null)
                        message = reason.toString();
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Can't parse error response: " + responseString);
            }

            int code = response.code();

            for (NetworkErrorHandler errorHandler : errorHandlers) {
                errorHandler.handleNetworkError(code, message);
            }
        }
    }
}
