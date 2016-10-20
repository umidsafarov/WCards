package com.gmail.safarov.umid.wcards.lingua.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Class needed to serialize LinguaLeo API translation response
 */
public class LinguaTranslations {
    @SerializedName("error_msg")
    private String error;

    @SerializedName("sound_url")
    private String voiceUrl;

    @SerializedName("translate")
    private List<LinguaTranslationVariant> variants;

    public class LinguaTranslationVariant {
        @SerializedName("value")
        private String value;

        @Override
        public String toString() {
            return value;
        }
    }

    public String getError() {
        return error;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public List<LinguaTranslationVariant> getVariants() {
        return variants;
    }


}
