package com.nelolik.pocketvocabulary;

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

import java.util.ArrayList;
import java.util.List;

public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.WordViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private OnButtonClickListener mListener;

    WordsListAdapter(Context context, Cursor cursor, OnButtonClickListener listener) {
        mContext = context;
//        mCursor = cursor;
        mCursor = cursor;
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
            wordViewHolder.mNewWordInput.setVisibility(View.VISIBLE);
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
            mButtonTranslate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mNewWordInput.getText().toString();
                    if (!text.isEmpty()) {
                        mListener.onButtonTranslate(text);
                    }
                }
            });
            mButtonAdd = itemView.findViewById(R.id.btn_add);
            mButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonAddClick(mNewWordInput.getText().toString(),
                            mTranslationInput.getText().toString());
                    mNewWordInput.setText("");
                    mTranslationInput.setText("");
                }
            });
            mNewWordInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mListener.onOriginChanged(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public interface OnButtonClickListener {
        void onButtonTranslate(String text);
        void onButtonAddClick (String origin, String translation);
        void onOriginChanged(String new_origin);
    }
}
