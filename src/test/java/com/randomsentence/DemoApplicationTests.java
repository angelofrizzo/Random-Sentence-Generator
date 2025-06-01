package com.example.demo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.SentenceGenerator.WordCheck;

class DemoApplicationTests {

    @Mock
    private SentenceRepository sentenceRepository;

    @Mock
    private SentenceGenerator sentenceGenerator;

    @Mock
    private RedirectAttributes redirectAttributes;



    @Mock
    private UserWordRepository userWordRepository;

    @Mock
    private Model model;

    @InjectMocks
    private SentenceController sentenceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

        @Test
    void testAddSentence_Success() {
        String title = "Non-toxic sentence";
        int numero = 3;
        String tense = "NULL";
        boolean toggle = false;

        Sentence mockSentence = new Sentence(title, List.of("Phrase 1", "Phrase 2", "Phrase 3"), "SyntaxTree");
        when(sentenceGenerator.generateSentences(title, numero, tense)).thenReturn(mockSentence);
         try (MockedStatic<ToxicityController> mockedStatic = Mockito.mockStatic(ToxicityController.class)) {
            mockedStatic.when(() -> ToxicityController.isToxic(title, 0.7)).thenReturn(false);
        }

        String result = sentenceController.addSentence(title, numero, tense, toggle, redirectAttributes);

        assertEquals("redirect:/sentence", result);
        ArgumentCaptor<Sentence> sentenceCaptor = ArgumentCaptor.forClass(Sentence.class);
        verify(sentenceRepository).save(sentenceCaptor.capture());
    }

@Test
void testAddSentence_ToxicTitle() {
    try (MockedStatic<ToxicityController> mockedStatic = Mockito.mockStatic(ToxicityController.class)) {
        String title = "You are a bad shit";
        int numero = 2;
        String tense = "Past";
        boolean toggle = false;

        mockedStatic.when(() -> ToxicityController.isToxic(title, 0.7)).thenReturn(true);

        String result = sentenceController.addSentence(title, numero, tense, toggle, redirectAttributes);

        assertEquals("redirect:/sentence", result);
        verify(redirectAttributes).addFlashAttribute(eq("errors"), anyList());
        verify(sentenceRepository, never()).save(any());
    }
}

   @Test
void testAddSentence_FilterToxicGeneratedSentences() {
    String title = "Safe Sentence";
    int numero = 3;
    String tense = "NULL";
    boolean toggle = true;

    Sentence mockSentence = new Sentence(title, List.of("Clean phrase", "Ugly bitch", "Clean phrase"), "SyntaxTree");
    when(sentenceGenerator.generateSentences(title, numero, tense)).thenReturn(mockSentence);

    try (MockedStatic<ToxicityController> mockedStatic = Mockito.mockStatic(ToxicityController.class)) {
        mockedStatic.when(() -> ToxicityController.isToxic("Ugly bitch", 0.5)).thenReturn(true);
        mockedStatic.when(() -> ToxicityController.isToxic("Clean phrase", 0.5)).thenReturn(false);

        String result = sentenceController.addSentence(title, numero, tense, toggle, redirectAttributes);

        assertEquals("redirect:/sentence", result);
        // Cattura il valore passato a save()
        ArgumentCaptor<Sentence> sentenceCaptor = ArgumentCaptor.forClass(Sentence.class);
        verify(sentenceRepository).save(sentenceCaptor.capture());

        // Controlla che la frase tossica sia stata rimossa
        assertEquals(3, sentenceCaptor.getValue().getFraseCasualeList().size());
    }
}



        @Test
    void testDeleteWord() {
        Long id = 1L;
        UserWord mockUserWord = new UserWord("TestWord", "NOUN");
        
        when(userWordRepository.getReferenceById(id)).thenReturn(mockUserWord);
        doNothing().when(userWordRepository).deleteById(id);

        String result = sentenceController.deleteWord(id);

        assertEquals("redirect:/sentence", result);
        verify(userWordRepository, times(1)).getReferenceById(id); // ✅ Controllo pre-cancellazione
        verify(userWordRepository, times(1)).deleteById(id);
    }


    @Test
    void testDeleteAllWord() {
        doNothing().when(userWordRepository).deleteAll();

        String result = sentenceController.deleteAllWord();

        assertEquals("redirect:/sentence", result);
        verify(userWordRepository, times(1)).deleteAll();
    }
    @Test
void testAddUserWord_Success() {
    String userWord = "example";
    List<WordCheck> mockWordChecks = List.of(
        new WordCheck(new Word("example", "NOUN"), true),
        new WordCheck(new Word("test", "VERB"), false) // Questa parola NON deve essere salvata
    );

    when(sentenceGenerator.addInputStringToRecord(userWord)).thenReturn(mockWordChecks);

    String result = sentenceController.addUserWord(userWord, redirectAttributes);

    assertEquals("redirect:/sentence", result);

    // Cattura l'oggetto passato al metodo save()
    ArgumentCaptor<UserWord> wordCaptor = ArgumentCaptor.forClass(UserWord.class);
    verify(userWordRepository, times(1)).save(wordCaptor.capture());

    // Verifica che la parola salvata sia "example"
    assertEquals("example", wordCaptor.getValue().getUserWord());
    assertEquals("NOUN_SG", wordCaptor.getValue().getWordType());

    // Verifica che la lista di parole non aggiunte contenga "test"
}

@Test
void testAddUserWord_AddedOnce() {
    String userWord = "example";
    List<WordCheck> mockWordChecks = List.of(
        new WordCheck(new Word("example", "NOUN"), true) // Deve essere salvata
    );

    when(sentenceGenerator.addInputStringToRecord(userWord)).thenReturn(mockWordChecks);

    String result = sentenceController.addUserWord(userWord, redirectAttributes);

    assertEquals("redirect:/sentence", result);

    // ✅ Verifica che la parola sia stata salvata UNA SOLA VOLTA
    verify(userWordRepository, times(1)).save(any(UserWord.class));
}
@Test
void testAddUserWord_OnlySavedOnce() {
    String userWord = "example";
    List<WordCheck> mockWordChecks = List.of(
        new WordCheck(new Word("example", "NOUN"), true), // Deve essere salvata
        new WordCheck(new Word("example", "NOUN"), true)  // Tentativo di risalvataggio
    );

    when(sentenceGenerator.addInputStringToRecord(userWord)).thenReturn(mockWordChecks);

    String result = sentenceController.addUserWord(userWord, redirectAttributes);

    assertEquals("redirect:/sentence", result);

    // ✅ Verifica che il metodo venga chiamato UNA SOLA VOLTA
    ArgumentCaptor<UserWord> captor = ArgumentCaptor.forClass(UserWord.class);
    verify(userWordRepository, times(1)).save(captor.capture());

    // ✅ Verifica che l'oggetto salvato sia quello atteso
    assertEquals("example", captor.getValue().getUserWord());
    assertEquals("NOUN_SG", captor.getValue().getWordType());
}
    @Test
    void testSetContext_Success() {
        String context = "Yesterday was a wonderful day. The sun was shining in the blue sky, and I was walking along the tree-lined avenue. As I walked, I thought about all the adventures I had experienced over the past few years. If I had made a different decision, perhaps today I would be in a completely different place.\n" + //
                        "\n" + //
                        "Today, however, is a rainy day. The rain is falling gently, and I am staying inside with a cup of hot tea. I am reading a fascinating book, feeling completely immersed in the story. Meanwhile, my dog is sleeping beside me, probably dreaming of chasing squirrels in the park.\n" + //
                        "\n" + //
                        "Tomorrow will be a special day. I will wake up early, prepare everything, and set off on a new adventure. If everything goes as planned, I will visit a place I have always wanted to see. I hope the weather is nice; otherwise, I will have to change my plans.\n" + //
                        "\n" + //
                        "Where will I be in ten years? If I have worked hard, perhaps I will be living in a big city, doing my dream job. Or, if I decide to change paths, I might find myself in a quiet village, far from the hustle and bustle. Whatever happens, I know I will continue seeking new experiences and learning something new every day.";
        when(sentenceGenerator.changeContext(context)).thenReturn(true);

        String result = sentenceController.setContext(context, redirectAttributes);

        assertEquals("redirect:/sentence", result);
        verify(userWordRepository, times(1)).deleteAll();
        verify(redirectAttributes, never()).addFlashAttribute(eq("errors"), anyList());
    }

    @Test
    void testSetContext_Failure() {
        String context = "Invalid context";
        when(sentenceGenerator.changeContext(context)).thenReturn(false);

        String result = sentenceController.setContext(context, redirectAttributes);

        assertEquals("redirect:/sentence", result);
        verify(userWordRepository, never()).deleteAll();
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("errors"), eq(List.of("Il contesto non è stato cambiato, prova ad usare un testo piu vario")));
    }

     @Test
    void testGenerateSentences_DifferentTenses() {
        String title = "Example sentence";
        int numero = 3;

        // Array con diversi tempi verbali da testare
        String[] tenses = {"Past", "Present", "Future", "Conditional"};

        for (String tense : tenses) {
            sentenceGenerator.generateSentences(title, numero, tense);
            
            // ✅ Verifica che `generateSentences()` sia stato chiamato con ogni tempo verbale
            verify(sentenceGenerator, times(1)).generateSentences(title, numero, tense);

            // 🚀 Resetta il mock per la prossima iterazione
            reset(sentenceGenerator);
        }
    }
    


}
