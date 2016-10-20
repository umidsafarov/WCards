package com.gmail.safarov.umid.wcards.activities.wordslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.activities.translation.TranslationActivity;
import com.gmail.safarov.umid.wcards.data.models.Word;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WordsListFragment extends Fragment implements WordsListContract.View {

    WordsListContract.Presenter mPresenter;
    private Unbinder butterknifeUnbinder;
    private WordsListAdapter mWordsListAdapter;

    @BindView(R.id.translate_button)
    Button mTranslateButton;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.floating_button)
    FloatingActionButton mFloatingButton;
    @BindView(R.id.no_data_text)
    TextView mNoDataText;
    @BindView(R.id.new_word_text)
    TextView mNewWordText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_words_list, container, false);
        setHasOptionsMenu(true);
        butterknifeUnbinder = ButterKnife.bind(this, root);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mWordsListAdapter = new WordsListAdapter(adapterCallback);
        mRecyclerView.setAdapter(mWordsListAdapter);

        return root;
    }

    @Override
    public void setPresenter(@NonNull WordsListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @Override
    public void showTranslationButton() {
        mTranslateButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTranslationButton() {
        mTranslateButton.setVisibility(View.GONE);
    }

    @Override
    public void setWords(@NonNull List<Word> words) {
        mNoDataText.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mWordsListAdapter.replaceData(words);
    }

    @Override
    public void addWordToList(@NonNull Word word) {
        mWordsListAdapter.addItem(word);
    }

    @Override
    public void removeWordFromList(@NonNull Word word) {
        mWordsListAdapter.removeItem(word);
    }

    @Override
    public void showNoDataLabel() {
        mNoDataText.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showAddWordUI() {
        mFloatingButton.setImageResource(R.drawable.ic_done);
        mNewWordText.setVisibility(View.VISIBLE);
        mNewWordText.requestFocus();
    }

    @Override
    public void hideAddWordUI() {
        mFloatingButton.setImageResource(R.drawable.ic_new);
        mNewWordText.setVisibility(View.GONE);
        mNewWordText.setText("");
    }

    @Override
    public void showTranslation(long packId) {
        Intent intent = new Intent(getContext(), TranslationActivity.class);
        intent.putExtra(TranslationActivity.EXTRA_PACK_ID, packId);
        startActivity(intent);
    }

    @Override
    public void showTranslation(long packId, long wordId) {
        Intent intent = new Intent(getContext(), TranslationActivity.class);
        intent.putExtra(TranslationActivity.EXTRA_PACK_ID, packId);
        intent.putExtra(TranslationActivity.EXTRA_WORD_ID, wordId);
        startActivity(intent);
    }

    @OnClick(R.id.translate_button)
    public void translateButtonClicked() {
        mPresenter.translate();
    }

    @OnClick(R.id.floating_button)
    public void floatingButtonClicked() {
        if (mNewWordText.getVisibility() == View.GONE) {
            mPresenter.newWord();
        } else {
            mPresenter.createWord(mNewWordText.getText().toString());
        }
    }

    private WordsListAdapter.CallbackListener adapterCallback = new WordsListAdapter.CallbackListener() {
        @Override
        public void onOpen(Word word) {
            mPresenter.translate(word);
        }

        @Override
        public void onDelete(Word word) {
            mPresenter.deleteWord(word);
        }
    };
}

