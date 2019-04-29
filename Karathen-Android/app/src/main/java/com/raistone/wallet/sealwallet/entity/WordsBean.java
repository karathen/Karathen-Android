package com.raistone.wallet.sealwallet.entity;

public class WordsBean {
    private String word;
    private boolean isSelect;
    private int position;
    private String flag;

    public WordsBean() {
    }

    public WordsBean(String word, boolean isSelect) {
        this.word = word;
        this.isSelect = isSelect;
    }

    public WordsBean(String word, boolean isSelect, int position) {
        this.word = word;
        this.isSelect = isSelect;
        this.position = position;
    }

    public WordsBean(String word, boolean isSelect, int position, String flag) {
        this.word = word;
        this.isSelect = isSelect;
        this.position = position;
        this.flag = flag;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
