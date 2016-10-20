package com.gmail.safarov.umid.wcards.activities.drawing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.components.DrawingView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DrawingFragment extends Fragment implements DrawingContract.View {

    DrawingContract.Presenter mPresenter;
    private Unbinder butterknifeUnbinder;

    MenuItem voiceMenuItem;

    @BindView(R.id.drawing_view)
    DrawingView mDrawingView;
    @BindView(R.id.translation_text)
    TextView mTranslationText;
    @BindView(R.id.draw_hint_text)
    TextView mDrawHintText;
    @BindView(R.id.floating_button)
    FloatingActionButton mFloationButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_drawing, container, false);
        setHasOptionsMenu(true);
        butterknifeUnbinder = ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void setPresenter(@NonNull DrawingContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drawing_menu, menu);
        voiceMenuItem = menu.findItem(R.id.voice);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.voice:
                mPresenter.playVoice();
                return true;
            case R.id.clear:
                mPresenter.clear();
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
    }

    @Override
    public void setWord(@NonNull String word) {
        mDrawHintText.setText(word);
    }

    @Override
    public void setTranslation(@NonNull String translation) {
        mTranslationText.setText(translation);
    }

    @Override
    public void setVoiceAvailable(boolean isAvailable) {
        if (voiceMenuItem != null)
            voiceMenuItem.setVisible(isAvailable);
    }

    /**
     * Clears drawn canvas
     */
    @Override
    public void clearCanvas() {
        mDrawingView.clear();
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

    @OnClick(R.id.floating_button)
    public void floatingButtonClicked() {
        mPresenter.applyBitmap(mDrawingView.getBitmap());
    }
}
