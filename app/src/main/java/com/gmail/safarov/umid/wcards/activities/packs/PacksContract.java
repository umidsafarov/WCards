package com.gmail.safarov.umid.wcards.activities.packs;

import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.StringRes;

import com.gmail.safarov.umid.wcards.BasePresenter;
import com.gmail.safarov.umid.wcards.BaseView;
import com.gmail.safarov.umid.wcards.data.models.Pack;

import java.util.List;

public interface PacksContract {
    interface View extends BaseView<Presenter> {

        void setPacks(@NonNull List<Pack> packs);

        void showNoDataLabel();

        void removePackFromList(@NonNull Pack pack);

        void showPack(long id);

        void showPermissionRequest(@NonNull String[] permissions);

        void showTextFileSelector();

        void showMessage(@StringRes int messageResId);

        void runOnUIThread(@NonNull Runnable runnable);

    }

    interface Presenter extends BasePresenter {

        void showActivePacks();

        void showCompletedPacks();

        void openPack(@NonNull Pack pack);

        void newPack();

        void permissionGranted(@NonNull @Size(min = 1) String[] permissions);

        void permissionDenied(@NonNull @Size(min = 1) String[] permissions);

        void createPack(@NonNull String filePath);

        void deletePack(@NonNull Pack pack);

        void completePack(@NonNull Pack pack);

        void activatePack(@NonNull Pack pack);

        boolean getIsActivePacksShown();

    }
}
