package com.gmail.safarov.umid.wcards.data.models;

public class Word {

    private final long id;
    private final long packId;
    private final String enText;
    private final String ruText;
    private final String enCanvasFileName;
    private final String ruCanvasFileName;
    private final String voiceFileName;

    public Word(long packId, String enText) {
        this(0, packId, enText, "", "", "", "");
    }

    public Word(long id, long packId, String enText, String ruText, String enCanvasFileName, String ruCanvasFileName, String voiceFileName) {
        this.id = id;
        this.packId = packId;
        this.enText = enText;
        this.ruText = ruText;
        this.enCanvasFileName = enCanvasFileName;
        this.ruCanvasFileName = ruCanvasFileName;
        this.voiceFileName = voiceFileName;
    }

    public long getId() {
        return id;
    }

    public long getPackId() {
        return packId;
    }

    public String getEnText() {
        return enText;
    }

    public String getRuText() {
        return ruText;
    }

    public String getEnCanvasFileName() {
        return enCanvasFileName;
    }

    public String getRuCanvasFileName() {
        return ruCanvasFileName;
    }

    public String getVoiceFileName() {
        return voiceFileName;
    }

}
