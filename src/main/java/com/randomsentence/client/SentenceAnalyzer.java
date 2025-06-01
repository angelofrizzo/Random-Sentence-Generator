package com.randomsentence.client;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.randomsentence.model.Word;

import java.util.ArrayList;
import java.util.List;

public class SentenceAnalyzer {
    public static AnalyzeSyntaxResponse analyzeSentence(String inputString) throws Exception {
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder()
                    .setContent(inputString)
                    .setType(Document.Type.PLAIN_TEXT)
                    .build();

            return language.analyzeSyntax(doc);
        }
    }

    public static List<Word> getWordsAnalyzed(AnalyzeSyntaxResponse inputSentenceAnalyzed) {
        List<Word> wordsAnalyzed = new ArrayList<>();

        boolean find;
        for(var token : inputSentenceAnalyzed.getTokensList()) {
            find = false;
            switch (token.getText().getContent()) {
                case "I" -> {
                    wordsAnalyzed.add(new Word("I", "PRONOUN_1SG"));
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
                case "is" -> {
                    wordsAnalyzed.add(new Word("is", "AUX_BE_PRES_SG"));
                    find = true;
                }
                case "are" -> {
                    wordsAnalyzed.add(new Word("are", "AUX_BE_PRES_PL"));
                    find = true;
                }
                case "was" -> {
                    wordsAnalyzed.add(new Word("was", "AUX_BE_PAST_SG"));
                    find = true;
                }
                case "were" -> {
                    wordsAnalyzed.add(new Word("were", "AUX_BE_PAST_PL"));
                    find = true;
                }
                case "does" -> {
                    wordsAnalyzed.add(new Word("does", "AUX_DO_PRES_SG"));
                    find = true;
                }
            }

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
                        if(token.getText().getContent().length() > 3 &&
                                token.getText().getContent().substring(token.getText().getContent().length()-3).equals("ing")) {
                            wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_ING"));
                        }
                        else{
                            switch (token.getPartOfSpeech().getTense()) {
                                case PRESENT -> {
                                    switch (token.getPartOfSpeech().getPerson()) {
                                        case THIRD -> {
                                            wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_PRES_3SG"));
                                        }
                                        default -> {
                                            wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_BASE"));
                                            wordsAnalyzed.add(new Word(token.getText().getContent(), "VERB_PRES_NON_3SG"));
                                        }
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