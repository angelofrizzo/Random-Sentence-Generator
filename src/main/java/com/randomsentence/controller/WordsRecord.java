package com.randomsentence.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randomsentence.model.Word;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;

public class WordsRecord {
    private final Random rand;

    // Hashmap where the keys are the token type
    private HashMap<String, Vector<String>> words;

    // Default constructor with load from words.json
    WordsRecord() {
        words = new HashMap<>();
        rand = new Random();
        loadWordsFromJSon("./src/main/resources/records/words.json");
    }

    // Constructor that creates wordsRecord from a given list of words
    WordsRecord(List<Word> initWords) {
        words = new HashMap<>();
        rand = new Random();
        for (Word word : initWords) {
            addWord(word);
        }
    }

    public void loadWordsFromJSon(String filePath) {
        Gson gson = new Gson();
        try{
            Reader reader = new FileReader(filePath);
            words = gson.fromJson(reader, new TypeToken<HashMap<String, Vector<String>>>(){}.getType());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // Add word to internal record
    public void addWord(Word word) {
        // Check if the word type is already present in the hashmap, if not add the type to the hashmap
        words.putIfAbsent(word.getWordType(), new Vector<>());
        if(!words.get(word.getWordType()).contains(word.getWord())) {
            words.get(word.getWordType()).add(word.getWord());
        }
    }

    // Try adding a new word to the record, if not false value is returned and do not modify the record
    public boolean addWordWithCheck(Word word) {
        words.putIfAbsent(word.getWordType(), new Vector<>());
        if(!words.get(word.getWordType()).contains(word.getWord())) {
            words.get(word.getWordType()).add(word.getWord());
            return true;
        }
        else{
            return false;
        }
    }

    // Return a random word given a token type
    public String getWord(String wordType) throws IllegalArgumentException {
        // Returns an error if there are no words with the specified token type
        if(!words.containsKey(wordType)) {
            throw new IllegalArgumentException("Word type " + wordType + " is not found");
        }

        return words.get(wordType).get(rand.nextInt(words.get(wordType).size()));
    }

    // Check if record contain a specific token type
    public boolean contatinsKey(String key) {
        return words.containsKey(key);
    }

    // Remove a word from the internal record, if that word is not present do nothing
    public void removeWord(Word word) {
        if(words.containsKey(word.getWordType())) {
            if(!words.get(word.getWordType()).contains(word.getWord())) {
                words.get(word.getWordType()).remove(word.getWord());
            }
        }
    }

    // Remove all words from the record and add all words from the given list
    public void changeContest(List<Word> newContest){
        words.clear();

        for(Word word : newContest) {
            addWord(word);
        }
    }
}
