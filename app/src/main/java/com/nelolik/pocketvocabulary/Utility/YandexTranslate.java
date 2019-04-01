package com.nelolik.pocketvocabulary.Utility;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class YandexTranslate {
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    public static final String API_KEY =
            "trnsl.1.1.20190401T173554Z.0da2696c90503551.7fec7d4049e58fb4b843d9de89cf2c85ec9a5cff";

    public YandexTranslate(){

    }

    public static Request getQueryUrl(String text) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
        urlBuilder.addQueryParameter("key", API_KEY)
                .addQueryParameter("text", text)
                .addQueryParameter("lang", "en-ru")
                .addQueryParameter("format", "plain");
        String url = urlBuilder.build().toString();
        return new Request.Builder().url(url).build();
    }
}
