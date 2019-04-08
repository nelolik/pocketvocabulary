package com.nelolik.pocketvocabulary.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class YandexTranslate {
//    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private static final String BASE_URL = "https://translate.yandex.net";
    public static final String API_KEY =
            "trnsl.1.1.20190401T173554Z.0da2696c90503551.7fec7d4049e58fb4b843d9de89cf2c85ec9a5cff";

    public YandexTranslate(){

    }

    public static Request getQueryUrl(String text) {
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder();
        String url = null;
        try {
            urlBuilder
                    .scheme("https")
                    .host("translate.yandex.net")
                    .addPathSegment("api")
                    .addPathSegment("v1.5")
                    .addPathSegment("tr.json")
                    .addPathSegment("translate")
                    .addQueryParameter("key", API_KEY)
                    .addQueryParameter("text", text)
                    .addQueryParameter("lang", "en-ru")
                    .addQueryParameter("format", "plain");
            url = urlBuilder.build().toString();
            return new Request.Builder().url(url).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String parseJSONFromResponse(Response response) {
        try {
            String textResponse = response.body().string();
            JSONObject json = new JSONObject(textResponse);
            String fromJson = json.getString("text");
            if (fromJson.length() > 4) {
                return fromJson.substring(2, fromJson.length() - 2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
