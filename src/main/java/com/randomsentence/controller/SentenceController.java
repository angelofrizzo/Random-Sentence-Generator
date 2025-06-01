package com.randomsentence.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.randomsentence.client.ToxicityController;
import com.randomsentence.model.Sentence;
import com.randomsentence.model.UserWord;
import com.randomsentence.model.Word;
import com.randomsentence.repository.SentenceRepository;
import com.randomsentence.repository.UserWordRepository;

@Controller
@RequestMapping("/sentence")
public class SentenceController {
    // -----Repository------- //
    @Autowired
    private SentenceRepository sentenceRepository;

    @Autowired
    private UserWordRepository userWordRepository;
    private final SentenceGenerator sentenceGenerator = new SentenceGenerator();


    // -----Return data to /sentence ------- //
    @GetMapping
    public String listSentence(Model model) {
        model.addAttribute("sentence", sentenceRepository.findAll());
        model.addAttribute("userWords", userWordRepository.findAll());
        model.addAttribute("tenses",sentenceGenerator.getKeys());
        return "sentence-list";
    }

    // -----Map the request /sentence to process the sentence------- //
    @PostMapping
    public String addSentence(@RequestParam String title, @RequestParam int numero, @RequestParam String tense,@RequestParam(required = false) Boolean toggle,RedirectAttributes redirectAttributes) {
        if(toggle==null) toggle=false; //to avoid null pointer exception
        if(ToxicityController.isToxic(title, 0.7)){ //verify the toxicity of the imput
            List<String> errorsList=new ArrayList<>();
            errorsList.add("your input sentence does not respect our policies");
            redirectAttributes.addFlashAttribute("errors",errorsList);
            return "redirect:/sentence";
        }
        Sentence sentence = sentenceGenerator.generateSentences(title, numero, tense); //generate a Object Sentence that respect the criteria
        if(toggle){ //control if the User want the output moderate 
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


    //------Map the request /sentence/userWord to add UserWords to the repository------- //
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



    //------Map the request /userWord/delete to remove a specific UserWord from the repository------- //
    @PostMapping("/userWord/delete")
    public String deleteWord(@RequestParam Long id) {
        UserWord userWordToDelete=userWordRepository.getReferenceById(id); //get the UserWord from the h2 repositoy
        Word wordToDelte = new Word(userWordToDelete.userWord,userWordToDelete.wordType); //create a Word with the userWord carateristic
        //remove from both repository
        sentenceGenerator.removeWordFromRecord(wordToDelte);
        userWordRepository.deleteById(id);
        return "redirect:/sentence";
    }


    //------Map the request /userWord/deleteAll to remove all UserWord from the repository------- //
    @PostMapping("/userWord/deleteAll")
    public String deleteAllWord() {
        for(UserWord userWordToDelete:userWordRepository.findAll()){
            //remove all user's word from the sentenceGenerator repository (but not our defoult words)
            Word wordToDelte = new Word(userWordToDelete.userWord,userWordToDelete.wordType);
            sentenceGenerator.removeWordFromRecord(wordToDelte);

        }
        userWordRepository.deleteAll();
        return "redirect:/sentence";
    }


    //------Map the request /userWord/context to add a context to the genereted sentences------- /
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
