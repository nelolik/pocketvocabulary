package com.nelolik.pocketvocabulary;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.WordViewHolder> {

    private List<String> mCursor;
    private Context mContext;

    public WordsListAdapter(Context context, Cursor cursor) {
        mContext = context;
//        mCursor = cursor;
        mCursor = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) mCursor.add(String.valueOf(i));
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
            wordViewHolder.mOriginWord.setText("word " + i);
            wordViewHolder.mTranslation.setText("translations");
            wordViewHolder.mTranslationInput.setVisibility(View.GONE);
            wordViewHolder.mNewWordInput.setVisibility(View.GONE);
            wordViewHolder.mButtonAdd.setVisibility(View.GONE);
        } else {
            wordViewHolder.mOriginWord.setVisibility(View.GONE);
            wordViewHolder.mTranslation.setVisibility(View.GONE);
            wordViewHolder.mTranslationInput.setVisibility(View.VISIBLE);
            wordViewHolder.mNewWordInput.setVisibility(View.VISIBLE);
            wordViewHolder.mButtonAdd.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.size();//getCount();
        }
        return 0;
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {

        EditText mNewWordInput;
        EditText mTranslationInput;
        TextView mOriginWord;
        TextView mTranslation;
        Button mButtonAdd;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public WordViewHolder(@NonNull View itemView, boolean isFirst) {
            super(itemView);
            mNewWordInput = itemView.findViewById(R.id.et_origin_word_input);
            mTranslationInput = itemView.findViewById(R.id.et_translation_input);
            mOriginWord = itemView.findViewById(R.id.tv_origin_word);
            mTranslation = itemView.findViewById(R.id.tv_translation);
            mButtonAdd = itemView.findViewById(R.id.btn_add);
        }
    }
}
