package com.nelolik.pocketvocabulary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nelolik.pocketvocabulary.Utility.TranslationsDBContract;
import com.nelolik.pocketvocabulary.Utility.TranslationsDBHelper;
import com.nelolik.pocketvocabulary.Utility.YandexTranslate;
import com.nelolik.pocketvocabulary.dummy.DummyContent.DummyItem;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainListFragment extends Fragment
        implements WordsListAdapter.OnButtonClickListener {

    private WordsListAdapter mListAdapter;
    private SQLiteDatabase mDb;
    private HandlerThread mWorkingThread;
    private Handler mThreadHandler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mListAdapter = new WordsListAdapter(getContext(), null, getActivity(), this);
            recyclerView.setAdapter(mListAdapter);
        }
        mWorkingThread = new HandlerThread("BackgroundThread");
        mWorkingThread.start();
        mThreadHandler = new Handler(mWorkingThread.getLooper());

        TranslationsDBHelper dbHelper = new TranslationsDBHelper(getContext());
        mDb = dbHelper.getWritableDatabase();
        getDbCursor(null);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    private void getDbCursor (final String like_arg) {
        mThreadHandler.post(() -> {
                String selection= null;
                if (like_arg != null && !like_arg.isEmpty()) {
                    selection = TranslationsDBContract.COLUMN_ORIGIN + " LIKE \"" + like_arg + "%\"";
                }
                Cursor cursor = mDb.query(TranslationsDBContract.TABLE_NAME,
                        null,
                        selection,
                        null,
                        null,
                        null,
                        TranslationsDBContract.COLUMN_DATE + " DESC");
                mListAdapter.setCursor(cursor);
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
    }

    @Override
    public void onButtonAddClick(String origin, String translation) {
        if (origin == null || origin.isEmpty()
        || translation == null || translation.isEmpty()) {
            return;
        }

        String newWord = origin.trim();
        String formattedTranslation = formatTranslationString(translation);
        saveWordToDB(newWord, formattedTranslation);
    }

    @Override
    public void onOriginChanged(String new_origin) {
        getDbCursor(new_origin);
    }

    private String formatTranslationString(String input) {
        StringBuffer result = new StringBuffer();
        Pattern pattern = Pattern.compile("[a-zA-Zа-яА-ЯёЁ\\-_]+");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            result.append(input.substring(matcher.start(), matcher.end())).append(", ");
        }
        if (result.length() > 3) {
            result.delete(result.length() - 2, result.length());
        }
        return result.toString();
    }

    private void saveWordToDB(final String origin, final String translation) {
        mThreadHandler.post(() -> {
                ContentValues cv = new ContentValues();
                Cursor cursor = queryFromDbByOrigin(origin);
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    int translIdx = cursor.getColumnIndex(TranslationsDBContract.COLUMN_TRANSLATION);
                    String oldTranslation = cursor.getString(translIdx);
                    String newTranslation = oldTranslation + ", " + translation;
                    int idIdx = cursor.getColumnIndex(TranslationsDBContract._ID);
                    long id = cursor.getLong(idIdx);
                    cv.put(TranslationsDBContract._ID, id);
                    cv.put(TranslationsDBContract.COLUMN_ORIGIN, origin);
                    cv.put(TranslationsDBContract.COLUMN_TRANSLATION, newTranslation);
                    cv.put(TranslationsDBContract.COLUMN_DATE, new Date().getTime());
                    try {
                        mDb.beginTransaction();
                        mDb.replace(TranslationsDBContract.TABLE_NAME, null, cv);
                        mDb.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mDb.endTransaction();
                    }
                } else {
                    cv.put(TranslationsDBContract.COLUMN_ORIGIN, origin);
                    cv.put(TranslationsDBContract.COLUMN_TRANSLATION, translation);
                    cv.put(TranslationsDBContract.COLUMN_DATE, new Date().getTime());
                    try {
                        mDb.beginTransaction();
                        mDb.insert(TranslationsDBContract.TABLE_NAME, null, cv);
                        mDb.setTransactionSuccessful();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mDb.endTransaction();
                    }
                }
                getDbCursor(null);
                });

    }

    private Cursor queryFromDbByOrigin(String origin) {
        return mDb.query(TranslationsDBContract.TABLE_NAME,
                null,
                TranslationsDBContract.COLUMN_ORIGIN + " =?",
                new String[]{origin},
                null, null,null);
    }
}
