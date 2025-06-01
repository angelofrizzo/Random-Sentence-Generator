package com.randomsentence.controller;

import java.util.ArrayList;
import java.util.List;

import com.randomsentence.client.ToxicityController;
import com.randomsentence.model.Sentence;
import com.randomsentence.model.UserWord;
import com.randomsentence.model.Word;
import com.randomsentence.repository.SentenceRepository;
import com.randomsentence.repository.UserWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sentence")
public class SentenceController {
    @Autowired
    private SentenceRepository sentenceRepository;

    @Autowired
    private UserWordRepository userWordRepository;
    private final SentenceGenerator sentenceGenerator = new SentenceGenerator();

    @GetMapping
    public String listSentence(Model model) {
        model.addAttribute("sentence", sentenceRepository.findAll());
        model.addAttribute("userWords", userWordRepository.findAll());
        model.addAttribute("tenses",sentenceGenerator.getKeys());
        return "sentence-list";
    }

    @PostMapping
    public String addSentence(@RequestParam String title, @RequestParam int numero, @RequestParam String tense,@RequestParam(required = false) Boolean toggle,RedirectAttributes redirectAttributes) {
        if(toggle==null) toggle=false;
        System.out.println( "il valore di toggle é: "+toggle);
        if(ToxicityController.isToxic(title, 0.7)){
            List<String> errorsList=new ArrayList<>();
            errorsList.add("your input sentence does not respect our policies");
            redirectAttributes.addFlashAttribute("errors",errorsList);
            return "redirect:/sentence";
        }
        Sentence sentence = sentenceGenerator.generateSentences(title, numero, tense);
        if(toggle){
            for(int i=0;i<sentence.fraseCasualeList.size();i++){
                if(ToxicityController.isToxic(sentence.fraseCasualeList.get(i), 0.5)){
                    System.out.print(sentence.fraseCasualeList.get(i));
                    sentence.fraseCasualeList.remove(i);
                    i--;
                }
            }
        }
        sentenceRepository.save(sentence);
        return "redirect:/sentence";
    }

    @PostMapping("/userWord")
    public String addUserWord(@RequestParam String userWord, RedirectAttributes redirectAttributes) {
        var wordsChecks = sentenceGenerator.addInputStringToRecord(userWord);
        List<String> wordListNotAdded = new ArrayList<>();

        for (var wordCheck : wordsChecks) {
            if (wordCheck.check) {
                userWordRepository.save(new UserWord(wordCheck.word.getWord(), wordCheck.word.getWordType()));
            } else {
                wordListNotAdded.add(wordCheck.word.getWord());
            }
        }

        redirectAttributes.addFlashAttribute("wordListNotAdded", wordListNotAdded);
        return "redirect:/sentence";
    }


    @PostMapping("/userWord/delete")
    public String deleteWord(@RequestParam Long id) {
        UserWord userWordToDelete=userWordRepository.getReferenceById(id);
        Word wordToDelte = new Word(userWordToDelete.userWord,userWordToDelete.wordType);
        sentenceGenerator.removeWordFromRecord(wordToDelte);
        userWordRepository.deleteById(id);
        return "redirect:/sentence";
    }

    @PostMapping("/userWord/deleteAll")
    public String deleteAllWord() {
        for(UserWord userWordToDelete:userWordRepository.findAll()){
             Word wordToDelte = new Word(userWordToDelete.userWord,userWordToDelete.wordType);
            sentenceGenerator.removeWordFromRecord(wordToDelte);

        }
        userWordRepository.deleteAll();
        return "redirect:/sentence";
    }
     @PostMapping("/context")
    public String setContext(@RequestParam String context,RedirectAttributes redirectAttributes) {
        boolean b=sentenceGenerator.changeContext(context);
        if(!b){
            List<String> errorsList=new ArrayList<>();
            errorsList.add("Il contesto non è stato cambiato, prova ad usare un testo piu vario");
            redirectAttributes.addFlashAttribute("errors",errorsList);
        }else{
            userWordRepository.deleteAll();
        }
        return "redirect:/sentence";
    }
    
}
