package com.gmail.safarov.umid.wcards.activities.drawing;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.BasePresenter;
import com.gmail.safarov.umid.wcards.BaseView;

public interface DrawingContract {
    interface View extends BaseView<DrawingContract.Presenter> {

        void setWord(@NonNull String word);

        void setTranslation(@NonNull String translation);

        void setVoiceAvailable(boolean isAvailable);

        void clearCanvas();

        void showMessage(@NonNull String message);

        void finish();

    }

    interface Presenter extends BasePresenter {

        void applyBitmap(@NonNull Bitmap bitmap);

        void playVoice();

        void deleteWord();

        void clear();

    }
}
