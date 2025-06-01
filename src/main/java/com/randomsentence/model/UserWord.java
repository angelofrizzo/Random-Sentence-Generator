package com.randomsentence.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public  Long id;
    public  String userWord;
    public String wordType;
    

    // Constructors
    public UserWord() { }

    public UserWord(String userWord) {
        this.userWord=userWord;
        this.wordType="Null";
    }

    public UserWord(String userWord, String wordType) {
        this.userWord=userWord;
        this.wordType=wordType;
    }

    // Getters and Setters
    public String getUserWord() {
        return userWord;
    }

    public void setUserWord(String title) {
        this.userWord = title;
    }

    public String getWordType(){
        return wordType;
    }

    public void setWordType(String type) {
        this.wordType = type;
    }
}
