package com.gmail.safarov.umid.wcards.activities.training;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.models.Word;
import com.gmail.safarov.umid.wcards.data.source.DataSource;
import com.gmail.safarov.umid.wcards.utils.ClearFilesUtils;
import com.gmail.safarov.umid.wcards.utils.SoundUtils;

import java.io.File;
import java.util.List;

public class TrainingPresenter implements TrainingContract.Presenter {

    private Context mContext;
    private long mPackId;
    private DataSource mDataSource;
    private TrainingContract.View mView;

    private List<Word> mWords;
    private int mTrainingMode;
    private boolean mIsCardFlipped;

    public TrainingPresenter(@NonNull Context context, int trainingMode, long packId, @NonNull TrainingContract.View view, @NonNull DataSource dataSource) {
        mContext = context;
        mTrainingMode = trainingMode;
        mPackId = packId;
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mWords = mDataSource.getWords(mPackId, DataSource.WORDS_FLAG_TRANSLATED | DataSource.WORDS_FLAG_DRAWN);
        showWord();
    }

    @Override
    public void nextCard() {
        nextWord();
    }

    @Override
    public void flipCard() {
        if (mWords.get(0) != mWords.get(mWords.size() - 1))
            mWords.add(mWords.get(0));
        mIsCardFlipped = !mIsCardFlipped;
        showCardSide();
    }

    @Override
    public void playVoice() {
        if (!"".equals(mWords.get(0).getVoiceFileName())) {
            File externalDirectory = mContext.getExternalFilesDir(null);
            if (externalDirectory != null)
                SoundUtils.getInstance().playSound(externalDirectory.getAbsolutePath().concat("/").concat(mWords.get(0).getVoiceFileName()));
        }
    }

    @Override
    public void redraw() {
        mView.showDrawing(mWords.get(0).getId());
    }

    @Override
    public void deleteWord() {
        ClearFilesUtils.deleteFilesOfWord(mContext, mDataSource, mWords.get(0).getId());
        mDataSource.deleteWord(mWords.get(0).getId());
        nextWord();
        mView.showMessage(mContext.getString(R.string.drawing_message_word_removed));
    }

    private void nextWord() {
        mWords.remove(0);
        showWord();
    }

    private void showWord() {
        if (mWords.size() > 0) {
            mIsCardFlipped = false;
            mView.setVoiceAvailable(!"".equals(mWords.get(0).getVoiceFileName()));
            showCardSide();
        } else
            mView.finish();
    }

    private void showCardSide() {
        if ((mTrainingMode == TrainingActivity.TRAINING_MODE_EN_RU && !mIsCardFlipped) || (mTrainingMode == TrainingActivity.TRAINING_MODE_RU_EN && mIsCardFlipped)) {
            mView.setWord(mWords.get(0).getEnText());
            mView.setCanvasFileName(mContext.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(mWords.get(0).getEnCanvasFileName()));
        } else {
            mView.setWord(mWords.get(0).getRuText());
            mView.setCanvasFileName(mContext.getExternalFilesDir(null).getAbsolutePath().concat("/").concat(mWords.get(0).getRuCanvasFileName()));
        }
    }
}
