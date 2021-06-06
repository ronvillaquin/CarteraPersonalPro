package com.rrvq.carterapersonalpro;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Bienvenida extends AppCompatActivity {

    String nombreDB = "BDCartera";

    //la declaro global para poder compararla al crear cuenta
    File currentDB;
    int permiso = 0;

    int[] icono_gasto = {R.drawable.ca36, R.drawable.ca23, R.drawable.ca6, R.drawable.ca5, R.drawable.ca22, R.drawable.ca2,
            R.drawable.ca3, R.drawable.ca40, R.drawable.ca8, R.drawable.ca31, R.drawable.ca69, R.drawable.ca44};

    int[] icono_ingreso = {R.drawable.ca29, R.drawable.ca1, R.drawable.ca30, R.drawable.ca0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        //permisos para escribir en la sd del celular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            permisoUsuarioGuardado();
        }


    }

    public void btnCancelar(View vista) {
        finish();
    }

    public void btnCrear(View vista) {

        //permisos para escribir en la sd del celular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            permisoUsuarioGuardado();
        }


        //*********************** DIALOGO PARA ELIMINAR  *********************************/
        final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getResources().getString(R.string.restaurardatos));

        dialogo.setMessage(getResources().getString(R.string.restaurarBodynUEVO)); // mensaje o body del dialog
        dialogo.setCancelable(false);
        //para el bootn aceptar del dialogo
        dialogo.setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {

                //permisos para escribir en la sd del celular
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Verifica permisos para Android 6.0+
                    permisoUsuarioGuardado();
                }

                //llamamos el metodo par aexportar la base de dato
                importDB(Bienvenida.this);

                if (permiso == 1 && currentDB.exists()) {

                    // apertura de a base de datos para que leea y escriba
                    AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Bienvenida.this, "BDCartera", null, 1);
                    SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                    Cursor fila = baseDeDatos.rawQuery("SELECT rowid FROM icono_gasto", null);
                    if (fila.moveToFirst()) {
                        int i = 0;
                        do {

                            ContentValues icono = new ContentValues();
                            icono.put("icon_gasto", icono_gasto[i]);

                            i++;
                            baseDeDatos.update("icono_gasto", icono, "rowid=" + i, null);

                        } while (fila.moveToNext());
                    }

                    Cursor f = baseDeDatos.rawQuery("SELECT rowid FROM icono_ingreso", null);
                    if (f.moveToFirst()) {
                        int i = 0;
                        do {

                            ContentValues icono = new ContentValues();
                            icono.put("icon_ingreso", icono_ingreso[i]);

                            i++;
                            baseDeDatos.update("icono_ingreso", icono, "rowid=" + i, null);

                        } while (f.moveToNext());
                    }


                    baseDeDatos.close();

                    //llamamos el metodo par aexportar la base de dato
                    exportDB(Bienvenida.this);
                    Intent intent = new Intent(Bienvenida.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(Bienvenida.this, getResources().getString(R.string.noencontrorespaldo),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        //para el bootn aceptar del dialogo
        dialogo.setNeutralButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {

                dialogo.cancel();
            }
        });
        // para el boton cancelar del dialogo
        dialogo.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {

                Intent intent = new Intent(Bienvenida.this, Main2Activity_Categorias_add.class);
                startActivity(intent);
                finish();
            }
        });
        dialogo.show();


    }

    /*********************** para IMPORTAR la base de datos sqlite  *********************************/
    public void importDB(Context context) {
        try {
//            File sd = Environment.getExternalStorageDirectory();
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath(nombreDB);
                String backupDBPath = String.format("%s.bak", nombreDB);
                currentDB = new File(sd, backupDBPath);

                // importar
                if (currentDB.exists()) {


                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.restauracioncompleta) +
                                    " " + backupDB.toString(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.restauracionfalloonohayarchivo),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************** Para exportar la base de datos sqlite  *********************************/
    public void exportDB(Context context) {
        try {
//            File sd = Environment.getExternalStorageDirectory();
//            File data = Environment.getDataDirectory();
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                String backupDBPath = String.format("%s.bak", nombreDB);
                File currentDB = context.getDatabasePath(nombreDB);
                File backupDB = new File(sd, backupDBPath);

                // exportar
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.respaldocompleto) +
                                    " " + backupDB.toString(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.respaldofallido),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //permiso ara poder escribir en la sd
    private void permisoUsuarioGuardado() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            permiso = 1;
            Log.i("Mensaje", "Se tiene permiso para leer!");
        }
    }


}
