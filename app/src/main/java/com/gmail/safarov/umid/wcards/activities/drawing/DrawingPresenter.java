package com.gmail.safarov.umid.wcards.activities.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.models.Word;
import com.gmail.safarov.umid.wcards.data.source.DataSource;
import com.gmail.safarov.umid.wcards.utils.ClearFilesUtils;
import com.gmail.safarov.umid.wcards.utils.SoundUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawingPresenter implements DrawingContract.Presenter {

    private Context mContext;
    private long mPackId;
    private long mWordId;
    private DataSource mDataSource;
    private DrawingContract.View mView;

    private List<Word> mWords;
    //save card front side bitmap to apply it when booth sides would be drawn
    private Bitmap mStoredRuBitmap;

    public DrawingPresenter(@NonNull Context context, long packId, long wordId, @NonNull DrawingContract.View view, @NonNull DataSource dataSource) {
        mContext = context;
        mPackId = packId;
        mWordId = wordId;
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mWordId > 0) {
            mWords = new ArrayList<>();
            mWords.add(mDataSource.getWord(mWordId));
        } else
            mWords = mDataSource.getWords(mPackId, DataSource.WORDS_FLAG_TRANSLATED | DataSource.WORDS_FLAG_UNDRAWN);
        showWord();
    }

    /**
     * Saves bitmap to PNG file and applies this file to database row of current Word. After, shows next word. Runs on UIThreads
     */
    @Override
    public void applyBitmap(@NonNull Bitmap bitmap) {
        if (mStoredRuBitmap == null) {
            mStoredRuBitmap = bitmap;

            mView.clearCanvas();
            mView.setWord(mWords.get(0).getEnText());
            mView.setTranslation(mWords.get(0).getRuText());

        } else {

            String ruFileName = String.valueOf(mWords.get(0).getId()).concat("_ru.png");
            String enFileName = String.valueOf(mWords.get(0).getId()).concat("_en.png");

            try {
                File imageFile = new File(mContext.getExternalFilesDir(null), ruFileName);
                FileOutputStream out = new FileOutputStream(imageFile);
                mStoredRuBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();

                imageFile = new File(mContext.getExternalFilesDir(null), enFileName);
                out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                mView.showMessage(e.getMessage());
                return;
            }

            mDataSource.setWordRuCanvasFileName(mWords.get(0).getId(), ruFileName);
            mDataSource.setWordEnCanvasFileName(mWords.get(0).getId(), enFileName);
            mStoredRuBitmap = null;
            nextWord();
        }
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
    public void deleteWord() {
        ClearFilesUtils.deleteFilesOfWord(mContext, mDataSource, mWords.get(0).getId());
        mDataSource.deleteWord(mWords.get(0).getId());
        nextWord();
        mView.showMessage(mContext.getString(R.string.drawing_message_word_removed));
    }

    @Override
    public void clear() {
        mView.clearCanvas();
    }

    private void nextWord() {
        mWords.remove(0);
        showWord();
    }

    private void showWord() {
        if (mWords.size() > 0) {
            mStoredRuBitmap = null;
            mView.clearCanvas();
            mView.setWord(mWords.get(0).getRuText());
            mView.setTranslation(mWords.get(0).getEnText());
            mView.setVoiceAvailable(!"".equals(mWords.get(0).getVoiceFileName()));
        } else
            mView.finish();
    }
}
