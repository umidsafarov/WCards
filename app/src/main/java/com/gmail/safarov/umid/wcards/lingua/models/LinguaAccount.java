package com.gmail.safarov.umid.wcards.lingua.models;

import com.google.gson.annotations.SerializedName;

/**
 * Class needed to serialize LinguaLeo API login response
 */
public class LinguaAccount {
    @SerializedName("error_msg")
    private String error;

    @SerializedName("user")
    private LinguaUser user;

    public class LinguaUser {

        @SerializedName("autologin_key")
        private String token;

        @SerializedName("nickname")
        private String name;

        @SerializedName("avatar")
        private String avatarUrl;
    }

    public String getError() {
        return error;
    }

    public String getToken() {
        return user.token;
    }

    public String getName() {
        return user.name;
    }

    public String getAvatarUrl() {
        return user.avatarUrl;
    }
}
