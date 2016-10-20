package com.gmail.safarov.umid.wcards.activities.pack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.activities.drawing.DrawingActivity;
import com.gmail.safarov.umid.wcards.activities.training.TrainingActivity;
import com.gmail.safarov.umid.wcards.activities.wordslist.WordsListActivity;
import com.gmail.safarov.umid.wcards.activities.wordsselection.WordsSelectionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PackFragment extends Fragment implements PackContract.View {

    PackContract.Presenter mPresenter;
    private Unbinder butterknifeUnbinder;

    @BindView(R.id.edit_name_text)
    EditText mEditNameText;
    @BindView(R.id.edit_name_button)
    ImageButton mEditNameButton;

    @BindView(R.id.words_selection_card)
    CardView mWordsSelectionCard;
    @BindView(R.id.words_list_card)
    CardView mWordsListCard;
    @BindView(R.id.cards_draw_card)
    CardView mCardsDrawingCard;
    @BindView(R.id.training_en_ru_card)
    CardView mTrainingEnRuCard;
    @BindView(R.id.training_ru_en_card)
    CardView mTrainingRuEnCard;

    @BindView(R.id.words_selection_text)
    TextView mWordsSelectionText;
    @BindView(R.id.words_list_text)
    TextView mWordsListText;
    @BindView(R.id.cards_draw_text)
    TextView mCardsDrawText;
    @BindView(R.id.training_en_ru_text)
    TextView mTrainingEnRuText;
    @BindView(R.id.training_ru_en_text)
    TextView mTrainingRuEnText;

    @BindView(R.id.words_selection_counter)
    TextView mWordsSelectionCounterText;
    @BindView(R.id.words_list_counter)
    TextView mWordsListCounterText;
    @BindView(R.id.cards_draw_counter)
    TextView mCardsDrawCounterText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pack, container, false);
        setHasOptionsMenu(true);
        butterknifeUnbinder = ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void setPresenter(@NonNull PackContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pack_menu, menu);
        if (mPresenter.getIsComplete())
            menu.findItem(R.id.complete).setVisible(false);
        else
            menu.findItem(R.id.activate).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.complete:
            case R.id.activate:
                mPresenter.toggleIsComplete();
                return true;
            case R.id.delete:
                mPresenter.delete();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @Override
    public void setName(@NonNull String name) {
        mEditNameText.setText(name);
    }

    @Override
    public void setTextPage(int num, int total) {
        mWordsSelectionCounterText.setText(String.format(getString(R.string.counter_pages), num, total));
    }

    @Override
    public void setWordsListCount(int num, int total) {
        mWordsListCounterText.setText(String.format(getString(R.string.counter_translations), num, total));
    }

    @Override
    public void setCardsDrawnCount(int num, int total) {
        mCardsDrawCounterText.setText(String.format(getString(R.string.counter_cards), num, total));
    }

    @Override
    public void setWordsSelectionIsCompleted(boolean isCompleted) {
        if (isCompleted) {
            mWordsSelectionCounterText.setVisibility(View.GONE);
            collapseCard(mWordsSelectionCard, mWordsSelectionText);
        } else {
            mWordsSelectionCounterText.setVisibility(View.VISIBLE);
            expandCard(mWordsSelectionCard, mWordsSelectionText);
        }
    }

    @Override
    public void setWordsListIsCompleted(boolean isCompleted) {
        if (isCompleted) {
            mWordsListCounterText.setVisibility(View.GONE);
            collapseCard(mWordsListCard, mWordsListText);
        } else {
            mWordsListCounterText.setVisibility(View.VISIBLE);
            expandCard(mWordsListCard, mWordsListText);
        }
    }

    @Override
    public void setCardsDrawIsCompleted(boolean isCompleted) {
        if (isCompleted) {
            mCardsDrawCounterText.setVisibility(View.GONE);
            collapseCard(mCardsDrawingCard, mCardsDrawText);
        } else {
            mCardsDrawCounterText.setVisibility(View.VISIBLE);
            expandCard(mCardsDrawingCard, mCardsDrawText);
        }
    }

    @Override
    public void setTrainingIsAvailable(boolean isAvailable) {
        if (isAvailable) {
            expandCard(mTrainingEnRuCard, mTrainingEnRuText);
            expandCard(mTrainingRuEnCard, mTrainingRuEnText);
        } else {
            collapseCard(mTrainingEnRuCard, mTrainingEnRuText);
            collapseCard(mTrainingRuEnCard, mTrainingRuEnText);
        }
    }

    @Override
    public void showWordsSelection(long packId) {
        Intent intent = new Intent(getContext(), WordsSelectionActivity.class);
        intent.putExtra(WordsSelectionActivity.EXTRA_PACK_ID, packId);
        startActivity(intent);
    }

    @Override
    public void showWordsList(long packId) {
        Intent intent = new Intent(getContext(), WordsListActivity.class);
        intent.putExtra(WordsListActivity.EXTRA_PACK_ID, packId);
        startActivity(intent);
    }

    @Override
    public void showCardsDraw(long packId) {
        Intent intent = new Intent(getContext(), DrawingActivity.class);
        intent.putExtra(DrawingActivity.EXTRA_PACK_ID, packId);
        startActivity(intent);
    }

    @Override
    public void showTraning(long packId, int trainingMode) {
        Intent intent = new Intent(getContext(), TrainingActivity.class);
        intent.putExtra(TrainingActivity.EXTRA_PACK_ID, packId);
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_MODE, trainingMode);
        startActivity(intent);
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void showMessage(@StringRes int messageResId) {
        Toast.makeText(getActivity(), messageResId, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.edit_name_button)
    public void onEditNameButtonClicked() {
        if (mEditNameText.isFocusable()) {
            //edit complete
            mPresenter.editName(mEditNameText.getText().toString());
            mEditNameText.setClickable(false);
            mEditNameText.setCursorVisible(false);
            mEditNameText.setFocusable(false);
            mEditNameText.setFocusableInTouchMode(false);
            mEditNameButton.setImageResource(R.drawable.ic_edit);
        } else {
            //start edit
            mEditNameText.setClickable(true);
            mEditNameText.setCursorVisible(true);
            mEditNameText.setFocusable(true);
            mEditNameText.setFocusableInTouchMode(true);
            mEditNameText.selectAll();
            mEditNameText.requestFocus();
            mEditNameButton.setImageResource(R.drawable.ic_done);
        }
    }

    @OnClick(R.id.words_selection_card)
    public void onWordsSelectionCardClicked() {
        mPresenter.openWordsSelection();
    }

    @OnClick(R.id.words_list_card)
    public void onWordsListCardClicked() {
        mPresenter.openWordsList();
    }

    @OnClick(R.id.cards_draw_card)
    public void onCardsDrawCardClicked() {
        mPresenter.openCardsDraw();
    }

    @OnClick(R.id.training_en_ru_card)
    public void onTrainingEnRuCardClicked() {
        mPresenter.openTraining(TrainingActivity.TRAINING_MODE_EN_RU);
    }

    @OnClick(R.id.training_ru_en_card)
    public void onTrainingRuEnCardClicked() {
        mPresenter.openTraining(TrainingActivity.TRAINING_MODE_RU_EN);
    }

    private void collapseCard(@NonNull CardView cardView, @NonNull TextView textView) {
        //make card ui as inactive. but allow user to tap it
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorGray));
        ViewGroup.LayoutParams lp = cardView.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        cardView.setLayoutParams(lp);
    }

    private void expandCard(@NonNull CardView cardView, @NonNull TextView textView) {
        //make card ui as active
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimary));
        ViewGroup.LayoutParams lp = cardView.getLayoutParams();
        lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, getResources().getDisplayMetrics());
        cardView.setLayoutParams(lp);
    }
}
