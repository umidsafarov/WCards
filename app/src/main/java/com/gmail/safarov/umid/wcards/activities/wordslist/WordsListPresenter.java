package com.gmail.safarov.umid.wcards.activities.wordslist;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.data.models.Word;
import com.gmail.safarov.umid.wcards.data.source.DataSource;
import com.gmail.safarov.umid.wcards.utils.ClearFilesUtils;

import java.util.ArrayList;
import java.util.List;

public class WordsListPresenter implements WordsListContract.Presenter {

    private Context mContext;
    private long mPackId;
    private DataSource mDataSource;
    private WordsListContract.View mView;

    public WordsListPresenter(@NonNull Context context, long packId, @NonNull WordsListContract.View view, @NonNull DataSource dataSource) {
        mContext = context;
        mPackId = packId;
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        List<Word> words = mDataSource.getWords(mPackId);
        if (words.size() > 0)
            mView.setWords(words);
        else
            mView.showNoDataLabel();
        if (mDataSource.getWordsCount(mPackId, DataSource.WORDS_FLAG_TRANSLATED) < words.size())
            mView.showTranslationButton();
        else
            mView.hideTranslationButton();
    }

    /**
     * Starts creation of new Word
     */
    @Override
    public void newWord() {
        mView.showAddWordUI();
    }

    /**
     * Creates new Word from the given word
     *
     * @param word Word english text
     */
    @Override
    public void createWord(@NonNull String word) {
        word = word.trim().toLowerCase();
        if (!"".equals(word)) {
            int totalWordsCount = mDataSource.getWordsCount(mPackId);
            Word newWord = mDataSource.getWord(mDataSource.createWord(new Word(mPackId, word)));
            if (totalWordsCount == 0) {
                List<Word> words = new ArrayList<>();
                words.add(newWord);
                mView.setWords(words);
            } else
                mView.addWordToList(newWord);
            mView.showTranslationButton();
        }
        mView.hideAddWordUI();
    }

    @Override
    public void deleteWord(@NonNull Word word) {
        ClearFilesUtils.deleteFilesOfWord(mContext, mDataSource, word.getId());
        mDataSource.deleteWord(word.getId());
        mView.removeWordFromList(word);
        int totalWordsCount = mDataSource.getWordsCount(mPackId);
        if (totalWordsCount == 0)
            mView.showNoDataLabel();
        if (mDataSource.getWordsCount(mPackId, DataSource.WORDS_FLAG_TRANSLATED) == totalWordsCount)
            mView.hideTranslationButton();
    }

    @Override
    public void translate() {
        mView.showTranslation(mPackId);
    }

    /**
     * Open TranslationUI
     */
    @Override
    public void translate(@NonNull Word word) {
        if ("".equals(word.getRuText()))
            mView.showTranslation(mPackId, word.getId());
    }
}
