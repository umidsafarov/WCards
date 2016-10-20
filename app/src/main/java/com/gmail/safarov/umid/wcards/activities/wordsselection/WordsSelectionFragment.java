package com.gmail.safarov.umid.wcards.activities.wordsselection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.safarov.umid.wcards.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WordsSelectionFragment extends Fragment implements WordsSelectionContract.View {

    private WordsSelectionContract.Presenter mPresenter;
    private Unbinder butterknifeUnbinder;
    private WordsSelectionPagerAdapter mPagesAdapter;

    @BindView(R.id.counter_text)
    TextView counterText;
    @BindView(R.id.pager)
    ViewPager mPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_words_selection, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, root);

        mPagesAdapter = new WordsSelectionPagerAdapter(getContext(), pagerAdapterCallback);
        mPager.setAdapter(mPagesAdapter);
        mPager.addOnPageChangeListener(pagerChangeListener);

        return root;
    }

    @Override
    public void setPresenter(@NonNull WordsSelectionContract.Presenter presenter) {
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
    public void setPageNum(int num, int total) {
        counterText.setText(String.format(getString(R.string.counter_pages), num, total));
    }

    @Override
    public void setTextPages(@NonNull List<String> pages) {
        mPagesAdapter.replaceData(pages);
    }

    @Override
    public void setPageIndex(int index) {
        mPager.setCurrentItem(index);
    }

    @Override
    public void markTextAsSelected(int startIndex, int endIndex) {
        mPagesAdapter.markTextAsSelected(startIndex, endIndex);
    }

    @Override
    public void markTextAsNeutral(int startIndex, int endIndex) {
        mPagesAdapter.markTextAsNeutral(startIndex, endIndex);
    }

    ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mPagesAdapter.setCurrentPosition(position);
            mPresenter.pageChanged(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    WordsSelectionPagerAdapter.CallbackListener pagerAdapterCallback = new WordsSelectionPagerAdapter.CallbackListener() {
        @Override
        public void onCreationComplete() {
            mPresenter.markWords();
        }

        @Override
        public void onWordAdded(int index) {
            mPresenter.toggleWordAtPosition(index);
        }

        @Override
        public void onWordAdded(int startIndex, int endIndex) {
            mPresenter.addWordAtPosition(startIndex, endIndex);
        }
    };
}

