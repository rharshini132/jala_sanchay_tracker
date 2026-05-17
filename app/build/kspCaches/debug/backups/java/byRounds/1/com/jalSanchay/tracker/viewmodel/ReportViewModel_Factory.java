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
public final class ReportViewModel_Factory implements Factory<ReportViewModel> {
  private final Provider<JalRepository> repositoryProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public ReportViewModel_Factory(Provider<JalRepository> repositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.repositoryProvider = repositoryProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public ReportViewModel get() {
    return newInstance(repositoryProvider.get(), userPreferencesProvider.get());
  }

  public static ReportViewModel_Factory create(Provider<JalRepository> repositoryProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new ReportViewModel_Factory(repositoryProvider, userPreferencesProvider);
  }

  public static ReportViewModel newInstance(JalRepository repository,
      UserPreferences userPreferences) {
    return new ReportViewModel(repository, userPreferences);
  }
}
