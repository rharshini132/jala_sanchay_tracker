package com.jalSanchay.tracker.viewmodel;

import com.jalSanchay.tracker.data.datastore.UserPreferences;
import com.jalSanchay.tracker.data.repository.JalRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<JalRepository> repositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public HistoryViewModel_Factory(Provider<JalRepository> repositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.repositoryProvider = repositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(repositoryProvider.get(), userPreferencesProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<JalRepository> repositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new HistoryViewModel_Factory(repositoryProvider, userPreferencesProvider);
  }

  public static HistoryViewModel newInstance(JalRepository repository,
      UserPreferences userPreferences) {
    return new HistoryViewModel(repository, userPreferences);
  }
}
