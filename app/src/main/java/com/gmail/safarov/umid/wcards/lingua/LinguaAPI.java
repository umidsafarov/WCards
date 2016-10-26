package com.gmail.safarov.umid.wcards.lingua;

import android.os.AsyncTask;

import com.gmail.safarov.umid.wcards.lingua.models.LinguaTranslations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Gets data from LinguaLeo API
 */
public class LinguaAPI {

    private static final String API_URL = "http://api.lingualeo.com/";

    private static LinguaAPI instance;

    public static LinguaAPI getInstance() {
        if (instance == null) {
            instance = new LinguaAPI();
        }
        return instance;
    }

    private LinguaService service;

    private LinguaAPI() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.interceptors().add(new RepeaterInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();

        service = retrofit.create(LinguaService.class);
    }

//    public void login(final String email, final String password, final LinguaCallbackListener<LinguaAccount> callbackListener) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Call<LinguaAccount> call = service.login(email, password);
//                    Response<LinguaAccount> response = call.execute();
//                    if (response.isSuccessful())
//                        callbackListener.onResponse(response.body());
//                    else
//                        callbackListener.onError(response.errorBody().string());
//                } catch (IOException exception) {
//                    callbackListener.onError(exception.getMessage());
//                }
//            }
//        }).start();
//    }

    /**
     * Gets the given word translation
     */
    public Call translate(String word, final LinguaCallbackListener<LinguaTranslations> callbackListener) {
        final Call<LinguaTranslations> call = service.translate(word);

        new AsyncTask<Void, Void, Response<LinguaTranslations>>() {
            String errorMessage;

            @Override
            protected Response<LinguaTranslations> doInBackground(Void... params) {
                try {
                    return call.execute();
                } catch (IOException e) {
                    errorMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Response<LinguaTranslations> response) {
                if (!call.isCanceled()) {
                    if (errorMessage == null) {
                        if (response.isSuccessful()) {
                            callbackListener.onResponse(response.body());
                        } else {
                            try {
                                callbackListener.onError(response.errorBody().string());
                            } catch (IOException e) {
                                callbackListener.onError(e.getMessage());
                            }
                        }
                    } else {
                        callbackListener.onError(errorMessage);
                    }
                }
            }
        }

                .

                        execute();

        return call;
    }


    /**
     * Gets the sound file from the given URL
     */

    public Call downloadVoice(final String url, final File file, final LinguaCallbackListener<File> callbackListener) {
        final Call<ResponseBody> call = service.downloadVoice(url);

        new AsyncTask<Void, Void, File>() {
            String errorMessage;

            @Override
            protected File doInBackground(Void... params) {
                try {
                    Response<ResponseBody> response = call.execute();
                    if (call.isCanceled())
                        return null;
                    if (response.isSuccessful()) {
                        try {
                            InputStream inputStream = null;
                            OutputStream outputStream = null;

                            try {
                                byte[] fileReader = new byte[4096];

                                inputStream = response.body().byteStream();
                                outputStream = new FileOutputStream(file);

                                while (!call.isCanceled()) {
                                    int read = inputStream.read(fileReader);

                                    if (read == -1) {
                                        break;
                                    }

                                    outputStream.write(fileReader, 0, read);
                                }

                                outputStream.flush();

                                return file;
                            } catch (IOException e) {
                                errorMessage = e.getMessage();
                                return null;
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
                        } catch (IOException e) {
                            errorMessage = e.getMessage();
                            return null;
                        }

                    } else {
                        errorMessage = response.errorBody().string();
                        return null;
                    }
                } catch (IOException e) {
                    errorMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File file) {
                if (!call.isCanceled()) {
                    if (errorMessage == null) {
                        callbackListener.onResponse(file);
                    } else {
                        callbackListener.onError(errorMessage);
                    }
                }
            }
        }.execute();

        return call;
    }
}