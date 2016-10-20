package com.gmail.safarov.umid.wcards.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.data.models.Word;
import com.gmail.safarov.umid.wcards.data.source.DataSource;

import java.io.File;
import java.util.List;

public class ClearFilesUtils {

    /**
     * Deletes files associated with all Words of the given Pack ID
     * e.g. voice files, drawn cards
     */
    public static void deleteFilesOfPack(@NonNull Context context, @NonNull DataSource dataSource, long packId) {
        List<Word> words = dataSource.getWords(packId);
        for (Word word : words) {
            if (!"".equals(word.getVoiceFileName()))
                new File(context.getExternalFilesDir(null), word.getVoiceFileName()).delete();
            if (!"".equals(word.getEnCanvasFileName()))
                new File(context.getExternalFilesDir(null), word.getEnCanvasFileName()).delete();
            if (!"".equals(word.getRuCanvasFileName()))
                new File(context.getExternalFilesDir(null), word.getRuCanvasFileName()).delete();
        }
    }


    /**
     * Deletes files associated with the given Word ID
     * e.g. voice file, drawn card
     */
    public static void deleteFilesOfWord(@NonNull Context context, @NonNull DataSource dataSource, long wordId) {
        Word word = dataSource.getWord(wordId);
        if (!"".equals(word.getVoiceFileName()))
            new File(context.getExternalFilesDir(null), word.getVoiceFileName()).delete();
        if (!"".equals(word.getEnCanvasFileName()))
            new File(context.getExternalFilesDir(null), word.getEnCanvasFileName()).delete();
        if (!"".equals(word.getRuCanvasFileName()))
            new File(context.getExternalFilesDir(null), word.getRuCanvasFileName()).delete();

    }
}
