package com.example.lml.easyphoto.tongji;

public class TongjiBean {
    String foldName;
    boolean isChoose;
    boolean isNoSubmit;

    public boolean isNoSubmit() {
        return isNoSubmit;
    }

    public void setNoSubmit(boolean noSubmit) {
        isNoSubmit = noSubmit;
    }

    public String getFoldName() {
        return foldName;
    }

    public void setFoldName(String foldName) {
        this.foldName = foldName;
    }


    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }
}
