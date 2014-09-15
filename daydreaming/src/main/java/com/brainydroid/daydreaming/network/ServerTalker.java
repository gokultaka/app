package com.brainydroid.daydreaming.network;

import android.content.Context;

import com.brainydroid.daydreaming.background.Logger;
import com.brainydroid.daydreaming.db.Json;
import com.brainydroid.daydreaming.db.ParametersStorage;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.security.KeyPair;

@Singleton
public class ServerTalker {

    private static String TAG = "ServerTalker";

    @Inject Context context;
    @Inject ParametersStorage parametersStorage;
    @Inject ProfileFactory profileFactory;
    @Inject CryptoStorage cryptoStorage;
    @Inject Json json;

    private synchronized String getPostResultUrl() {
        return parametersStorage.getBackendApiUrl() + ServerConfig.YE_URL_RESULTS;
    }

    private synchronized String getPutProfileUrl() {
        return parametersStorage.getBackendApiUrl() + ServerConfig.YE_URL_PROFILES + "/"
                + cryptoStorage.getMaiId();
    }

    public synchronized void register(KeyPair keyPair,
                                      HttpConversationCallback callback) {
        Logger.i(TAG, "Registering at the server");

        Logger.d(TAG, "Getting key to register");
        String vkPem = cryptoStorage.createArmoredPublicKey(keyPair.getPublic());
        ProfileWrapper profileWrap = profileFactory.create(
                parametersStorage.getBackendExpId(), vkPem).buildWrapper();
        String jsonPayload = json.toJsonPublic(profileWrap);

        Logger.i(TAG, "Going to send the following to server (signed):");
        Logger.iRaw(TAG, jsonPayload);
        String signedJson = cryptoStorage.signJws(jsonPayload, keyPair.getPrivate());
        Logger.i(TAG, "Signed form:");
        Logger.iRaw(TAG, signedJson);

        String postUrl = parametersStorage.getBackendApiUrl() +
                ServerConfig.YE_URL_PROFILES;

        HttpPostData postData = new HttpPostData(postUrl, callback);
        postData.setPostString(signedJson);
        postData.setContentType("application/json");

        HttpPostTask postTask = new HttpPostTask();
        Logger.d(TAG, "Executing POST task for registration");
        postTask.execute(postData);
    }

    private synchronized void signAndPostData(
            String url, String data, HttpConversationCallback callback) {
        Logger.i(TAG, "Signing and POSTing data to server");

        Logger.d(TAG, "Signing data");
        String signedData = cryptoStorage.signJws(data);

        Logger.d(TAG, "Url is {}", url);
        HttpPostData postData = new HttpPostData(url, callback);
        postData.setPostString(signedData);
        postData.setContentType("application/jws");

        HttpPostTask postTask = new HttpPostTask();
        Logger.d(TAG, "Executing POST task for data upload");
        postTask.execute(postData);
    }

    public synchronized void signAndPostResult(
            String data, HttpConversationCallback callback) {
        signAndPostData(getPostResultUrl(), data, callback);
    }

    private synchronized void signAndPutData(
            String url, String data, HttpConversationCallback callback) {
        Logger.i(TAG, "Signing and PUTing data to server");

        Logger.d(TAG, "Signing data");
        String signedData = cryptoStorage.signJws(data);

        Logger.d(TAG, "Url is {}", url);
        HttpPutData putData = new HttpPutData(url, callback);
        putData.setPutString(signedData);
        putData.setContentType("application/jws");

        HttpPutTask putTask = new HttpPutTask();
        Logger.d(TAG, "Executing PUT task for data upload");
        putTask.execute(putData);
    }


    public synchronized void signAndPutProfile(
            String data, HttpConversationCallback callback) {
        signAndPutData(getPutProfileUrl(), data, callback);
    }

}
