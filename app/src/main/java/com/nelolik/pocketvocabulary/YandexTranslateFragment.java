package com.nelolik.pocketvocabulary;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nelolik.pocketvocabulary.Utility.YandexTranslate;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class YandexTranslateFragment extends Fragment {


    public YandexTranslateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yandex_translate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String text;
        Request request;
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey("text")) {
                text = getArguments().getString("text");
                request = YandexTranslate.getQueryUrl(text);
                OkHttpClient client = new OkHttpClient();
                TextView textToTranslate = view.findViewById(R.id.text_to_translate);
                textToTranslate.setText(text);
                final TextView translatedText = view.findViewById(R.id.translated_text);
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String textResponse = response.body().string();
                        Activity activity = getActivity();
                        int code = response.code();
                        Toast.makeText(activity, "Response code: " + code, Toast.LENGTH_SHORT).show();
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    translatedText.setText(textResponse);
                                }
                            });
                        }
                    }
                });
            }
        }

    }
}
