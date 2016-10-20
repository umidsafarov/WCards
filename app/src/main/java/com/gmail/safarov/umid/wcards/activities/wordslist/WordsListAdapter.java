package com.gmail.safarov.umid.wcards.activities.wordslist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.models.Word;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.ViewHolder> {

    private List<Word> mWords;
    private WordsListAdapter.CallbackListener mCallbackListener;

    public WordsListAdapter(WordsListAdapter.CallbackListener callbackListener) {
        mCallbackListener = callbackListener;
    }

    @Override
    public WordsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_words_list, parent, false);

        WordsListAdapter.ViewHolder vh = new WordsListAdapter.ViewHolder(v, mCallbackListener);
        v.setOnCreateContextMenuListener(vh);

        return vh;
    }

    @Override
    public void onBindViewHolder(WordsListAdapter.ViewHolder holder, int position) {
        holder.setWord(mWords.get(position));
        holder.setEnText(mWords.get(position).getEnText());
        holder.setRuText(mWords.get(position).getRuText());

    }

    @Override
    public int getItemCount() {
        return mWords == null ? 0 : mWords.size();
    }


    public void replaceData(@NonNull List<Word> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    public void addItem(@NonNull Word word) {
        mWords.add(0, word);
        notifyItemInserted(0);
    }

    public void removeItem(@NonNull Word word) {
        int position = mWords.indexOf(word);
        mWords.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        private Word mWord;
        private WordsListAdapter.CallbackListener mCallbackListener;

        @BindView(R.id.en_text)
        public TextView mEnText;
        @BindView(R.id.ru_text)
        public TextView mRuText;
        @BindView(R.id.no_translation_text)
        public TextView mNoTranslationText;

        public ViewHolder(View v, WordsListAdapter.CallbackListener callbackListener) {
            super(v);
            mCallbackListener = callbackListener;
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
            ButterKnife.bind(this, v);
        }

        public void setWord(@NonNull Word word) {
            mWord = word;
        }

        @NonNull
        public Word getWord() {
            return mWord;
        }

        public void setEnText(@NonNull String text) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
            mEnText.setText(text);
        }

        public void setRuText(@NonNull String text) {
            if ("".equals(text)) {
                mNoTranslationText.setVisibility(View.VISIBLE);
                mRuText.setVisibility(View.GONE);
            } else {
                text = text.substring(0, 1).toUpperCase() + text.substring(1);
                mRuText.setText(text);
                mNoTranslationText.setVisibility(View.GONE);
                mRuText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            mCallbackListener.onOpen(mWord);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            if ("".equals(mWord.getRuText()))
                menu.add(0, R.id.translate, 0, R.string.words_list_menu_translate).setOnMenuItemClickListener(this);
            menu.add(0, R.id.delete, 0, R.string.delete).setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.translate:
                    mCallbackListener.onOpen(mWord);
                    break;
                case R.id.delete:
                    mCallbackListener.onDelete(mWord);
                    break;
            }

            return false;

        }
    }

    public interface CallbackListener {
        void onOpen(Word word);

        void onDelete(Word word);
    }
}
