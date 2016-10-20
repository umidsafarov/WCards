package com.gmail.safarov.umid.wcards.activities.training;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.activities.drawing.DrawingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TrainingFragment extends Fragment implements TrainingContract.View {

    TrainingContract.Presenter mPresenter;
    private Unbinder butterknifeUnbinder;

    MenuItem voiceMenuItem;

    @BindView(R.id.image)
    ImageView mImageView;
    @BindView(R.id.word_text)
    TextView mWordText;

    private GestureDetectorCompat mDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);
        setHasOptionsMenu(true);
        butterknifeUnbinder = ButterKnife.bind(this, root);

        mImageView.setOnTouchListener(touchListener);
        mDetector = new GestureDetectorCompat(getContext(), gestureListener);

        return root;
    }

    @Override
    public void setPresenter(@NonNull TrainingContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.training_menu, menu);
        voiceMenuItem = menu.findItem(R.id.voice);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.voice:
                mPresenter.playVoice();
                return true;
            case R.id.redraw:
                mPresenter.redraw();
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
    public void setVoiceAvailable(boolean isAvailable) {
        if (voiceMenuItem != null)
            voiceMenuItem.setVisible(isAvailable);
    }

    @Override
    public void setWord(String word) {
        mWordText.setText(word);
    }

    /**
     * Loads card image from the file
     */
    @Override
    public void setCanvasFileName(@NonNull String filePath) {
        mImageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }

    @Override
    public void showDrawing(long wordId) {
        Intent intent = new Intent(getContext(), DrawingActivity.class);
        intent.putExtra(DrawingActivity.EXTRA_WORD_ID, wordId);
        startActivity(intent);
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

    /*
        Detect user gestures
     */
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            mPresenter.nextCard();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mPresenter.flipCard();
            return true;
        }
    };


}
