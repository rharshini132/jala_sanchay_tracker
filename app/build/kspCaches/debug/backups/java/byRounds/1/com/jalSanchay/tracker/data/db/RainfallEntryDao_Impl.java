package com.jalSanchay.tracker.data.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.jalSanchay.tracker.data.model.RainfallEntry;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RainfallEntryDao_Impl implements RainfallEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RainfallEntry> __insertionAdapterOfRainfallEntry;

  private final EntityDeletionOrUpdateAdapter<RainfallEntry> __deletionAdapterOfRainfallEntry;

  private final EntityDeletionOrUpdateAdapter<RainfallEntry> __updateAdapterOfRainfallEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllEntriesForUser;

  public RainfallEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRainfallEntry = new EntityInsertionAdapter<RainfallEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `rainfall_entries` (`id`,`userId`,`date`,`rainfallMm`,`litersHarvested`,`tankLevelAfterLiters`,`source`,`notes`,`roofAreaUsed`,`runoffUsed`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RainfallEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDate());
        statement.bindDouble(4, entity.getRainfallMm());
        statement.bindDouble(5, entity.getLitersHarvested());
        statement.bindDouble(6, entity.getTankLevelAfterLiters());
        statement.bindString(7, entity.getSource());
        statement.bindString(8, entity.getNotes());
        statement.bindDouble(9, entity.getRoofAreaUsed());
        statement.bindDouble(10, entity.getRunoffUsed());
        statement.bindLong(11, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfRainfallEntry = new EntityDeletionOrUpdateAdapter<RainfallEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `rainfall_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RainfallEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRainfallEntry = new EntityDeletionOrUpdateAdapter<RainfallEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `rainfall_entries` SET `id` = ?,`userId` = ?,`date` = ?,`rainfallMm` = ?,`litersHarvested` = ?,`tankLevelAfterLiters` = ?,`source` = ?,`notes` = ?,`roofAreaUsed` = ?,`runoffUsed` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RainfallEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDate());
        statement.bindDouble(4, entity.getRainfallMm());
        statement.bindDouble(5, entity.getLitersHarvested());
        statement.bindDouble(6, entity.getTankLevelAfterLiters());
        statement.bindString(7, entity.getSource());
        statement.bindString(8, entity.getNotes());
        statement.bindDouble(9, entity.getRoofAreaUsed());
        statement.bindDouble(10, entity.getRunoffUsed());
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllEntriesForUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM rainfall_entries WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertEntry(final RainfallEntry entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRainfallEntry.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEntry(final RainfallEntry entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRainfallEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEntry(final RainfallEntry entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRainfallEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllEntriesForUser(final int userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllEntriesForUser.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllEntriesForUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RainfallEntry>> getAllEntries(final int userId) {
    final String _sql = "SELECT * FROM rainfall_entries WHERE userId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rainfall_entries"}, new Callable<List<RainfallEntry>>() {
      @Override
      @NonNull
      public List<RainfallEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRainfallMm = CursorUtil.getColumnIndexOrThrow(_cursor, "rainfallMm");
          final int _cursorIndexOfLitersHarvested = CursorUtil.getColumnIndexOrThrow(_cursor, "litersHarvested");
          final int _cursorIndexOfTankLevelAfterLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "tankLevelAfterLiters");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfRoofAreaUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "roofAreaUsed");
          final int _cursorIndexOfRunoffUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "runoffUsed");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<RainfallEntry> _result = new ArrayList<RainfallEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RainfallEntry _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final float _tmpRainfallMm;
            _tmpRainfallMm = _cursor.getFloat(_cursorIndexOfRainfallMm);
            final float _tmpLitersHarvested;
            _tmpLitersHarvested = _cursor.getFloat(_cursorIndexOfLitersHarvested);
            final float _tmpTankLevelAfterLiters;
            _tmpTankLevelAfterLiters = _cursor.getFloat(_cursorIndexOfTankLevelAfterLiters);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final float _tmpRoofAreaUsed;
            _tmpRoofAreaUsed = _cursor.getFloat(_cursorIndexOfRoofAreaUsed);
            final float _tmpRunoffUsed;
            _tmpRunoffUsed = _cursor.getFloat(_cursorIndexOfRunoffUsed);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new RainfallEntry(_tmpId,_tmpUserId,_tmpDate,_tmpRainfallMm,_tmpLitersHarvested,_tmpTankLevelAfterLiters,_tmpSource,_tmpNotes,_tmpRoofAreaUsed,_tmpRunoffUsed,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<RainfallEntry>> getRecentEntries(final int userId) {
    final String _sql = "SELECT * FROM rainfall_entries WHERE userId = ? ORDER BY date DESC LIMIT 3";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rainfall_entries"}, new Callable<List<RainfallEntry>>() {
      @Override
      @NonNull
      public List<RainfallEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRainfallMm = CursorUtil.getColumnIndexOrThrow(_cursor, "rainfallMm");
          final int _cursorIndexOfLitersHarvested = CursorUtil.getColumnIndexOrThrow(_cursor, "litersHarvested");
          final int _cursorIndexOfTankLevelAfterLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "tankLevelAfterLiters");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfRoofAreaUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "roofAreaUsed");
          final int _cursorIndexOfRunoffUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "runoffUsed");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<RainfallEntry> _result = new ArrayList<RainfallEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RainfallEntry _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final float _tmpRainfallMm;
            _tmpRainfallMm = _cursor.getFloat(_cursorIndexOfRainfallMm);
            final float _tmpLitersHarvested;
            _tmpLitersHarvested = _cursor.getFloat(_cursorIndexOfLitersHarvested);
            final float _tmpTankLevelAfterLiters;
            _tmpTankLevelAfterLiters = _cursor.getFloat(_cursorIndexOfTankLevelAfterLiters);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final float _tmpRoofAreaUsed;
            _tmpRoofAreaUsed = _cursor.getFloat(_cursorIndexOfRoofAreaUsed);
            final float _tmpRunoffUsed;
            _tmpRunoffUsed = _cursor.getFloat(_cursorIndexOfRunoffUsed);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new RainfallEntry(_tmpId,_tmpUserId,_tmpDate,_tmpRainfallMm,_tmpLitersHarvested,_tmpTankLevelAfterLiters,_tmpSource,_tmpNotes,_tmpRoofAreaUsed,_tmpRunoffUsed,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<RainfallEntry>> getEntriesByDateRange(final int userId, final String from,
      final String to) {
    final String _sql = "SELECT * FROM rainfall_entries WHERE userId = ? AND date BETWEEN ? AND ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, from);
    _argIndex = 3;
    _statement.bindString(_argIndex, to);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rainfall_entries"}, new Callable<List<RainfallEntry>>() {
      @Override
      @NonNull
      public List<RainfallEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRainfallMm = CursorUtil.getColumnIndexOrThrow(_cursor, "rainfallMm");
          final int _cursorIndexOfLitersHarvested = CursorUtil.getColumnIndexOrThrow(_cursor, "litersHarvested");
          final int _cursorIndexOfTankLevelAfterLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "tankLevelAfterLiters");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfRoofAreaUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "roofAreaUsed");
          final int _cursorIndexOfRunoffUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "runoffUsed");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<RainfallEntry> _result = new ArrayList<RainfallEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RainfallEntry _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final float _tmpRainfallMm;
            _tmpRainfallMm = _cursor.getFloat(_cursorIndexOfRainfallMm);
            final float _tmpLitersHarvested;
            _tmpLitersHarvested = _cursor.getFloat(_cursorIndexOfLitersHarvested);
            final float _tmpTankLevelAfterLiters;
            _tmpTankLevelAfterLiters = _cursor.getFloat(_cursorIndexOfTankLevelAfterLiters);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final float _tmpRoofAreaUsed;
            _tmpRoofAreaUsed = _cursor.getFloat(_cursorIndexOfRoofAreaUsed);
            final float _tmpRunoffUsed;
            _tmpRunoffUsed = _cursor.getFloat(_cursorIndexOfRunoffUsed);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new RainfallEntry(_tmpId,_tmpUserId,_tmpDate,_tmpRainfallMm,_tmpLitersHarvested,_tmpTankLevelAfterLiters,_tmpSource,_tmpNotes,_tmpRoofAreaUsed,_tmpRunoffUsed,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalHarvest(final int userId) {
    final String _sql = "SELECT SUM(litersHarvested) FROM rainfall_entries WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rainfall_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalRainfall(final int userId) {
    final String _sql = "SELECT SUM(rainfallMm) FROM rainfall_entries WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rainfall_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getMonthlyHarvest(final int userId, final String monthPrefix) {
    final String _sql = "SELECT SUM(litersHarvested) FROM rainfall_entries WHERE userId = ? AND date LIKE ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, monthPrefix);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rainfall_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getEntryCount(final int userId) {
    final String _sql = "SELECT COUNT(*) FROM rainfall_entries WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rainfall_entries"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
