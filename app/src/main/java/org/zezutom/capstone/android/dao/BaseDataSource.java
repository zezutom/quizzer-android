package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDataSource<T extends Object, H extends BaseDataHelper> {

    private SQLiteDatabase db;

    private H helper;

    public BaseDataSource(H helper) {
        this.helper = helper;
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }


    protected abstract ContentValues getValues(T entity);

    protected abstract T getEntity(Cursor cursor);

    public void addOne(T entity) {
        getDb().insert(helper.getTableName(), null, getValues(entity));
    }

    public void addAll(List<T> entities) {
        for (T entity : entities) {
            addOne(entity);
        }
    }

    public List<T> getAll() {
        List<T> entities = new ArrayList<>();
        Cursor cursor = getDb().query(helper.getTableName(), helper.getColumns(),
                null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            entities.add(getEntity(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return entities;

    }

    private SQLiteDatabase getDb() {
        if (db == null) open();
        return db;
    }

}
