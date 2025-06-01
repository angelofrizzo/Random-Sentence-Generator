package com.randomsentence.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.randomsentence.client.SentenceAnalyzer;
import com.randomsentence.model.Sentence;
import com.randomsentence.model.Word;

public class SentenceGenerator {
    // Records that manage the possible template and words
    private final TemplatesRecord templatesRecord;
    private final WordsRecord wordsRecord;

    // Default constructor
    public SentenceGenerator() {
        templatesRecord = new TemplatesRecord();
        wordsRecord = new WordsRecord();
    }

    // Generator of a sentence given a string, number of desired random strings and a tense
    public Sentence generateSentences(String inputString, int numberOfSentences, String tense) {
        // Make the analysis on an input string
        AnalyzeSyntaxResponse inputSentenceAnalyzed = null;
        List<Word> wordsAnalyzed = new ArrayList<>();
        try {
            inputSentenceAnalyzed = SentenceAnalyzer.analyzeSentence(inputString.toLowerCase());
            wordsAnalyzed = SentenceAnalyzer.getWordsAnalyzed(inputSentenceAnalyzed);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Build generatedStrings list with random generated strings
        List<String> generatedStrings = new ArrayList<>();
        while(numberOfSentences-->0) {
            if(tense.equals("NULL")) {
                generatedStrings.add(generateString(wordsAnalyzed, templatesRecord.getBestTemplate(wordsAnalyzed)));
            }
            else {
                generatedStrings.add(generateString(wordsAnalyzed, templatesRecord.getTemplateByTense(tense)));
            }
        }

        // Generate syntaxTree
        String syntaxTree = SyntaxTree.syntaxTreeGenerator(inputSentenceAnalyzed);

        return new Sentence(inputString, generatedStrings, syntaxTree);
    }

    // Get a list of tenses
    public List<String> getKeys(){
        return templatesRecord.getKeys();
    }

    // Generate a random string given a list of words and a template
    public String generateString(List<Word> wordsAnalyzed, String template) {
        // Try to fill the template with the words from given list
        for(var word : wordsAnalyzed){
            if(template.contains("{"+word.getWordType()+"}")) {
                template = template.replaceFirst("\\{"+word.getWordType()+"}", word.getWord());
            }
        }

        // Complete the template with the words from wordsRecord
        while(template.contains("{")) {
            final int startIndex = template.indexOf("{");
            final int endIndex = template.indexOf("}");
            final String wordType = template.substring(startIndex+1, endIndex);
            template = template.replaceFirst("\\{"+wordType+"}", wordsRecord.getWord(wordType));
        }

        return template;
    }

    // Remove a word form record
    public void removeWordFromRecord(Word word) {
        wordsRecord.removeWord(word);
    }

    // Useful class for passing words with boolean value
    public static class WordCheck {
        public Word word;
        public boolean check;

        public WordCheck(Word word, boolean check) {
            this.word = word;
            this.check = check;
        }
    }

    // Take an input string, parse it to assign a corresponding token value, and add it to the internal record
    public List<WordCheck> addInputStringToRecord(String inputString) {
        // Make the analysis on an input string
        List<Word> wordsAnalyzed = new ArrayList<>();
        try {
            wordsAnalyzed = SentenceAnalyzer.getWordsAnalyzed(SentenceAnalyzer.analyzeSentence(inputString));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Add analysis words to internal record
        List<WordCheck> wordsChecks = new ArrayList<>();
        for(var word : wordsAnalyzed) {
            wordsChecks.add(
                    new WordCheck(word, wordsRecord.addWordWithCheck(word))
            );
        }

        return wordsChecks;
    }

    // Check if the context provided is sufficient for the new wordsRecord
    // - If yes, delete and complete the record with new words and return yes value
    public boolean changeContext(String contest) {
        // Get a valid token from the given context
        List<Word> contestAnalyzed = new ArrayList<>();
        try {
            contestAnalyzed = SentenceAnalyzer.getWordsAnalyzed(SentenceAnalyzer.analyzeSentence(contest));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Check if the context provided is sufficient and, if so, add the change to the internal record
        if(templatesRecord.isEnoughAndUpdate(contestAnalyzed)){
            wordsRecord.changeContest(contestAnalyzed);
            System.out.println("Contest changed");
            return true;
        }
        else{
            return false;
        }
    }
}
