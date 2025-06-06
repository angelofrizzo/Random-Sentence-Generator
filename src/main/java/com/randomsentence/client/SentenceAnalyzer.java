package com.randomsentence.client;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.PartOfSpeech;
import com.randomsentence.model.Word;

import java.util.ArrayList;
import java.util.List;

public class SentenceAnalyzer {
    // This parses the string given by the user via the Google API
    public static AnalyzeSyntaxResponse analyzeSentence(String inputString) throws Exception {
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder()
                    .setContent(inputString)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            return language.analyzeSyntax(doc);
        }
    }

    // This method returns the list of words given the output generated by Google API
    public static List<Word> getWordsAnalyzed(AnalyzeSyntaxResponse inputSentenceAnalyzed) {
        List<Word> wordsAnalyzed = new ArrayList<>();

        // A boolean value that becomes true if the wordType has been identified in the first section of the for
        boolean find;
        for(var token : inputSentenceAnalyzed.getTokensList()) {
            find = false;

            // Given their generality, these pronouns and verbs are treated differently than the rest of the tokens
            switch (token.getText().getContent()) {
                case "I", "is", "are", "was", "were", "does" -> {
                    find = true;
                }
                case "they", "we" -> {
                    wordsAnalyzed.add(new Word(token.getText().getContent(), "PRONOUN_PL"));
                    find = true;
                }
                case "he", "she", "it" -> {
                    wordsAnalyzed.add(new Word(token.getText().getContent(), "PRONOUN_3SG"));
                    find = true;
                }
            }

            // In this second one the wordType is identified thanks to the analysis of Google API
            if(!find) {
                switch (token.getPartOfSpeech().getTag()) {
                    case NOUN -> {
                        switch (token.getPartOfSpeech().getNumber()) {
                            case SINGULAR ->
                                    wordsAnalyzed.add(new Word(token.getText().getContent(), "NOUN_SG"));
                            case PLURAL ->
                                    wordsAnalyzed.add(new Word(token.getText().getContent(), "NOUN_PL"));
                        }
                    }
                    case VERB -> {
                        if(token.getText().getContent().endsWith("ing")) {
                            wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_ING"));
                        }
                        else{
                            switch (token.getPartOfSpeech().getTense()) {
                                case PRESENT -> {
                                    if (token.getPartOfSpeech().getPerson() == PartOfSpeech.Person.THIRD) {
                                        wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_PRES_3SG"));
                                    } else {
                                        wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_BASE"));
                                    }
                                }
                                case PAST -> {
                                    wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_PAST"));
                                }
                            }
                        }
                    }
                    case CONJ -> {
                        wordsAnalyzed.add(new Word(token.getText().getContent(), "CONJUNCTION"));
                    }
                    case ADP -> {
                        wordsAnalyzed.add(new Word(token.getText().getContent(), "PREPOSITION"));
                    }
                    case ADJ -> {
                        wordsAnalyzed.add(new Word(token.getText().getContent(), "ADJ"));
                    }
                    case ADV -> {
                        wordsAnalyzed.add(new Word(token.getText().getContent(), "ADV"));
                    }
                }
            }
        }

        return wordsAnalyzed;
    }
}