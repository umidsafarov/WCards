package com.gmail.safarov.umid.wcards.activities.wordsselection;

import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.BasePresenter;
import com.gmail.safarov.umid.wcards.BaseView;

import java.util.List;

public interface WordsSelectionContract {
    interface View extends BaseView<WordsSelectionContract.Presenter> {

        void setPageNum(int num, int total);

        void setTextPages(@NonNull List<String> pages);

        void setPageIndex(int index);

        void markTextAsSelected(int startIndex, int endIndex);

        void markTextAsNeutral(int startIndex, int endIndex);

    }

    interface Presenter extends BasePresenter {

        void markWords();

        void toggleWordAtPosition(int position);

        void addWordAtPosition(int start, int end);

        void pageChanged(int pageIndex);

    }
}
