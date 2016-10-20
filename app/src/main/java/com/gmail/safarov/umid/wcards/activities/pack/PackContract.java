package com.gmail.safarov.umid.wcards.activities.pack;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.gmail.safarov.umid.wcards.BasePresenter;
import com.gmail.safarov.umid.wcards.BaseView;

public interface PackContract {
    interface View extends BaseView<PackContract.Presenter> {

        void setName(@NonNull String name);

        void setTextPage(int num, int total);

        void setWordsListCount(int num, int total);

        void setCardsDrawnCount(int num, int total);

        void setWordsSelectionIsCompleted(boolean isCompleted);

        void setWordsListIsCompleted(boolean isCompleted);

        void setCardsDrawIsCompleted(boolean isCompleted);

        void setTrainingIsAvailable(boolean isAvailable);

        void showWordsSelection(long packId);

        void showWordsList(long packId);

        void showCardsDraw(long packId);

        void showTraning(long packId, int trainingMode);

        void finish();

        void showMessage(@StringRes int messageResId);

    }

    interface Presenter extends BasePresenter {

        void editName(@NonNull String newName);

        void openWordsSelection();

        void openWordsList();

        void openCardsDraw();

        void openTraining(int trainingMode);

        boolean getIsComplete();

        void toggleIsComplete();

        void delete();

    }
}
