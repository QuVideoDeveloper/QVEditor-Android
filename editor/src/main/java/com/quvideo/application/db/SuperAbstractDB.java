package com.quvideo.application.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 抽象类
 */
abstract class SuperAbstractDB<T> implements ITable<T> {
  protected SQLiteDatabase sdb;

  public SuperAbstractDB() {
    this.sdb = DraftDBHelper.getInstance().getWritableDatabase();
  }

  protected void beginTransaction() {
    sdb.beginTransaction();
  }

  protected void setTransactionSuccessful() {
    sdb.setTransactionSuccessful();
  }

  protected void endTransaction() {
    sdb.endTransaction();
  }

  @Override
  public long addItem(T paramT) {
    return sdb.replace(getTableName(), null, itemToContentValues(paramT));
  }

  @Override
  public void addItems(List<T> paramList) {
    try {
      beginTransaction();
      Iterator<T> localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        addItem(localIterator.next());
      }
      setTransactionSuccessful();
    } catch (Throwable ignore) {
    } finally {
      endTransaction();
    }
  }

  @Override
  public List<T> getAllItem() {
    return getResultFromCursor(sdb.query(getTableName(), null, null, null, null, null, null));
  }

  protected List<T> getResultFromCursor(Cursor paramCursor) {
    ArrayList<T> localArrayList = new ArrayList<T>();
    while (paramCursor.moveToNext()) {
      localArrayList.add(cursorToItem(paramCursor));
    }
    paramCursor.close();
    return localArrayList;
  }

  protected abstract String getTableName();

  @Override
  public List<T> getItemsByField(String columnName, String value) {
    String str = columnName + "=?";
    String[] arrayOfString = new String[1];
    arrayOfString[0] = value;
    return getResultFromCursor(sdb.query(getTableName(), null, str, arrayOfString, null, null, null));
  }

  @Override public void removeItems(List<T> paramList) {
    try {
      beginTransaction();
      Iterator<T> localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        removeItem(localIterator.next());
      }
      setTransactionSuccessful();
    } catch (Throwable ignore) {
    } finally {
      endTransaction();
    }
  }

  @Override
  public void removeItemsByField(String paramString1, String paramString2) {
    String str = paramString1 + "=?";
    String[] arrayOfString = new String[1];
    arrayOfString[0] = paramString2;
    sdb.delete(getTableName(), str, arrayOfString);
  }

  @Override
  public void removeItemsByField(String paramString, List<String> paramList) {
    try {
      beginTransaction();
      Iterator<String> localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        removeItemsByField(paramString, localIterator.next());
      }
      setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      endTransaction();
    }
  }

  protected void updateItemByField(String columnName, String value, ContentValues paramContentValues) {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = value;
    sdb.update(getTableName(), paramContentValues, columnName + "=?", arrayOfString);
  }

  @Override
  public void updateMultipleItems(List<T> paramList) {
    try {
      beginTransaction();
      Iterator<T> localIterator = paramList.iterator();
      while (localIterator.hasNext()) {
        updateItem(localIterator.next());
      }
      setTransactionSuccessful();
    } catch (Throwable ignore) {
    } finally {
      endTransaction();
    }
  }

  public static class ItemProperty {
    public final int ordinal;
    final Class<?> type;
    final boolean primaryKey;
    public final String columnName;

    public ItemProperty(int ordinal, Class<?> type, boolean primaryKey, String columnName) {
      this.ordinal = ordinal;
      this.type = type;
      this.primaryKey = primaryKey;
      this.columnName = columnName;
    }
  }
}
