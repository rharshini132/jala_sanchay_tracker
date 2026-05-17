package com.jalSanchay.tracker.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile TankSetupDao _tankSetupDao;

  private volatile RainfallEntryDao _rainfallEntryDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `city` TEXT NOT NULL, `householdSize` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tank_setup` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `roofAreaM2` REAL NOT NULL, `roofMaterial` TEXT NOT NULL, `runoffCoefficient` REAL NOT NULL, `tankCapacityLiters` REAL NOT NULL, `tankMaterial` TEXT NOT NULL, `currentWaterLevelLiters` REAL NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rainfall_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `date` TEXT NOT NULL, `rainfallMm` REAL NOT NULL, `litersHarvested` REAL NOT NULL, `tankLevelAfterLiters` REAL NOT NULL, `source` TEXT NOT NULL, `notes` TEXT NOT NULL, `roofAreaUsed` REAL NOT NULL, `runoffUsed` REAL NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0aed3fa8cb9004a8190ac5af1ab7b2ec')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `tank_setup`");
        db.execSQL("DROP TABLE IF EXISTS `rainfall_entries`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(7);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("passwordHash", new TableInfo.Column("passwordHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("city", new TableInfo.Column("city", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("householdSize", new TableInfo.Column("householdSize", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.jalSanchay.tracker.data.model.User).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsTankSetup = new HashMap<String, TableInfo.Column>(9);
        _columnsTankSetup.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("roofAreaM2", new TableInfo.Column("roofAreaM2", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("roofMaterial", new TableInfo.Column("roofMaterial", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("runoffCoefficient", new TableInfo.Column("runoffCoefficient", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("tankCapacityLiters", new TableInfo.Column("tankCapacityLiters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("tankMaterial", new TableInfo.Column("tankMaterial", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("currentWaterLevelLiters", new TableInfo.Column("currentWaterLevelLiters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTankSetup.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTankSetup = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTankSetup = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTankSetup = new TableInfo("tank_setup", _columnsTankSetup, _foreignKeysTankSetup, _indicesTankSetup);
        final TableInfo _existingTankSetup = TableInfo.read(db, "tank_setup");
        if (!_infoTankSetup.equals(_existingTankSetup)) {
          return new RoomOpenHelper.ValidationResult(false, "tank_setup(com.jalSanchay.tracker.data.model.TankSetup).\n"
                  + " Expected:\n" + _infoTankSetup + "\n"
                  + " Found:\n" + _existingTankSetup);
        }
        final HashMap<String, TableInfo.Column> _columnsRainfallEntries = new HashMap<String, TableInfo.Column>(11);
        _columnsRainfallEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("rainfallMm", new TableInfo.Column("rainfallMm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("litersHarvested", new TableInfo.Column("litersHarvested", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("tankLevelAfterLiters", new TableInfo.Column("tankLevelAfterLiters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("roofAreaUsed", new TableInfo.Column("roofAreaUsed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("runoffUsed", new TableInfo.Column("runoffUsed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRainfallEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRainfallEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRainfallEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRainfallEntries = new TableInfo("rainfall_entries", _columnsRainfallEntries, _foreignKeysRainfallEntries, _indicesRainfallEntries);
        final TableInfo _existingRainfallEntries = TableInfo.read(db, "rainfall_entries");
        if (!_infoRainfallEntries.equals(_existingRainfallEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "rainfall_entries(com.jalSanchay.tracker.data.model.RainfallEntry).\n"
                  + " Expected:\n" + _infoRainfallEntries + "\n"
                  + " Found:\n" + _existingRainfallEntries);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0aed3fa8cb9004a8190ac5af1ab7b2ec", "d561667e8b02134c82a4f2f4078e2d85");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","tank_setup","rainfall_entries");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `tank_setup`");
      _db.execSQL("DELETE FROM `rainfall_entries`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TankSetupDao.class, TankSetupDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RainfallEntryDao.class, RainfallEntryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public TankSetupDao tankSetupDao() {
    if (_tankSetupDao != null) {
      return _tankSetupDao;
    } else {
      synchronized(this) {
        if(_tankSetupDao == null) {
          _tankSetupDao = new TankSetupDao_Impl(this);
        }
        return _tankSetupDao;
      }
    }
  }

  @Override
  public RainfallEntryDao rainfallEntryDao() {
    if (_rainfallEntryDao != null) {
      return _rainfallEntryDao;
    } else {
      synchronized(this) {
        if(_rainfallEntryDao == null) {
          _rainfallEntryDao = new RainfallEntryDao_Impl(this);
        }
        return _rainfallEntryDao;
      }
    }
  }
}
