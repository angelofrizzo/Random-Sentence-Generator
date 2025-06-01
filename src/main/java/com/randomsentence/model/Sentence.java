package com.randomsentence.model;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public  Long id;
    @Lob
    @Column(length = 10000) // Specifica una lunghezza sufficiente
    public  String promptUtente;
    @Lob
    @Column(length = 10000) // Specifica una lunghezza sufficiente
    public String SyntaxTree;

    @ElementCollection
    public List<String> fraseCasualeList;

    // Costruttori
    public Sentence() { }
    public Sentence(String title, List<String> fraseCasuale, String SyntaxTree) {
        this.promptUtente = title;
        this.fraseCasualeList = new ArrayList<>(fraseCasuale);
        this.SyntaxTree=SyntaxTree;
    }
    public Sentence(String title, String fraseCasuale, String SyntaxTree) {
        this.promptUtente = title;
        this.fraseCasualeList=new ArrayList<>();
        this.fraseCasualeList.add(fraseCasuale);
        this.SyntaxTree=SyntaxTree;
    }
    public Sentence(String title, String fraseCasuale) {
        this.promptUtente = title;
        this.fraseCasualeList=new ArrayList<>();
        this.fraseCasualeList.add(fraseCasuale);
        this.SyntaxTree="";
    }
    public Sentence(String title) {
        this.promptUtente = title;
        this.fraseCasualeList=new ArrayList<>();
        this.SyntaxTree="";

    }

    // Getter e Setter
    public String getPromptUtente() { return promptUtente; }
    public void setPromptUtente(String promptUtente) { this.promptUtente = promptUtente; }

    public List<String> getFraseCasualeList() { return fraseCasualeList; }
    public void setFraseCasualeList(List<String> fraseCasuale) { this.fraseCasualeList = fraseCasuale; }

    public void addFraseCasuale(String fraseCasuale){ this.fraseCasualeList.add(fraseCasuale);}
    public String getFraseCasuale(int index){return fraseCasualeList.get(index);}

    public String getFSyntaxTree() { return SyntaxTree; }
    public void setSyntaxTree(String SyntaxTree) { this.SyntaxTree = SyntaxTree; }
}