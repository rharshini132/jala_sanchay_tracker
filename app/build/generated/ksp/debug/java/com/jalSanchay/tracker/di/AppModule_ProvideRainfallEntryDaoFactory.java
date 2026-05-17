package com.jalSanchay.tracker.di;

import com.jalSanchay.tracker.data.db.AppDatabase;
import com.jalSanchay.tracker.data.db.RainfallEntryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideRainfallEntryDaoFactory implements Factory<RainfallEntryDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideRainfallEntryDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RainfallEntryDao get() {
    return provideRainfallEntryDao(dbProvider.get());
  }

  public static AppModule_ProvideRainfallEntryDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideRainfallEntryDaoFactory(dbProvider);
  }

  public static RainfallEntryDao provideRainfallEntryDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRainfallEntryDao(db));
  }
}
