package com.gmail.safarov.umid.wcards.activities.packs;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.models.Pack;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PacksAdapter extends RecyclerView.Adapter<PacksAdapter.ViewHolder> {

    private List<Pack> mPacks;
    private CallbackListener mCallbackListener;

    public PacksAdapter(CallbackListener callbackListener) {
        mCallbackListener = callbackListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_packs, parent, false);

        ViewHolder vh = new ViewHolder(v, mCallbackListener);
        v.setOnCreateContextMenuListener(vh);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPack(mPacks.get(position));
        holder.setName(mPacks.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mPacks == null ? 0 : mPacks.size();
    }


    public void replaceData(List<Pack> packs) {
        mPacks = packs;
        notifyDataSetChanged();
    }

    public void removeItem(Pack pack) {
        int position = mPacks.indexOf(pack);
        mPacks.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        private Pack mPack;
        private CallbackListener mCallbackListener;

        @BindView(R.id.name_text)
        public TextView mNameText;

        public ViewHolder(View v, CallbackListener callbackListener) {
            super(v);
            mCallbackListener = callbackListener;
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
            ButterKnife.bind(this, v);
        }

        public void setPack(Pack pack) {
            mPack = pack;
        }

        public Pack getPack() {
            return mPack;
        }

        public void setName(String name) {
            mNameText.setText(name);
        }

        @Override
        public void onClick(View v) {
            mCallbackListener.onOpen(mPack);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(0, R.id.open, 0, R.string.packs_list_context_open).setOnMenuItemClickListener(this);
            if (mPack.isComplete())
                menu.add(0, R.id.activate, 0, R.string.packs_list_context_activate).setOnMenuItemClickListener(this);
            else
                menu.add(0, R.id.complete, 0, R.string.complete).setOnMenuItemClickListener(this);
            menu.add(0, R.id.delete, 0, R.string.delete).setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.open:
                    mCallbackListener.onOpen(mPack);
                    break;
                case R.id.complete:
                    mCallbackListener.onComplete(mPack);
                    break;
                case R.id.activate:
                    mCallbackListener.onActivate(mPack);
                    break;
                case R.id.delete:
                    mCallbackListener.onDelete(mPack);
                    break;
            }

            return false;

        }
    }

    public interface CallbackListener {
        void onOpen(Pack pack);

        void onComplete(Pack pack);

        void onActivate(Pack pack);

        void onDelete(Pack pack);
    }
}
