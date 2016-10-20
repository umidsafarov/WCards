package com.gmail.safarov.umid.wcards.activities.wordsselection;

import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.data.models.Pack;
import com.gmail.safarov.umid.wcards.data.models.Word;
import com.gmail.safarov.umid.wcards.data.source.DataSource;

import java.util.List;

public class WordsSelectionPresenter implements WordsSelectionContract.Presenter {

    private long mPackId;
    private DataSource mDataSource;
    private WordsSelectionContract.View mView;
    private Pack mPack;
    private int mPageIndex;

    public WordsSelectionPresenter(long packId, WordsSelectionContract.View view, DataSource dataSource) {
        mPackId = packId;
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mPack = mDataSource.getPack(mPackId);
        mView.setTextPages(mPack.getTextPages());
        mView.setPageNum(mPack.getPageIndex() + 1, mPack.getPagesTotal());
        mView.setPageIndex(mPack.getPageIndex());
        // set whole text read if there is only one page
        if (mPack.getPagesTotal() == 1)
            mDataSource.setPackPageIndex(mPackId, 0);
    }

    /**
     * Adds a word at position to Pack if it does not exist. And remove if exist.
     */
    @Override
    public void toggleWordAtPosition(int position) {
        String page = mPack.getTextPages().get(mPageIndex).toLowerCase();
        if (position > page.length() || !Character.isLetter(page.charAt(position)))
            return;

        int i = position;
        int wordStart = -1;
        int wordEnd = -1;

        while (true) {
            i++;
            if (i >= page.length() || (!Character.isLetter(page.charAt(i)) && page.charAt(i) != '-')) {
                wordEnd = i;
                break;
            }
        }
        i = position;
        while (true) {
            i--;
            if (i < 0 || (!Character.isLetter(page.charAt(i)) && page.charAt(i) != '-')) {
                wordStart = i + 1;
                break;
            }
        }

        try {
            if (wordStart < wordEnd) {
                String word = page.substring(wordStart, wordEnd).trim().toLowerCase();
                if (mDataSource.deleteWord(mPackId, word)) {
                    unmarkWord(page, word);
                } else {
                    mDataSource.createWord(new Word(mPackId, word));
                    markWord(page, word);
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
        }
    }

    /**
     * Adds a word or multiple words to Pack
     */
    @Override
    public void addWordAtPosition(int start, int end) {
        String page = mPack.getTextPages().get(mPageIndex);
        String word = page.substring(start, end).trim().toLowerCase();
        if (!mDataSource.isWordExist(mPackId, word)) {
            mDataSource.createWord(new Word(mPackId, word));
            markWord(page, word);
        }
    }

    @Override
    public void pageChanged(int pageIndex) {
        mPageIndex = pageIndex;
        mDataSource.setPackPageIndex(mPackId, pageIndex);
        // set whole text read if it is the last page
        if (pageIndex == mPack.getPagesTotal())
            mView.setPageNum(mPack.getPagesTotal(), mPack.getPagesTotal());
        else
            mView.setPageNum(pageIndex, mPack.getPagesTotal());
        markWords();
    }

    /**
     * Marks all existing words on the current text page
     */
    @Override
    public void markWords() {
        String text = mPack.getTextPages().get(mPageIndex).toLowerCase();
        List<Word> words = mDataSource.getWords(mPackId);
        for (Word word : words) {
            markWord(text, word.getEnText());
        }
    }

    private void markWord(@NonNull String text, @NonNull String word) {
        int index = text.indexOf(word, 0);
        while (index != -1) {
            mView.markTextAsSelected(index, index + word.length());
            index = text.indexOf(word, index + word.length());
        }
    }

    private void unmarkWord(@NonNull String text, @NonNull String word) {
        int index = text.indexOf(word, 0);
        while (index != -1) {
            mView.markTextAsNeutral(index, index + word.length());
            index = text.indexOf(word, index + word.length());
        }
    }
}
