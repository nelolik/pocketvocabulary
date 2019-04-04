package com.nelolik.pocketvocabulary;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nelolik.pocketvocabulary.Utility.YandexTranslate;

import org.json.JSONException;
import org.json.JSONObject;

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

    private UserActionListener mListener = null;

    public YandexTranslateFragment() {
        // Required empty public constructor
    }

    void setUserActionListener (UserActionListener listener) {
        mListener = listener;
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
        Request request;
        Bundle arguments = getArguments();
        requestToYandexTranslate(arguments, view);
        Button btnSave = view.findViewById(R.id.save_translation);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    TextView textToTranslate = view.findViewById(R.id.text_to_translate);
                    TextView translationTextView = view.findViewById(R.id.translated_text);
                    mListener.onButtonAddWordListener(textToTranslate.getText().toString(),
                            translationTextView.getText().toString());
                    getActivity().onBackPressed();
                }
            }
        });

    }

    private void requestToYandexTranslate(Bundle arguments, View view) {
        if (arguments == null) {
            return;
        }
        if (!arguments.containsKey("text")) {
            return;
        }
        String text = getArguments().getString("text");
        Request request = YandexTranslate.getQueryUrl(text);
        OkHttpClient client = new OkHttpClient();
        TextView textToTranslate = view.findViewById(R.id.text_to_translate);
        textToTranslate.setText(text);
        final TextView translationTextView = view.findViewById(R.id.translated_text);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    return;
                }
                final String translation = parseJSONfromResponse(response);
                if (translation == null) {
                    return;
                }
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        translationTextView.setText(translation);
                    }
                });
            }
        });
    }

    private String parseJSONfromResponse(Response response) {
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
        }
        return null;
    }

    public interface UserActionListener {
        void onButtonAddWordListener (String origin, String translation);
    }
}
