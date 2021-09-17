package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Language;
import com.a2.backend.exception.LanguageNotValidException;
import com.a2.backend.repository.LanguageRepository;
import com.a2.backend.service.LanguageService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class LanguageServiceImplTest {

    @Autowired private LanguageService languageService;

    @Autowired private LanguageRepository languageRepository;

    @Test
    void Test001_LanguageServiceWhenReceivesValidLanguageNameShouldCreateLanguageWithGivenName() {
        Language language = languageService.createLanguage("Java");

        assertNull(language.getId());
        assertEquals("Java", language.getName());
    }

    @Test
    void
            Test002_LanguageServiceWhenReceivesValidLanguageNamesShouldCreateLanguageListWithGivenNames() {
        List<String> languagesNames = Arrays.asList("Java", "PHP");

        List<Language> languageList = languageService.findOrCreateLanguage(languagesNames);

        assertEquals(2, languageList.size());

        assertEquals("Java", languageList.get(0).getName());
        assertEquals("PHP", languageList.get(1).getName());
    }

    @Test
    void
            Test003_LanguageServiceGivenAnExistingLanguageWhenTheSameLanguageIsSentTheOldLanguageIsReused() {

        Language language1 = languageRepository.save(languageService.createLanguage("C"));

        List<Language> languageList =
                languageService.findOrCreateLanguage(Arrays.asList("C", "PHP"));

        assertEquals(2, languageList.size());

        assertEquals(languageList.get(0).getId(), language1.getId());
    }

    @Test
    void Test004_LanguageServiceWhenReceivesValidLanguageNameShouldReturnLanguageWithGivenName() {

        Language language1 = languageRepository.save(languageService.createLanguage("Java"));
        Language language2 = languageRepository.save(languageService.createLanguage("C"));

        List<Language> languages = languageService.findLanguagesByNames(Arrays.asList("Java", "C"));

        assertEquals(2, languages.size());
        assertTrue(languages.contains(language1));
        assertTrue(languages.contains(language2));
    }

    @Test
    void
            Test005_LanguageServiceGivenValidLanguageNamesToUpdateAndCurrentLanguagesShouldReturnRemovedLanguages() {

        Language language1 = languageRepository.save(languageService.createLanguage("Java"));
        Language language2 = languageRepository.save(languageService.createLanguage("C"));

        List<Language> currentLanguages = Arrays.asList(language1, language2);
        List<String> updatedLanguagesNames = Arrays.asList("PHP", "Java");

        List<Language> removedLanguages =
                languageService.getRemovedLanguages(updatedLanguagesNames, currentLanguages);

        assertEquals(1, removedLanguages.size());
        assertEquals("C", removedLanguages.get(0).getName());
    }

    @Test
    void Test006_LanguageServiceShouldReturnListWithAllLanguageValidNames() {
        String validLanguageNames =
                "Java, C, C++, C#, Python, Visual Basic .NET, PHP, JavaScript, TypeScript, Delphi/Object Pascal, Swift, Perl, Ruby, Assembly language, R, Visual Basic, Objective-C, Go, MATLAB, PL/SQL, Scratch, SAS, D, Dart, ABAP, COBOL, Ada, Fortran, Transact-SQL, Lua, Scala, Logo, F#, Lisp, LabVIEW, Prolog, Haskell, Scheme, Groovy, RPG (OS/400), Apex, Erlang, MQL4, Rust, Bash, Ladder Logic, Q, Julia, Alice, VHDL, Awk, (Visual) FoxPro, ABC, ActionScript, APL, AutoLISP, bc, BlitzMax, Bourne shell, C shell, CFML, cg, CL (OS/400), Clipper, Clojure, Common Lisp, Crystal, Eiffel, Elixir, Elm, Emacs Lisp, Forth, Hack, Icon, IDL, Inform, Io, J, Korn shell, Kotlin, Maple, ML, NATURAL, NXT-G, OCaml, OpenCL, OpenEdge ABL, Oz, PL/I, PowerShell, REXX, Ring, S, Smalltalk, SPARK, SPSS, Standard ML, Stata, Tcl, VBScript, Verilog";
        List<String> validLanguageList =
                new ArrayList<>(Arrays.asList(validLanguageNames.split(", ")));

        assertEquals(validLanguageList, languageService.getValidLanguages());
    }

    @Test
    void Test007_LanguageServiceWhenReceivesNotValidLanguageNameShouldThrowException() {
        assertThrows(
                LanguageNotValidException.class,
                () ->
                        languageService.findOrCreateLanguage(
                                Arrays.asList("Not Valid Name", "Java")));
    }
}
