package com.randomsentence;

public class Word {
    private final String word;
    private final String wordType;

    public Word(String word, String wordType) {
        this.word = word;
        this.wordType = wordType;
    }

    public String getWord() {
        return word;
    }

    public String getWordType() {
        return wordType;
    }
}
