package com.randomsentence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

class SentenceControllerTest {

    @Mock
    private SentenceRepository sentenceRepository;

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
    void testListSentence() {
        when(sentenceRepository.findAll()).thenReturn(List.of(new Sentence()));
        when(userWordRepository.findAll()).thenReturn(List.of(new UserWord()));

        String result = sentenceController.listSentence(model);

        verify(model).addAttribute(eq("sentence"), any());
        verify(model).addAttribute(eq("userWords"), any());
        assertEquals("sentence-list", result);
    }


    @Test
    void testDeleteWord() {
        Long id = 1L;

        doNothing().when(userWordRepository).deleteById(id);

        String result = sentenceController.deleteWord(id);

        assertEquals("redirect:/sentence", result);
        verify(userWordRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteAllWord() {
        doNothing().when(userWordRepository).deleteAll();

        String result = sentenceController.deleteAllWord();

        assertEquals("redirect:/sentence", result);
        verify(userWordRepository, times(1)).deleteAll();
    }
}
