package com.gmail.safarov.umid.wcards.activities.packs;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.activities.pack.PackActivity;
import com.gmail.safarov.umid.wcards.data.models.Pack;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PacksFragment extends Fragment implements PacksContract.View {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int FILE_PICKER_REQUEST_CODE = 2;

    private PacksContract.Presenter mPresenter;
    private Unbinder butterknifeUnbinder;
    private PacksAdapter mPacksAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.floating_button)
    FloatingActionButton mFloatingButton;
    @BindView(R.id.no_data_text)
    TextView mNoDataText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_packs, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, view);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), noOfColumns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPacksAdapter = new PacksAdapter(adapterCallback);
        mRecyclerView.setAdapter(mPacksAdapter);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void setPresenter(@NonNull PacksContract.Presenter presenter) {
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
    public void setPacks(@NonNull List<Pack> packs) {
        mNoDataText.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mPacksAdapter.replaceData(packs);
    }

    @Override
    public void showNoDataLabel() {
        mNoDataText.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void removePackFromList(@NonNull Pack pack) {
        mPacksAdapter.removeItem(pack);
    }

    @Override
    public void showPack(long id) {
        Intent intent = new Intent(getContext(), PackActivity.class);
        intent.putExtra(PackActivity.EXTRA_PACK_ID, id);
        startActivity(intent);
    }

    @Override
    public void showPermissionRequest(@NonNull @Size(min = 1) String[] permissions) {
        if (ContextCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        else
            mPresenter.permissionGranted(permissions);
    }

    /**
     * Starts intent to choose text file
     */
    @Override
    public void showTextFileSelector() {
        new MaterialFilePicker()
                .withSupportFragment(this)
                .withFilter(Pattern.compile(".*\\.(txt|srt|ass|sub|sbv|)$"))
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(true)
                .start();
    }

    /**
     * Shows Toast
     */
    @Override
    public void showMessage(@StringRes int messageResId) {
        Toast.makeText(getActivity(), messageResId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Runs task on UIThread. Does nothing if activity already destroyed
     */
    @Override
    public void runOnUIThread(@NonNull Runnable runnable) {
        if (getActivity() != null)
            getActivity().runOnUiThread(runnable);
    }

    @OnClick(R.id.floating_button)
    public void floatingButtonClicked() {
        mPresenter.newPack();
    }

    public PacksAdapter.CallbackListener adapterCallback = new PacksAdapter.CallbackListener() {
        @Override
        public void onOpen(Pack pack) {
            mPresenter.openPack(pack);
        }

        @Override
        public void onComplete(Pack pack) {
            mPresenter.completePack(pack);
        }

        @Override
        public void onActivate(Pack pack) {
            mPresenter.activatePack(pack);
        }

        @Override
        public void onDelete(Pack pack) {
            mPresenter.deletePack(pack);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @Size(min = 1) String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mPresenter.permissionGranted(permissions);
                else
                    mPresenter.permissionDenied(permissions);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            mPresenter.createPack(filePath);
        }
    }
}
