package com.gmail.safarov.umid.wcards.lingua;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Interceptor that repeats request if it fails
 * the number of attempts is 3
 */
public class RepeaterInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        int attemptsCount = 0;
        while (!response.isSuccessful() && attemptsCount < 3) {
            attemptsCount++;
            response = chain.proceed(request);
        }
        return response;
    }
}