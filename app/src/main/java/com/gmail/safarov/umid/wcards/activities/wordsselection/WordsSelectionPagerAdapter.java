package com.gmail.safarov.umid.wcards.activities.wordsselection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.safarov.umid.wcards.R;

import java.util.List;

public class WordsSelectionPagerAdapter extends PagerAdapter implements ActionMode.Callback {

    private List<String> mPages;
    private CallbackListener mCallbackListener;
    private int mCurrentPosition;
    private SparseArray<TextView> mTextViews;
    private boolean mCreationComplete;
    private Context mContext;

    public WordsSelectionPagerAdapter(@NonNull Context context, @NonNull CallbackListener callbackListener) {
        mCallbackListener = callbackListener;
        mTextViews = new SparseArray<>();
        mCreationComplete = false;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPages == null ? 0 : mPages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.adapter_words_selection, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(new SpannableString(mPages.get(position)));
        textView.setCustomSelectionActionModeCallback(this);
        textView.setOnTouchListener(touchListener);
        mTextViews.put(position, textView);
        container.addView(view);

        if (!mCreationComplete) {
            mCreationComplete = true;
            mCallbackListener.onCreationComplete();
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //public interface
    public void replaceData(@NonNull List<String> pages) {
        mPages = pages;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }

    void markTextAsSelected(int startIndex, int endIndex) {
        if (mCreationComplete) {
            ((SpannableString) mTextViews.get(mCurrentPosition).getText()).setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.textColorGray)), startIndex, endIndex, Spannable.SPAN_COMPOSING);
        }
    }

    void markTextAsNeutral(int startIndex, int endIndex) {
        if (mCreationComplete) {
            ForegroundColorSpan[] spans = ((SpannableString) mTextViews.get(mCurrentPosition).getText()).getSpans(startIndex, endIndex, ForegroundColorSpan.class);
            for (ForegroundColorSpan span : spans) {
                ((SpannableString) mTextViews.get(mCurrentPosition).getText()).removeSpan(span);
            }
        }
    }

    // textview tap detector
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        private long mDownTime;
        private float mDownX;
        private float mDownY;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mDownTime = System.currentTimeMillis();
                mDownX = motionEvent.getX();
                mDownY = motionEvent.getY();
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && System.currentTimeMillis() - mDownTime < 200
                    && Math.abs(motionEvent.getX() - mDownX) < 5 && Math.abs(motionEvent.getY() - mDownY) < 5) {
                int charPos = mTextViews.get(mCurrentPosition).getOffsetForPosition(motionEvent.getX(), motionEvent.getY());
                mCallbackListener.onWordAdded(charPos);
            }
            return false;
        }
    };

    // textview selection context menu
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.clear();
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.words_selection_selection_context, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add:
                TextView textView = mTextViews.get(mCurrentPosition);
                mCallbackListener.onWordAdded(textView.getSelectionStart(), textView.getSelectionEnd());
                mode.finish();
                return true;

            case R.id.cancel:
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    //callback
    public interface CallbackListener {

        void onCreationComplete();

        void onWordAdded(int index);

        void onWordAdded(int startIndex, int endIndex);

    }
}
