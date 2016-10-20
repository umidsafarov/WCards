package com.gmail.safarov.umid.wcards.activities.training;

import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.BasePresenter;
import com.gmail.safarov.umid.wcards.BaseView;

public interface TrainingContract {
    interface View extends BaseView<TrainingContract.Presenter> {

        void setVoiceAvailable(boolean isAvailable);

        void setWord(@NonNull String word);

        void setCanvasFileName(@NonNull String filePath);

        void showDrawing(long wordId);

        void showMessage(@NonNull String message);

        void finish();

    }

    interface Presenter extends BasePresenter {

        void nextCard();

        void flipCard();

        void playVoice();

        void redraw();

        void deleteWord();

    }
}
