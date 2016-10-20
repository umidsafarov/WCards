package com.gmail.safarov.umid.wcards.lingua;

import android.support.annotation.NonNull;

public interface LinguaCallbackListener<T> {
    void onResponse(@NonNull T response);

    void onError(@NonNull String errorMessage);
}
