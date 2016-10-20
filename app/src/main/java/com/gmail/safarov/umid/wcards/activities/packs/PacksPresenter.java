package com.gmail.safarov.umid.wcards.activities.packs;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.models.Pack;
import com.gmail.safarov.umid.wcards.data.source.DataSource;
import com.gmail.safarov.umid.wcards.utils.ClearFilesUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class PacksPresenter implements PacksContract.Presenter {

    private Context mContext;
    private DataSource mDataSource;
    private PacksContract.View mView;

    private boolean mIsActivePacksShown = true;

    public PacksPresenter(@NonNull Context context, @NonNull PacksContract.View view, @NonNull DataSource dataSource, boolean isActivePacksShown) {
        mContext = context;
        mDataSource = dataSource;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mIsActivePacksShown)
            showActivePacks();
        else
            showCompletedPacks();
    }

    @Override
    public void showActivePacks() {
        List<Pack> packs = mDataSource.getPacks(true);
        if (packs.size() > 0)
            mView.setPacks(packs);
        else
            mView.showNoDataLabel();
        mIsActivePacksShown = true;
    }

    @Override
    public void showCompletedPacks() {
        List<Pack> packs = mDataSource.getPacks(false);
        if (packs.size() > 0)
            mView.setPacks(packs);
        else
            mView.showNoDataLabel();
        mIsActivePacksShown = false;
    }

    @Override
    public void openPack(@NonNull Pack pack) {
        mView.showPack(pack.getId());
    }

    /**
     * Starts creation of new Pack
     */
    @Override
    public void newPack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mView.showPermissionRequest(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        else
            mView.showTextFileSelector();
    }

    @Override
    public void permissionGranted(@NonNull @Size(min = 1) String[] permissions) {
        mView.showTextFileSelector();
    }

    @Override
    public void permissionDenied(@NonNull @Size(min = 1) String[] permissions) {
        mView.showMessage(R.string.packs_error_message_read_permission_required);
    }

    /**
     * Creates new Pack from the given text file
     *
     * @param filePath path to the text
     */
    @Override
    public void createPack(@NonNull final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    File file = new File(filePath);
                    // Do not allow files greater than 512KB. The best way to use our technique is learning small amount of new words in one Pack.
                    if (file.length() > 512 * 1024) {
                        mView.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mView.showMessage(R.string.packs_error_message_big_file);
                            }
                        });
                        return;
                    }

                    StringBuilder text = new StringBuilder();
                    String line;
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();

                    String filename = file.getName();
                    // remove extension of file
                    int extensionPos = filename.lastIndexOf(".");
                    if (extensionPos > 0)
                        filename = filename.substring(0, extensionPos);
                    // crop file name if it is too long
                    if (filename.length() > 15)
                        filename = filename.substring(0, 15);

                    // crate new pack
                    Pack pack = new Pack(filename, text.toString());
                    final long packId = mDataSource.createPack(pack);

                    // open created pack editing
                    mView.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.showPack(packId);
                        }
                    });

                } catch (IOException e) {
                    mView.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.showMessage(R.string.packs_error_message_cant_read_file);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void deletePack(@NonNull Pack pack) {
        ClearFilesUtils.deleteFilesOfPack(mContext, mDataSource, pack.getId());
        mDataSource.deletePack(pack.getId());
        mView.removePackFromList(pack);
        if (mDataSource.getPacksCount(mIsActivePacksShown) == 0)
            mView.showNoDataLabel();
    }

    @Override
    public void completePack(@NonNull Pack pack) {
        mDataSource.setPackIsComplete(pack.getId(), true);
        mView.removePackFromList(pack);
        if (mDataSource.getPacksCount(mIsActivePacksShown) == 0)
            mView.showNoDataLabel();
        mView.showMessage(R.string.packs_message_moved_to_completed);
    }

    @Override
    public void activatePack(@NonNull Pack pack) {
        mDataSource.setPackIsComplete(pack.getId(), false);
        mView.removePackFromList(pack);
        if (mDataSource.getPacksCount(mIsActivePacksShown) == 0)
            mView.showNoDataLabel();
        mView.showMessage(R.string.packs_message_moved_to_active);
    }

    @Override
    public boolean getIsActivePacksShown() {
        return mIsActivePacksShown;
    }
}
