package com.gmail.safarov.umid.wcards.data.models;

import java.util.ArrayList;
import java.util.List;

public class Pack {

    private final long id;
    private final String name;
    private final String text;
    private final int pageIndex;
    private final boolean isCompleted;

    public Pack(String name, String text) {
        this(0, name, text, -1, false);
    }

    public Pack(long id, String name, String text, int pageIndex, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.pageIndex = pageIndex;
        this.isCompleted = isCompleted;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public boolean isComplete() {
        return isCompleted;
    }

    /*
    * Paged Pack text
    */
    private static final int PAGE_SIZE = 500;

    private List<String> mTextPages;

    private void createPages() {
        mTextPages = new ArrayList<>();
        int startIndex = 0;
        int endIndex = PAGE_SIZE;

        while (endIndex < text.length()) {
            while (Character.isLetter(text.charAt(endIndex)) || text.charAt(endIndex) == '-') {
                endIndex--;
                if (endIndex == startIndex) {
                    endIndex = startIndex + PAGE_SIZE;
                    break;
                }
            }
            mTextPages.add(text.substring(startIndex, endIndex));
            startIndex = endIndex;
            endIndex += PAGE_SIZE;
        }
        mTextPages.add(text.substring(startIndex, text.length()));
    }

    public int getPagesTotal() {
        if (mTextPages == null)
            createPages();
        return mTextPages.size();
    }

    public List<String> getTextPages() {
        if (mTextPages == null)
            createPages();
        return mTextPages;
    }
}
