package com.example.final_pro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

   //tarif
   private static final String TABLE_NAME = "en_fa";

    private static final String COLUMN_EN = "en";
    private static final String COLUMN_FA = "fa";
    private static String db_name = "en_fa.db";
    private static String db_path = "";
    private static int db_ver = 1;

    private SQLiteDatabase database;
    private Context context;
    private boolean needUpdate = false;

    //method sazande class
    public DatabaseHelper(Context context){
        super(context,db_name,null,db_ver);
        if (Build.VERSION.SDK_INT>=17){

            db_path = context.getApplicationInfo().dataDir + "/databases/";
        }else{
            db_path = "/data/data/"+ context.getPackageName()+ "/databases/";
        }
        this.context = context;
        copyDatabase();
        this.getReadableDatabase();

    }//DatabaseHelper

    private void copyDatabase() {
        if (!checkDatabase()){
            this.getReadableDatabase();
            try {
                copyDBFile();
            }catch (IOException e){
                throw new Error("copy error");
            }//catch
        }//if

    }//copyDatabase

    private void copyDBFile() throws  IOException {

        InputStream inputStream = context.getAssets().open(db_name);
        OutputStream outputStream = new FileOutputStream(db_path+db_name);
        byte[] buffer = new byte[1024];
        int length;
        while ((length=inputStream.read(buffer))>0)
            outputStream.write(buffer,0,length);
        outputStream.flush();
        outputStream.close();
        inputStream.close();


    }



    private boolean checkDatabase() {
        File dbFile = new File(db_path+db_name);
        if (dbFile.exists()) return true;
        else
            return false;

    }




    private boolean checkIfTableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?", new String[]{tableName});
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        this.database = db;
    }

    public String getRandomData() {
        String result = "";
        if (checkIfTableExists("idioms")) {
            String query = "SELECT fa, en FROM idioms ORDER BY RANDOM() LIMIT 1";
            Cursor cursor = database.rawQuery(query,null);
            if (cursor != null && cursor.moveToFirst()) {
                String fa = cursor.getString(0);
                String en = cursor.getString(1);
                result = en + "\n \n " + fa;
                cursor.close();
            }
        }
        return result;
    }

    private static final String DATABASE_NAME = "en_fa.db";
    private static final int DATABASE_VERSION = 1;



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

       // database.execSQL(CREATE_TABLE_SAVED);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        if (i1>i)
            needUpdate = true;

    }

    public void updateDatabase() throws IOException{
        if (needUpdate){
            File dbFile = new File(db_path+db_name);
            if (dbFile.exists())
                dbFile.delete();
            copyDatabase();
            needUpdate=false;
        }//if

    }//updateDatabase

    @Override
    public synchronized void close() {
        if (database!=null)
            database.close();
        super.close();
    }

public void deleteRowsWithSharedId(String word) {
    if (!checkIfTableExists("en_fa")) {
        Toast.makeText(context, "Database Error", Toast.LENGTH_SHORT).show();
        return;
    }
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EN + " = ?";
    Cursor cursor = database.rawQuery(query, new String[]{word});
    if (cursor.getCount() == 0) {
        Toast.makeText(context, "There's No Such Word", Toast.LENGTH_SHORT).show();
        return;
    }
    cursor.moveToFirst();
    int firstRowId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
    if (cursor.getCount() > 1) {
        cursor.moveToNext();
        int secondRowId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        database.execSQL("DELETE FROM en_fa WHERE id IN (?, ?)", new Object[] { firstRowId, secondRowId });
    } else {
        database.execSQL("DELETE FROM en_fa WHERE id=?", new Object[] { firstRowId });
    }
    cursor.close();
    Toast.makeText(context, "Done Successfully", Toast.LENGTH_SHORT).show();
}

    public boolean checkIfWordExists(String englishWord) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_EN + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{englishWord});

        boolean wordExists = false;
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            wordExists = (count > 0);
            cursor.close();
        }

        db.close();
        return wordExists;
    }

    public void updatePersianWord(String englishWord, String persianWord){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FA, persianWord);
        db.update(TABLE_NAME, values, COLUMN_EN + " = ?", new String[]{englishWord});
        db.close();
    }

    public void insertNewWord(String englishWord, String persianWord) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EN, englishWord);
        values.put(COLUMN_FA, persianWord);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public ArrayList Get_Data(){
        ArrayList<saved_model> model = new ArrayList<>();

        Cursor cr = database.rawQuery("SELECT id, en, fa FROM en_fa WHERE flg = 1;",null);
        if (cr.getCount()>0){

            cr.moveToFirst();
            do {

                saved_model savedModel = new saved_model();
                //savedModel.setId(cr.getString(cr.getColumnIndexOrThrow("id")));
                savedModel.setEn(cr.getString(cr.getColumnIndexOrThrow("en")));
                savedModel.setFa(cr.getString(cr.getColumnIndexOrThrow("fa")));
                model.add(savedModel);


            }while (cr.moveToNext());
        }

        return model;
    }

    public ArrayList<String> getCombinedValues() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"en", "fa"};
        Cursor cursor = db.query("en_fa", columns, null, null, null, null, null);
        ArrayList<String> combinedValues = new ArrayList<>();

        int i = 0;
        while (cursor.moveToNext() && i < 1234 + 90700) {
            if (i >= 1234) {
            String faValue = cursor.getString(cursor.getColumnIndexOrThrow("fa"));
            String enValue = cursor.getString(cursor.getColumnIndexOrThrow("en"));

            if (faValue != null && enValue != null) {
                //combinedValues.add(i, faValue);
               // combinedValues.add(i + 1, enValue);
                combinedValues.add(faValue);
                combinedValues.add(enValue);
            }}

            i += 1;
        }

        cursor.close();
        return combinedValues;
    }


}





