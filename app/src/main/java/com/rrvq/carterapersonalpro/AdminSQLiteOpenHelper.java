package com.rrvq.carterapersonalpro;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase baseDeDatos) {
        baseDeDatos.execSQL("create table gasto(row_cuenta int, row_icon_gasto int," +
                "fecha_gasto text, desc_gasto text, monto_gasto real)");

        baseDeDatos.execSQL("create table ingreso(row_cuenta int, row_icon_ingreso int," +
                "fecha_ingreso text, desc_ingreso text, monto_ingreso real)");

        baseDeDatos.execSQL("create table icono_gasto(icon_gasto int, inombre_gasto text)");

        baseDeDatos.execSQL("create table icono_ingreso(icon_ingreso int, inombre_ingreso text)");

        baseDeDatos.execSQL("create table cuenta(nombre_cuenta text, icon_cuenta int," +
                " icon_spinner int, disponible_cuenta real)");

        baseDeDatos.execSQL("create table transferencia(fecha_transf text, monto_transf real," +
                " desc_transf text, desde_row int, hasta_row int)");

        baseDeDatos.execSQL("create table preferencias(row_cuenta int, tipo_moneda text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    //parallenar el spinner con sqlite
    public List<String> getAllLabels() {
        List<String> list = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM cuenta";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return list;
    }


}
