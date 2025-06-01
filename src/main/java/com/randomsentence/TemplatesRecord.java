package com.randomsentence;

import java.io.FileReader;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TemplatesRecord {
    private final Random rand;

    // Hashmap where the keys are the available verb tense
    private HashMap<String, Vector<String>> tenseTemplates;

    // Total number of templates available
    private int numOfTemplates;

    // Default constructor with load from templates.json
    public TemplatesRecord() {
        tenseTemplates = new HashMap<>();
        rand = new Random();
        loadTemplatesFromJSon("./src/main/resources/records/templates.json");

        // Calculate a total number of templates
        for (String tense : tenseTemplates.keySet()) {
            numOfTemplates += tenseTemplates.get(tense).size();
        }
    }

    private void loadTemplatesFromJSon(String filePath) {
        Gson gson = new Gson();
        try{
            Reader reader = new FileReader(filePath);
            tenseTemplates = gson.fromJson(reader, new TypeToken<HashMap<String, Vector<String>>>(){}.getType());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // Returns the template to the index position by viewing the map as a vector
    private String getIndex(int index) throws IndexOutOfBoundsException {
        for(String tense : tenseTemplates.keySet()){
            if(index >= tenseTemplates.get(tense).size()){
                index -= tenseTemplates.get(tense).size();
            }
            else{
                return tenseTemplates.get(tense).get(index);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    // Returns a totally random template
    public String getRandomTemplate() throws NullPointerException {
        final int randIndex = rand.nextInt(numOfTemplates);
        return getIndex(randIndex);
    }

    // Returns best match template from a 3 consecutive random templates
    public String getBestTemplate(List<Word> inputWord) {
        // Vector with 3 consecutive random templates
        Vector<Map.Entry<String, Integer>> templatePairs = new Vector<>();

        // Random index where 3 consecutive templates start
        final int randomIndex = rand.nextInt(numOfTemplates);
        final int RANDOM_WORDS = 3;
        for(int i = randomIndex; i < randomIndex + RANDOM_WORDS; i++) {
            templatePairs.add(new AbstractMap.SimpleEntry<>(getIndex(i % numOfTemplates), 0));
        }

        // Evaluate the number of math tokens for each template
        for (var currentTemplate : templatePairs) {
            String copyTemplate = currentTemplate.getKey();
            for (Word word : inputWord) {
                if (copyTemplate.contains("{"+word.getWordType()+"}")) {
                    copyTemplate = copyTemplate.replaceFirst("\\{"+word.getWordType()+"}", "");
                    currentTemplate.setValue(currentTemplate.getValue() + 1);
                }
            }
        }

        // Find the template with max match score
        Map.Entry<String, Integer> bestTemplate = new AbstractMap.SimpleEntry<>("", -1);
        for(var currentTemplate : templatePairs) {
            if (currentTemplate.getValue() > bestTemplate.getValue()) {
                bestTemplate = currentTemplate;
            }
        }

        return bestTemplate.getKey();
    }

    // Returns a random template with given tense
    public String getTemplateByTense(String tense) throws NullPointerException {
        if (!tenseTemplates.containsKey(tense))
            throw new NullPointerException();

        return tenseTemplates.get(tense).get(rand.nextInt(tenseTemplates.get(tense).size()));
    }

    // Get a list of tenses
    public List<String> getKeys() {
        return List.copyOf(tenseTemplates.keySet());
    }

    // Assess whether the list of words is sufficient to fill at least one template for each verb tense
    // - If yes removes templates that can't be filled
    public boolean isEnoughAndUpdate(List<Word> contest) {
        WordsRecord tempWordsRecord = new WordsRecord(contest);

        // New hashmap where a fillable template is put in
        HashMap<String, Vector<String>> tempTenseTemplates = new HashMap<>();
        for (var tense : tenseTemplates.keySet()) {
            for (String template : tenseTemplates.get(tense)) {
                String copyTemplate = template;

                // For each current template check if it's fillable
                while(copyTemplate.contains("{")) {
                    final int startIndex = copyTemplate.indexOf("{");
                    final int endIndex = copyTemplate.indexOf("}");
                    final String wordType = copyTemplate.substring(startIndex+1, endIndex);
                    if(!tempWordsRecord.contatinsKey(wordType)){
                        break;
                    }
                    copyTemplate = copyTemplate.replaceFirst("\\{"+wordType+"}", tempWordsRecord.getWord(wordType));
                }

                // If copyTemplate is completed then template is added to tempTenseTemplates
                if(!copyTemplate.contains("{")){
                    tempTenseTemplates.putIfAbsent(tense, new Vector<>());
                    tempTenseTemplates.get(tense).add(template);
                }
            }

            // If a tense vector remains empty then the list of words is insufficient
            if(!tempTenseTemplates.containsKey(tense)){
                return false;
            }
        }
        tenseTemplates = tempTenseTemplates;
        return true;
    }
}
