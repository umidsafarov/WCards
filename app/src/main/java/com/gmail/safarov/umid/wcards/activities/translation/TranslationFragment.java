package com.gmail.safarov.umid.wcards.activities.translation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.lingua.models.LinguaTranslations;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TranslationFragment extends Fragment implements TranslationContract.View {

    TranslationContract.Presenter mPresenter;
    private Unbinder butterknifeUnbinder;
    private ArrayAdapter<LinguaTranslations.LinguaTranslationVariant> mListAdapter;

    MenuItem voiceMenuItem;

    @BindView(R.id.edit_word_text)
    EditText mEditWordText;
    @BindView(R.id.edit_word_button)
    ImageButton mEditWordButton;

    @BindView(R.id.list)
    ListView mList;
    @BindView(R.id.preloader)
    ProgressBar mPreloader;
    @BindView(R.id.message_text)
    TextView mMessageText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_translation, container, false);
        setHasOptionsMenu(true);
        butterknifeUnbinder = ButterKnife.bind(this, root);

        mListAdapter = new ArrayAdapter<>(getContext(),
                R.layout.adapter_translation, new ArrayList<LinguaTranslations.LinguaTranslationVariant>() {
        });
        mList.setAdapter(mListAdapter);
        mList.setOnItemClickListener(listItemClickListener);


        return root;
    }

    @Override
    public void setPresenter(@NonNull TranslationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.translation_menu, menu);
        voiceMenuItem = menu.findItem(R.id.voice);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.voice:
                mPresenter.playVoice();
                return true;
            case R.id.delete:
                mPresenter.deleteWord();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
        mPresenter.cancelServerRequests();
    }

    @Override
    public void setWord(@NonNull String word) {
        mEditWordText.setText(word);
        mListAdapter.clear();
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showTranslations(@NonNull List<LinguaTranslations.LinguaTranslationVariant> translations) {
        mMessageText.setVisibility(View.GONE);
        mList.setVisibility(View.VISIBLE);
        mPreloader.setVisibility(View.GONE);

        mListAdapter.addAll(translations);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setVoiceAvailable(boolean isAvailable) {
        if (voiceMenuItem != null)
            voiceMenuItem.setVisible(isAvailable);
    }

    @Override
    public void showNoDataLabel() {
        mMessageText.setText(R.string.translation_list_no_data);
        mMessageText.setVisibility(View.VISIBLE);
        mList.setVisibility(View.GONE);
        mPreloader.setVisibility(View.GONE);
    }

    @Override
    public void showPreloader() {
        mMessageText.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        mPreloader.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingError(@NonNull String message) {
        mMessageText.setText(message);
        mMessageText.setVisibility(View.VISIBLE);
        mList.setVisibility(View.GONE);
        mPreloader.setVisibility(View.GONE);
    }

    /**
     * Shows Toast
     */
    @Override
    public void showMessage(@NonNull String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    /**
     * Runs task on UIThread. Does nothing if activity already destroyed
     */
    @Override
    public void runOnUIThread(@NonNull Runnable runnable) {
        if (getActivity() != null)
            getActivity().runOnUiThread(runnable);
    }

    @OnClick(R.id.edit_word_button)
    public void onEditWordButtonClicked() {
        if (mEditWordText.isFocusable()) {
            //edit complete
            mPresenter.cancelServerRequests();
            mPresenter.editWord(mEditWordText.getText().toString());
            mEditWordText.setClickable(false);
            mEditWordText.setCursorVisible(false);
            mEditWordText.setFocusable(false);
            mEditWordText.setFocusableInTouchMode(false);
            mEditWordButton.setImageResource(R.drawable.ic_edit);
        } else {
            //start edit
            mEditWordText.setClickable(true);
            mEditWordText.setCursorVisible(true);
            mEditWordText.setFocusable(true);
            mEditWordText.setFocusableInTouchMode(true);
            mEditWordText.selectAll();
            mEditWordText.requestFocus();
            mEditWordButton.setImageResource(R.drawable.ic_done);
        }
    }

    AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mListAdapter.getItem(position) != null)
                mPresenter.translate(mListAdapter.getItem(position).toString());
        }
    };
}
