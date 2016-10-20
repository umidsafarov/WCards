package com.gmail.safarov.umid.wcards.lingua;


import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.lingua.models.LinguaTranslations;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface LinguaService {
//    @NonNull
//    @FormUrlEncoded
//    @POST("api/login")
//    Call<LinguaAccount> login(@NonNull @Field("email") String email, @NonNull @Field("password") String password);

    /**
     * Gets the given word translation
     */
    @NonNull
    @FormUrlEncoded
    @POST("gettranslates")
    Call<LinguaTranslations> translate(@NonNull @Field("word") String word);

    /**
     * Gets the sound file from the given URL
     */
    @NonNull
    @GET
    Call<ResponseBody> downloadVoice(@NonNull @Url String fileUrl);
}
