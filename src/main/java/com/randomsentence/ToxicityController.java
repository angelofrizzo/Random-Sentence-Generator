package com.randomsentence;

import java.io.IOException;

import com.google.cloud.language.v1.ClassificationCategory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.ModerateTextResponse;

public class ToxicityController {
    public static boolean  isToxic(String textToCheck, double sensibility){
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            // Creazione del documento
            Document doc = Document.newBuilder()
                .setContent(textToCheck)
                .setType(Document.Type.PLAIN_TEXT)
                .build();
            ModerateTextResponse response= language.moderateText(doc);
           //System.err.println(response);//
           for(ClassificationCategory campo : response.getModerationCategoriesList()){
                if(campo.getConfidence()>0.8) return true;

           }
        } catch (IOException e) {
            System.err.println("Errore durante l'analisi delle entità: " + e.getMessage());
        }
        return false;
    }

}
