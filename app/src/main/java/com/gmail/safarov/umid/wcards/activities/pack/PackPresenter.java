package com.gmail.safarov.umid.wcards.activities.pack;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.models.Pack;
import com.gmail.safarov.umid.wcards.data.source.DataSource;
import com.gmail.safarov.umid.wcards.utils.ClearFilesUtils;

public class PackPresenter implements PackContract.Presenter {

    private Context mContext;
    private long mPackId;
    private DataSource mDataSource;
    private PackContract.View mView;

    private Pack mPack;

    public PackPresenter(@NonNull Context context, long packId, @NonNull PackContract.View view, @NonNull DataSource dataSource) {
        mContext = context;
        mDataSource = dataSource;
        mPackId = packId;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        int totalWordsCount = mDataSource.getWordsCount(mPackId);
        int translatedWordsCount = mDataSource.getWordsCount(mPackId, DataSource.WORDS_FLAG_TRANSLATED);
        int drawnCardsCount = mDataSource.getWordsCount(mPackId, DataSource.WORDS_FLAG_DRAWN);

        mPack = mDataSource.getPack(mPackId);
        mView.setName(mPack.getName());
        mView.setTextPage(mPack.getPageIndex() + 1, mPack.getPagesTotal());
        mView.setWordsListCount(translatedWordsCount, totalWordsCount);
        mView.setCardsDrawnCount(drawnCardsCount, translatedWordsCount);

        mView.setWordsSelectionIsCompleted(mPack.getPageIndex() == mPack.getPagesTotal() - 1);
        mView.setWordsListIsCompleted(translatedWordsCount == totalWordsCount);
        mView.setCardsDrawIsCompleted(drawnCardsCount == translatedWordsCount);
        mView.setTrainingIsAvailable(drawnCardsCount > 0);
    }

    @Override
    public void editName(@NonNull String newName) {
        mDataSource.setPackName(mPackId, newName);
    }

    @Override
    public void openWordsSelection() {
        mView.showWordsSelection(mPackId);
    }

    @Override
    public void openWordsList() {
        mView.showWordsList(mPackId);
    }

    @Override
    public void openCardsDraw() {
        if (mDataSource.getWordsCount(mPackId, DataSource.WORDS_FLAG_TRANSLATED | DataSource.WORDS_FLAG_UNDRAWN) == 0)
            mView.showMessage(R.string.pack_message_no_cards_to_draw);
        else
            mView.showCardsDraw(mPackId);
    }

    @Override
    public void openTraining(int trainingMode) {
        if (mDataSource.getWordsCount(mPackId, DataSource.WORDS_FLAG_DRAWN) == 0)
            mView.showMessage(R.string.pack_message_no_cards_to_train);
        else
            mView.showTraning(mPackId, trainingMode);
    }

    @Override
    public boolean getIsComplete() {
        return mPack.isComplete();
    }

    /**
     * Marks Pack as completed if it is active. And as active if it is completed
     */
    @Override
    public void toggleIsComplete() {
        mDataSource.setPackIsComplete(mPackId, !mPack.isComplete());
        mView.finish();
    }

    @Override
    public void delete() {
        ClearFilesUtils.deleteFilesOfPack(mContext, mDataSource, mPackId);
        mDataSource.deletePack(mPackId);
        mView.finish();
    }
}
