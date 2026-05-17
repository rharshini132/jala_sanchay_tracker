package com.jalSanchay.tracker.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.jalSanchay.tracker.data.model.TankSetup;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TankSetupDao_Impl implements TankSetupDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TankSetup> __insertionAdapterOfTankSetup;

  private final SharedSQLiteStatement __preparedStmtOfUpdateWaterLevel;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByUser;

  public TankSetupDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTankSetup = new EntityInsertionAdapter<TankSetup>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tank_setup` (`id`,`userId`,`roofAreaM2`,`roofMaterial`,`runoffCoefficient`,`tankCapacityLiters`,`tankMaterial`,`currentWaterLevelLiters`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TankSetup entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindDouble(3, entity.getRoofAreaM2());
        statement.bindString(4, entity.getRoofMaterial());
        statement.bindDouble(5, entity.getRunoffCoefficient());
        statement.bindDouble(6, entity.getTankCapacityLiters());
        statement.bindString(7, entity.getTankMaterial());
        statement.bindDouble(8, entity.getCurrentWaterLevelLiters());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfUpdateWaterLevel = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tank_setup SET currentWaterLevelLiters = ?, updatedAt = ? WHERE userId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tank_setup WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final TankSetup setup,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTankSetup.insert(setup);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWaterLevel(final int userId, final float level, final long time,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateWaterLevel.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, level);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, time);
        _argIndex = 3;
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
          __preparedStmtOfUpdateWaterLevel.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByUser(final int userId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByUser.acquire();
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
          __preparedStmtOfDeleteByUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getSetupByUser(final int userId,
      final Continuation<? super TankSetup> $completion) {
    final String _sql = "SELECT * FROM tank_setup WHERE userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TankSetup>() {
      @Override
      @Nullable
      public TankSetup call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfRoofAreaM2 = CursorUtil.getColumnIndexOrThrow(_cursor, "roofAreaM2");
          final int _cursorIndexOfRoofMaterial = CursorUtil.getColumnIndexOrThrow(_cursor, "roofMaterial");
          final int _cursorIndexOfRunoffCoefficient = CursorUtil.getColumnIndexOrThrow(_cursor, "runoffCoefficient");
          final int _cursorIndexOfTankCapacityLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "tankCapacityLiters");
          final int _cursorIndexOfTankMaterial = CursorUtil.getColumnIndexOrThrow(_cursor, "tankMaterial");
          final int _cursorIndexOfCurrentWaterLevelLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "currentWaterLevelLiters");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final TankSetup _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final float _tmpRoofAreaM2;
            _tmpRoofAreaM2 = _cursor.getFloat(_cursorIndexOfRoofAreaM2);
            final String _tmpRoofMaterial;
            _tmpRoofMaterial = _cursor.getString(_cursorIndexOfRoofMaterial);
            final float _tmpRunoffCoefficient;
            _tmpRunoffCoefficient = _cursor.getFloat(_cursorIndexOfRunoffCoefficient);
            final float _tmpTankCapacityLiters;
            _tmpTankCapacityLiters = _cursor.getFloat(_cursorIndexOfTankCapacityLiters);
            final String _tmpTankMaterial;
            _tmpTankMaterial = _cursor.getString(_cursorIndexOfTankMaterial);
            final float _tmpCurrentWaterLevelLiters;
            _tmpCurrentWaterLevelLiters = _cursor.getFloat(_cursorIndexOfCurrentWaterLevelLiters);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new TankSetup(_tmpId,_tmpUserId,_tmpRoofAreaM2,_tmpRoofMaterial,_tmpRunoffCoefficient,_tmpTankCapacityLiters,_tmpTankMaterial,_tmpCurrentWaterLevelLiters,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
