package com.jalSanchay.tracker.data.repository;

import com.jalSanchay.tracker.data.db.RainfallEntryDao;
import com.jalSanchay.tracker.data.db.TankSetupDao;
import com.jalSanchay.tracker.data.db.UserDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class JalRepository_Factory implements Factory<JalRepository> {
  private final Provider<UserDao> userDaoProvider;

  private final Provider<TankSetupDao> tankSetupDaoProvider;

  private final Provider<RainfallEntryDao> rainfallEntryDaoProvider;

  public JalRepository_Factory(Provider<UserDao> userDaoProvider,
      Provider<TankSetupDao> tankSetupDaoProvider,
      Provider<RainfallEntryDao> rainfallEntryDaoProvider) {
    this.userDaoProvider = userDaoProvider;
    this.tankSetupDaoProvider = tankSetupDaoProvider;
    this.rainfallEntryDaoProvider = rainfallEntryDaoProvider;
  }

  @Override
  public JalRepository get() {
    return newInstance(userDaoProvider.get(), tankSetupDaoProvider.get(), rainfallEntryDaoProvider.get());
  }

  public static JalRepository_Factory create(Provider<UserDao> userDaoProvider,
      Provider<TankSetupDao> tankSetupDaoProvider,
      Provider<RainfallEntryDao> rainfallEntryDaoProvider) {
    return new JalRepository_Factory(userDaoProvider, tankSetupDaoProvider, rainfallEntryDaoProvider);
  }

  public static JalRepository newInstance(UserDao userDao, TankSetupDao tankSetupDao,
      RainfallEntryDao rainfallEntryDao) {
    return new JalRepository(userDao, tankSetupDao, rainfallEntryDao);
  }
}
