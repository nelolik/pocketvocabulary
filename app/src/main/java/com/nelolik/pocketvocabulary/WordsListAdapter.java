package com.nelolik.pocketvocabulary;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nelolik.pocketvocabulary.Utility.TranslationsDBContract;
import com.nelolik.pocketvocabulary.Utility.YandexTranslate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.WordViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private Activity mActivity;
    private OnButtonClickListener mListener;
    private String mPreviousOrigString = "";
    private String mTranslationText = "";

    WordsListAdapter(Context context, Cursor cursor,
                     Activity activity, OnButtonClickListener listener) {
        mContext = context;
        mCursor = cursor;
        mActivity = activity;
        mListener = listener;
    }

    void setCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        view = inflater.inflate(R.layout.new_word_view, viewGroup, false);
        return new WordViewHolder(view, true);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder wordViewHolder, int i) {
        if (i > 0) {
            wordViewHolder.mOriginWord.setVisibility(View.VISIBLE);
            wordViewHolder.mTranslation.setVisibility(View.VISIBLE);
            wordViewHolder.mTranslationInput.setVisibility(View.GONE);
            wordViewHolder.mNewWordInput.setVisibility(View.GONE);
            wordViewHolder.mButtonTranslate.setVisibility(View.GONE);
            wordViewHolder.mButtonAdd.setVisibility(View.GONE);
            if (mCursor.moveToPosition(i - 1)) {
                int indxWord = mCursor.getColumnIndex(TranslationsDBContract.COLUMN_ORIGIN);
                int indxTrans = mCursor.getColumnIndex(TranslationsDBContract.COLUMN_TRANSLATION);
                String word = mCursor.getString(indxWord);
                String translation = mCursor.getString(indxTrans);
                wordViewHolder.mOriginWord.setText(word);
                wordViewHolder.mTranslation.setText(translation);
            }
        } else {
            wordViewHolder.mOriginWord.setVisibility(View.GONE);
            wordViewHolder.mTranslation.setVisibility(View.GONE);
            wordViewHolder.mTranslationInput.setVisibility(View.VISIBLE);
            wordViewHolder.mTranslationInput.setText(mTranslationText);
            wordViewHolder.mNewWordInput.setVisibility(View.VISIBLE);
            wordViewHolder.mNewWordInput.setText(mPreviousOrigString);
            wordViewHolder.mNewWordInput.setSelection(mPreviousOrigString.length());
            wordViewHolder.mButtonTranslate.setVisibility(View.VISIBLE);
            wordViewHolder.mButtonAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount() + 1;
        }
        return 1;
    }

    class WordViewHolder extends RecyclerView.ViewHolder {

        EditText mNewWordInput;
        EditText mTranslationInput;
        TextView mOriginWord;
        TextView mTranslation;
        Button mButtonTranslate;
        Button mButtonAdd;

        WordViewHolder(@NonNull final View itemView, boolean isFirst) {
            super(itemView);
            mNewWordInput = itemView.findViewById(R.id.et_origin_word_input);
            mTranslationInput = itemView.findViewById(R.id.et_translation_input);
            mOriginWord = itemView.findViewById(R.id.tv_origin_word);
            mTranslation = itemView.findViewById(R.id.tv_translation);
            mButtonTranslate = itemView.findViewById(R.id.btn_translate);
            mButtonTranslate.setOnClickListener((v) ->  onButtonTranslate());
            mButtonAdd = itemView.findViewById(R.id.btn_add);
            mButtonAdd.setOnClickListener((v) -> {
                    mListener.onButtonAddClick(mNewWordInput.getText().toString(),
                            mTranslationInput.getText().toString());
                    mNewWordInput.setText("");
                    mTranslationInput.setText("");
                    mTranslationText = "";
            });
            mNewWordInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (mPreviousOrigString.contentEquals(s)) {
                        return;
                    }
                    mPreviousOrigString = s.toString();
                    mTranslationText = "";
                    mListener.onOriginChanged(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        private void onButtonTranslate() {
            String text = mNewWordInput.getText().toString();
            if (text.isEmpty()) {
                return;
            }
            Request request = YandexTranslate.getQueryUrl(text);
            if (request == null) {
                return;
            }
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 200) {
                        Toast.makeText(mContext,
                                "Response code from Yandex Translate: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String translation = YandexTranslate.parseJSONFromResponse(response);
                    if (translation == null) {
                        return;
                    }
                    mTranslationText = translation;
                    mActivity.runOnUiThread(() -> { notifyDataSetChanged(); });
                }
            });
        }
    }

    public interface OnButtonClickListener {
        void onButtonAddClick (String origin, String translation);
        void onOriginChanged(String new_origin);
    }
}
