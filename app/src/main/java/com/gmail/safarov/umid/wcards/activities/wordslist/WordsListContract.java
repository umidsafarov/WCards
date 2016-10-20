package com.gmail.safarov.umid.wcards.activities.wordslist;

import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.BasePresenter;
import com.gmail.safarov.umid.wcards.BaseView;
import com.gmail.safarov.umid.wcards.data.models.Word;

import java.util.List;

public interface WordsListContract {
    interface View extends BaseView<WordsListContract.Presenter> {

        void showTranslationButton();

        void hideTranslationButton();

        void setWords(@NonNull List<Word> words);

        void addWordToList(@NonNull Word word);

        void removeWordFromList(@NonNull Word word);

        void showNoDataLabel();

        void showAddWordUI();

        void hideAddWordUI();

        void showTranslation(long packId);

        void showTranslation(long packId, long wordId);

    }

    interface Presenter extends BasePresenter {

        void newWord();

        void createWord(@NonNull String word);

        void deleteWord(@NonNull Word word);

        void translate();

        void translate(@NonNull Word word);

    }
}
