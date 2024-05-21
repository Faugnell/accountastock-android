package com.example.accountastock_android.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.view.SurfaceControl;

import androidx.annotation.RequiresApi;

import com.example.accountastock_android.data.model.Expense;
import com.example.accountastock_android.data.model.Object;
import com.example.accountastock_android.data.model.Profit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPENSE_TABLE = "CREATE TABLE expense (" +
                "id_expense INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "note TEXT, " +
                "amount REAL, " +
                "tax_percent REAL, " +
                "fk_id_user INTEGER, " +
                "date DATE DEFAULT (strftime('%Y-%m-%d', 'now')))";

        String CREATE_OBJECT_TABLE = "CREATE TABLE object (" +
                "id_object INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "note TEXT, " +
                "fk_id_user INTEGER, " +
                "quantity INTEGER)";

        String CREATE_PROFIT_TABLE = "CREATE TABLE profit (" +
                "id_profit INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "note TEXT, " +
                "amount REAL, " +
                "fk_id_user INTEGER, " +
                "date DATE DEFAULT (strftime('%Y-%m-%d', 'now')))";

        String CREATE_TRANSACTION_TABLE = "CREATE TABLE `transaction` (" +
                "id_transaction INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "note TEXT, " +
                "amount REAL, " +
                "fk_id_profit INTEGER, " +
                "fk_id_expense INTEGER, " +
                "FOREIGN KEY (fk_id_profit) REFERENCES profit(id_profit), " +
                "FOREIGN KEY (fk_id_expense) REFERENCES expense(id_expense)" +
                ")";


        db.execSQL(CREATE_EXPENSE_TABLE);
        db.execSQL(CREATE_OBJECT_TABLE);
        db.execSQL(CREATE_PROFIT_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expense");
        db.execSQL("DROP TABLE IF EXISTS object");
        db.execSQL("DROP TABLE IF EXISTS profit");
        db.execSQL("DROP TABLE IF EXISTS `transaction`");
        onCreate(db);
    }

    // Méthodes pour insérer des données dans les nouvelles tables
    public void addExpense(String title, String note, double amount, int taxPercent, int userId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("amount", amount);
        values.put("tax_percent", taxPercent);
        values.put("fk_id_user", userId);
        values.put("date", date);
        db.insert("expense", null, values);
        db.close();
    }

    public void addObject(String title, String note, int userId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("fk_id_user", userId);
        values.put("quantity", quantity);
        db.insert("object", null, values);
        db.close();
    }

    public void addProfit(String title, String note, double amount, int userId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("amount", amount);
        values.put("fk_id_user", userId);
        values.put("date", date);
        db.insert("profit", null, values);
        db.close();
    }

    public void addTransaction(String title, String note, double amount, int fkIdProfit, int fkIdExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("amount", amount);
        values.put("fk_id_profit", fkIdProfit);
        values.put("fk_id_expense", fkIdExpense);
        db.insert("transaction", null, values);
        db.close();
    }

    public List<Object> getAllObjects() {
        List<Object> objects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM object", null);

        if (cursor.moveToFirst()) {
            do {
                Object object = new Object();
                object.setId(cursor.getInt(0));
                object.setTitle(cursor.getString(1));
                object.setNote(cursor.getString(2));
                object.setUserId(cursor.getInt(3));
                object.setQuantity(cursor.getInt(4));
                objects.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return objects;
    }

    public List<Profit> getAllProfits() {
        List<Profit> profits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM profit", null);

        if (cursor.moveToFirst()) {
            do {
                Profit profit = new Profit();
                profit.setId(cursor.getInt(0));
                profit.setTitle(cursor.getString(1));
                profit.setNote(cursor.getString(2));
                profit.setAmount(cursor.getDouble(3));
                profit.setUserId(cursor.getInt(4));
                profit.setDate(cursor.getString(5));
                profits.add(profit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return profits;
    }

    // Méthode pour calculer les profits nets des 30 derniers jours
    public double getNetProfitLast30Days() {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryExpenses = "SELECT SUM(amount) FROM expense WHERE date >= date('now', '-30 days')";
        String queryProfits = "SELECT SUM(amount) FROM profit WHERE date >= date('now', '-30 days')";

        double totalExpenses = 0;
        double totalProfits = 0;

        Cursor cursorExpenses = db.rawQuery(queryExpenses, null);
        if (cursorExpenses.moveToFirst()) {
            totalExpenses = cursorExpenses.getDouble(0);
        }
        cursorExpenses.close();

        Cursor cursorProfits = db.rawQuery(queryProfits, null);
        if (cursorProfits.moveToFirst()) {
            totalProfits = cursorProfits.getDouble(0);
        }
        cursorProfits.close();

        db.close();
        return totalProfits - totalExpenses;
    }

    public double getProfitPercentageLast30Days() {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryFirstDay = "SELECT amount FROM profit WHERE date = date('now', '-30 days')";
        String queryLastDay = "SELECT amount FROM profit WHERE date = date('now')";

        double firstDayAmount = 0;
        double lastDayAmount = 0;

        Cursor cursorFirstDay = db.rawQuery(queryFirstDay, null);
        if (cursorFirstDay.moveToFirst()) {
            firstDayAmount = cursorFirstDay.getDouble(0);
        }
        cursorFirstDay.close();

        Cursor cursorLastDay = db.rawQuery(queryLastDay, null);
        if (cursorLastDay.moveToFirst()) {
            lastDayAmount = cursorLastDay.getDouble(0);
        }
        cursorLastDay.close();

        db.close();

        // Calcul du pourcentage de gain
        double profitPercentage = ((lastDayAmount - firstDayAmount) / firstDayAmount) * 100;
        return profitPercentage;
    }

    public Pair<Double, Double> getProfitAndExpenseForDay(int day) {
        double profit = 0;
        double expense = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        // Exécutez votre requête SQL pour obtenir les profits et les dépenses pour le jour donné
        Cursor cursor = db.rawQuery("SELECT SUM(profit_column_name), SUM(expense_column_name) " +
                "FROM your_table_name " +
                "WHERE day_column_name = ?", new String[]{String.valueOf(day)});

        if (cursor.moveToFirst()) {
            profit = cursor.getDouble(0);
            expense = cursor.getDouble(1);
        }

        cursor.close();
        db.close();

        return new Pair<>(profit, expense);
    }
}
