package com.example.accountastock_android.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceControl;

import androidx.annotation.RequiresApi;

import com.example.accountastock_android.data.model.Expense;
import com.example.accountastock_android.data.model.Object;
import com.example.accountastock_android.data.model.Profit;
import com.example.accountastock_android.data.model.Transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 4;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OBJECT_TABLE = "CREATE TABLE object (" +
                "id_object INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "note TEXT, " +
                "fk_id_user INTEGER, " +
                "quantity INTEGER)";

        String CREATE_EXPENSE_TABLE = "CREATE TABLE expense (" +
                "id_expense INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "note TEXT, " +
                "amount REAL, " +
                "tax_percent REAL, " +
                "fk_id_user INTEGER, " +
                "date DATE DEFAULT (strftime('%Y-%m-%d', 'now')))";

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
                "date DATE DEFAULT (strftime('%Y-%m-%d', 'now'))," +
                "fk_id_profit INTEGER, " +
                "fk_id_expense INTEGER, " +
                "fk_id_user INTEGER, " +
                "FOREIGN KEY (fk_id_profit) REFERENCES profit(id_profit), " +
                "FOREIGN KEY (fk_id_expense) REFERENCES expense(id_expense), " +
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean addExpense(String title, String note, double amount, int taxPercent, int userId) {
        boolean returnValue = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("amount", amount);
        values.put("tax_percent", taxPercent);
        values.put("fk_id_user", userId);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        values.put("date", date);

        // Insérer l'expense et obtenir l'ID généré
        long expenseId = db.insert("expense", null, values);

        // Vérifier si l'insertion a réussi
        if (expenseId != -1) {
            // Ajouter une transaction en utilisant l'ID de l'expense
            addTransaction(title, note, amount, date, null, (int) expenseId, userId);
            returnValue = true;
        }

        return returnValue;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean addProfit(String title, String note, double amount, int userId) {
        boolean returnValue = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("amount", amount);
        values.put("fk_id_user", userId);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        values.put("date", date);

        // Insérer l'expense et obtenir l'ID généré
        long profitId = db.insert("profit", null, values);

        // Vérifier si l'insertion a réussi
        if (profitId != -1) {
            // Ajouter une transaction en utilisant l'ID de l'expense
            addTransaction(title, note, amount, date, (int) profitId, null, userId);
            returnValue = true;
        }
        return returnValue;
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

    public void addTransaction(String title, String note, double amount, String date, Integer fkIdProfit, Integer fkIdExpense, Integer fkIdUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("note", note);
        values.put("amount", amount);
        values.put("date", date);
        if (fkIdProfit != null) {
            values.put("fk_id_profit", fkIdProfit);
        }
        if (fkIdExpense != null) {
            values.put("fk_id_expense", fkIdExpense);
        }
        if (fkIdUser != null) {  // Make sure fk_id_user is added
            values.put("fk_id_user", fkIdUser);
        }
        db.insert("`transaction`", null, values);
        db.close();
    }

    public List<Transaction> getTransactionsLast30Days() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM `transaction` WHERE date >= date('now', '-30 days')";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int idTransaction = cursor.getInt(cursor.getColumnIndexOrThrow("id_transaction"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                Integer fkIdProfit = cursor.isNull(cursor.getColumnIndexOrThrow("fk_id_profit")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("fk_id_profit"));
                Integer fkIdExpense = cursor.isNull(cursor.getColumnIndexOrThrow("fk_id_expense")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("fk_id_expense"));
                Integer fkIdUser = cursor.isNull(cursor.getColumnIndexOrThrow("fk_id_user")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("fk_id_user"));

                Transaction transaction = new Transaction(idTransaction, title, note, amount, date, fkIdProfit, fkIdExpense, fkIdUser);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Log the transactions to verify
        for (Transaction transaction : transactions) {
            Log.d("Transaction DB", transaction.toString());
        }

        return transactions;
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
}
