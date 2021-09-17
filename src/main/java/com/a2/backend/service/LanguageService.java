package com.a2.backend.service;

import com.a2.backend.entity.Language;
import java.util.List;
import java.util.Optional;

public interface LanguageService {

    List<Language> findOrCreateLanguage(List<String> languagesToAdd);

    Language createLanguage(String languageName);

    Optional<Language> findLanguageByName(String languageName);

    List<Language> findLanguagesByNames(List<String> languagesToFind);

    List<Language> getRemovedLanguages(List<String> updated, List<Language> current);

    void deleteUnusedLanguages(List<Language> removedLanguages);

    List<Language> getAllLanguages();

    List<String> getValidLanguages();
}
