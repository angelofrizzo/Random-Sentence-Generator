package com.randomsentence;
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
    

    // Costruttori
    public UserWord() { }

    public UserWord(String userWord) {
        this.userWord=userWord;
        this.wordType="Null";
    }
    public UserWord(String userWord, String wordType) {
        this.userWord=userWord;
        this.wordType=wordType;
    }
    

    // Getter e Setter
    public String getUserWord() { return userWord; }
    public void setUserWord(String title) { this.userWord = title; }

    public String getWordType(){return wordType;}
    public void setWordType(String type) { this.wordType = type; }

}
