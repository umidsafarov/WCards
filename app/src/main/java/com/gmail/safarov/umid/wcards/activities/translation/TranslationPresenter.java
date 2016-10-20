package com.gmail.safarov.umid.wcards.activities.translation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.models.Word;
import com.gmail.safarov.umid.wcards.data.source.DataSource;
import com.gmail.safarov.umid.wcards.lingua.LinguaAPI;
import com.gmail.safarov.umid.wcards.lingua.LinguaCallbackListener;
import com.gmail.safarov.umid.wcards.lingua.models.LinguaTranslations;
import com.gmail.safarov.umid.wcards.utils.ClearFilesUtils;
import com.gmail.safarov.umid.wcards.utils.SoundUtils;

import java.io.File;
import java.util.List;

import retrofit2.Call;

public class TranslationPresenter implements TranslationContract.Presenter {

    private Context mContext;
    private long mPackId;
    private long mStartWordId;
    private DataSource mDataSource;
    private TranslationContract.View mView;

    private List<Word> mWords;

    public TranslationPresenter(Context context, long packId, long wordId, TranslationContract.View view, DataSource dataSource) {
        mContext = context;
        mPackId = packId;
        mStartWordId = wordId;
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mWords = mDataSource.getWords(mPackId, DataSource.WORDS_FLAG_UNTRANSLATED);
        // if startWordId passed - make this word first in order
        if (mStartWordId > 0) {
            for (int i = 0; i < mWords.size(); i++) {
                if (mWords.get(i).getId() == mStartWordId) {
                    mWords.add(0, mWords.remove(i));
                    break;
                }
            }
        }
        showWord();
    }

    @Override
    public void playVoice() {
        if (!"".equals(mWords.get(0).getVoiceFileName())) {
            File externalDirectory = mContext.getExternalFilesDir(null);
            if (externalDirectory != null)
                SoundUtils.getInstance().playSound(externalDirectory.getAbsolutePath().concat("/").concat(mWords.get(0).getVoiceFileName()));
        }
    }

    /**
     * Sets Word russian translation text and shows the next word
     */
    @Override
    public void translate(@NonNull String translation) {
        mDataSource.setWordRuText(mWords.get(0).getId(), translation);
        nextWord();
    }

    @Override
    public void editWord(@NonNull String newName) {
        mDataSource.replaceWord(mWords.get(0).getId(), newName);
        mWords.set(0, mDataSource.getWord(mWords.get(0).getId()));
        showWord();
    }

    @Override
    public void deleteWord() {
        ClearFilesUtils.deleteFilesOfWord(mContext, mDataSource, mWords.get(0).getId());
        mDataSource.deleteWord(mWords.get(0).getId());
        nextWord();
        mView.showMessage(mContext.getString(R.string.translation_word_removed));
    }

    private void nextWord() {
        mWords.remove(0);
        showWord();
    }

    private void showWord() {
        if (mWords.size() > 0) {
            mView.setWord(mWords.get(0).getEnText());
            mView.setVoiceAvailable(false);
            mView.showPreloader();
            mTranslationsResponse = null;
            downloadTranslations();
        } else
            mView.finish();
    }

    @Override
    public void cancelServerRequests() {
        if (mCurrentLinguaCall != null)
            mCurrentLinguaCall.cancel();
    }

    //server interaction
    private Call mCurrentLinguaCall;
    private LinguaTranslations mTranslationsResponse;

    private void downloadTranslations() {
        if (mCurrentLinguaCall != null)
            mCurrentLinguaCall.cancel();
        mCurrentLinguaCall = LinguaAPI.getInstance().translate(mWords.get(0).getEnText(), translationsDownloadCallbackListener);
    }

    private LinguaCallbackListener<LinguaTranslations> translationsDownloadCallbackListener = new LinguaCallbackListener<LinguaTranslations>() {
        @Override
        public void onResponse(@NonNull LinguaTranslations response) {
            mCurrentLinguaCall = null;
            mTranslationsResponse = response;
            if (!"".equals(mWords.get(0).getVoiceFileName())) {
                downloadComplete();
            } else if ("".equals(response.getVoiceUrl()))
                downloadComplete();
            else
                downloadVoice(response.getVoiceUrl());
        }

        @Override
        public void onError(@NonNull final String errorMessage) {
            mCurrentLinguaCall = null;
            mView.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mView.showLoadingError(errorMessage);
                }
            });
        }
    };


    private void downloadVoice(@NonNull String url) {
        if (mCurrentLinguaCall != null)
            mCurrentLinguaCall.cancel();
        mCurrentLinguaCall = LinguaAPI.getInstance().downloadVoice(url, new File(mContext.getExternalFilesDir(null), String.valueOf(mWords.get(0).getId()).concat(".mp3")), voiceDownloadCallbackListener);
    }

    private LinguaCallbackListener<File> voiceDownloadCallbackListener = new LinguaCallbackListener<File>() {
        @Override
        public void onResponse(@NonNull File response) {
            mCurrentLinguaCall = null;
            mDataSource.setWordVoiceFileName(mWords.get(0).getId(), response.getName());
            mWords.set(0, mDataSource.getWord(mWords.get(0).getId()));
            downloadComplete();
        }

        @Override
        public void onError(@NonNull final String errorMessage) {
            mCurrentLinguaCall = null;
            mView.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mView.showLoadingError(errorMessage);
                }
            });
        }
    };

    private void downloadComplete() {
        mView.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mView.setVoiceAvailable(!"".equals(mWords.get(0).getVoiceFileName()));
                if (mTranslationsResponse.getVariants().size() > 0)
                    mView.showTranslations(mTranslationsResponse.getVariants());
                else
                    mView.showNoDataLabel();
            }
        });
    }
}
