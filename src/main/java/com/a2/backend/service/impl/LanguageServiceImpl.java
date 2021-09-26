package com.a2.backend.service.impl;

import com.a2.backend.entity.Language;
import com.a2.backend.exception.LanguageNotValidException;
import com.a2.backend.repository.LanguageRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.LanguageService;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    private final ProjectRepository projectRepository;

    private final String validLanguageNames =
            "Java, C, C++, C#, Python, Visual Basic .NET, PHP, JavaScript, TypeScript, Delphi/Object Pascal, Swift, Perl, Ruby, Assembly language, R, Visual Basic, Objective-C, Go, MATLAB, PL/SQL, Scratch, SAS, D, Dart, ABAP, COBOL, Ada, Fortran, Transact-SQL, Lua, Scala, Logo, F#, Lisp, LabVIEW, Prolog, Haskell, Scheme, Groovy, RPG (OS/400), Apex, Erlang, MQL4, Rust, Bash, Ladder Logic, Q, Julia, Alice, VHDL, Awk, (Visual) FoxPro, ABC, ActionScript, APL, AutoLISP, bc, BlitzMax, Bourne shell, C shell, CFML, cg, CL (OS/400), Clipper, Clojure, Common Lisp, Crystal, Eiffel, Elixir, Elm, Emacs Lisp, Forth, Hack, Icon, IDL, Inform, Io, J, Korn shell, Kotlin, Maple, ML, NATURAL, NXT-G, OCaml, OpenCL, OpenEdge ABL, Oz, PL/I, PowerShell, REXX, Ring, S, Smalltalk, SPARK, SPSS, Standard ML, Stata, Tcl, VBScript, Verilog";

    private final List<String> validLanguageList =
            new ArrayList<>(Arrays.asList(validLanguageNames.split(", ")));

    public LanguageServiceImpl(
            LanguageRepository languageRepository, ProjectRepository projectRepository) {
        this.languageRepository = languageRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Language> findOrCreateLanguage(List<String> languagesToAdd) {
        List<Language> languageList = new ArrayList<>();

        for (String languageName : languagesToAdd) {
            Optional<Language> optionalLanguage = findLanguageByName(languageName);

            if (optionalLanguage.isEmpty()) {
                if (validLanguageList.contains(languageName)) {
                    languageList.add(createLanguage(languageName));
                } else {
                    throw new LanguageNotValidException(
                            String.format("Language %s is not valid", languageName));
                }
            } else {
                languageList.add(optionalLanguage.get());
            }
        }
        return languageList;
    }

    @Override
    public Language createLanguage(String languageName) {
        return Language.builder().name(languageName).build();
    }

    @Override
    public Optional<Language> findLanguageByName(String languageName) {
        return languageRepository.findByName(languageName);
    }

    @Override
    public List<Language> findLanguagesByNames(List<String> languagesToFind) {
        List<Language> languagesFound = new ArrayList<>();

        for (String languageName : languagesToFind) {
            Optional<Language> optionalLanguage = findLanguageByName(languageName);
            optionalLanguage.ifPresent(languagesFound::add);
        }
        return languagesFound;
    }

    @Override
    public List<Language> getRemovedLanguages(
            List<String> updatedLanguages, List<Language> currentLanguages) {
        List<Language> removedLanguages = new ArrayList<>();

        for (Language language : currentLanguages) {
            if (!updatedLanguages.contains(language.getName())) {
                removedLanguages.add(language);
            }
        }
        return removedLanguages;
    }

    @Override
    public void deleteUnusedLanguages(List<Language> removedLanguages) {
        for (Language language : removedLanguages) {
            if (projectRepository
                    .findProjectsByLanguageName(language.getName().toUpperCase(Locale.ROOT))
                    .isEmpty()) {
                languageRepository.deleteById(language.getId());
            }
        }
    }

    @Override
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    @Override
    public List<String> getValidLanguages() {
        return validLanguageList;
    }
}
