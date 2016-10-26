package com.gmail.safarov.umid.wcards.activities.translation;

import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.BasePresenter;
import com.gmail.safarov.umid.wcards.BaseView;
import com.gmail.safarov.umid.wcards.lingua.models.LinguaTranslations;

import java.util.List;

public interface TranslationContract {
    interface View extends BaseView<TranslationContract.Presenter> {

        void setWord(@NonNull String word);

        void showTranslations(@NonNull List<LinguaTranslations.LinguaTranslationVariant> translations);

        void setVoiceAvailable(boolean isAvailable);

        void showPreloader();

        void showNoDataLabel();

        void showLoadingError(@NonNull String message);

        void finish();

        void showMessage(@NonNull String message);

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void playVoice();

        void translate(@NonNull String translation);

        void editWord(@NonNull String newName);

        void deleteWord();

        void cancelServerRequests();

    }
}
