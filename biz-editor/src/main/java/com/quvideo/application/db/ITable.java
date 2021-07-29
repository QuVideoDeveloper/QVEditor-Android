package com.quvideo.application.db;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.List;

interface ITable<T> {

  long addItem(T paramT);

  void addItems(List<T> paramList);

  T cursorToItem(Cursor paramCursor);

  List<T> getAllItem();

  List<T> getItemsByField(String paramString1, String paramString2);

  ContentValues itemToContentValues(T paramT);

  void removeItem(T paramT);

  void removeItems(List<T> paramList);

  void removeItemsByField(String paramString1, String paramString2);

  void removeItemsByField(String paramString, List<String> paramList);

  void updateItem(T paramT);

  void updateMultipleItems(List<T> paramList);
}
