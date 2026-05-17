package com.jalSanchay.tracker.di;

import com.jalSanchay.tracker.data.db.AppDatabase;
import com.jalSanchay.tracker.data.db.TankSetupDao;
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
public final class AppModule_ProvideTankSetupDaoFactory implements Factory<TankSetupDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideTankSetupDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TankSetupDao get() {
    return provideTankSetupDao(dbProvider.get());
  }

  public static AppModule_ProvideTankSetupDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideTankSetupDaoFactory(dbProvider);
  }

  public static TankSetupDao provideTankSetupDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTankSetupDao(db));
  }
}
