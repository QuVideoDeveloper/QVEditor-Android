package com.quvideo.application.editor;

public class EditOperate {

    private int resId;
    private String title;

    public EditOperate(int resId, String title) {
        this.resId = resId;
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public String getTitle() {
        return title;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
