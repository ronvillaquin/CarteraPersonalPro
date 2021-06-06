package com.rrvq.carterapersonalpro;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class Main2Activity_Ajustes extends AppCompatActivity {

    Toolbar toolbar;
    Cursor fila;
    String nombreDB = "BDCartera";
    String tipo_moneda;
    String selectMoneda;
    Spinner spinner;

    String[] monedas = {"$", "€", "¥", "£", "s/", "bsf", "bs"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__ajustes);

        //*********************  Casting de los view ********************************/
        castin_view();

        //*********************  Toolbar  ********************************/
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.ajustes) + "</font>"));

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // recupero el tipo de moneda
        fila = baseDeDatos.rawQuery("SELECT tipo_moneda FROM preferencias", null);
        if (fila.moveToFirst()) {
            tipo_moneda = fila.getString(0);
        }

        menuSpinner();

        baseDeDatos.close();

    }

    /*********************  Casting de los view ********************************/
    public void castin_view() {
        toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.view_spinner);
    }

    /*********************  Flecha ATRAS color BLANCA ********************************/
    public void flechaBlanca() {

        // pra colocar la flecha de color blanco de volver
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // flecha de volver atras
    }

    /********************* Boton ACERCA DE CARTERA PERSONAL  ************************/
    public void btnAcercaDe(View vista) {

        final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getResources().getString(R.string.acercadeheader));
        dialogo.setMessage(getResources().getString(R.string.ajustesAcercade) +
                "\n\n" + getResources().getString(R.string.version));

        dialogo.setCancelable(false);
        dialogo.setNegativeButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                // accion si da cancelar que no haga nada
                dialogo.cancel();
            }
        });
        dialogo.show();

    }

    /********************* Boton POLITICA DE PRIVACIDAD  ************************/
    public void btnPolitica(View vista) {

        /*final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getResources().getString(R.string.politicaheader));
        dialogo.setMessage(getResources().getString(R.string.politicabody));

        dialogo.setCancelable(false);
        dialogo.setNegativeButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                // accion si da cancelar que no haga nada
                dialogo.cancel();
            }
        });
        dialogo.show();*/

        Uri uri = Uri.parse("http://terminosmenores.epizy.com/politica.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    /******************** Boton de BAKUP BASE DE DATOS  **********************************/
    public void btnBakup(View vista) {

        //permisos para escribir en la sd del celular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            permisoUsuarioGuardado();
        }

        //*********************** DIALOGO   *********************************/
        final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getResources().getString(R.string.copiadeseguridad));
        dialogo.setMessage(getResources().getString(R.string.segurocopia));
        dialogo.setCancelable(false);
        //para el bootn aceptar del dialogo
        dialogo.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {

                //llamamos el metodo par aexportar la base de dato
                exportDB(Main2Activity_Ajustes.this);
            }
        });
        // para el boton cancelar del dialogo
        dialogo.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                // accion si da cancelar que no haga nada
                dialogo.cancel();
            }
        });
        dialogo.show();

    }

    /******************** Boton de RESTAURAR BASE DE DATOS  **********************************/
    public void btnRestaurar(View vista) {

        //permisos para escribir en la sd del celular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            permisoUsuarioGuardado();
        }

        //*********************** DIALOGO   *********************************/
        final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getResources().getString(R.string.restaurardatos));

        dialogo.setMessage(getResources().getString(R.string.restaurarBody)); // mensaje o body del dialog
        dialogo.setCancelable(false);
        //para el bootn aceptar del dialogo
        dialogo.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {

                //llamamos el metodo par aexportar la base de dato
                importDB(Main2Activity_Ajustes.this);
            }
        });
        // para el boton cancelar del dialogo
        dialogo.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                // accion si da cancelar que no haga nada
                dialogo.cancel();
            }
        });
        dialogo.show();

    }

    /*********************  Spinner ********************************/
    public void menuSpinner() {

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // llamo a a base de datos adminDB la declare global y agrego el metodoo que cree en ella
        // para llenar las cuentas en el spinner
        List<String> listaSpinner = adminDB.getAllLabels();
//        spinner.setCompoundDrawablesWithIntrinsicBounds( icoSpinner, 0, 0, 0 );
        // Creando adaptador para el spinner
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_monedas, monedas);
        spinner.setAdapter(dataAdapter);

        // para colocar un valor predeterminado en el spinner para que muestre el ultimo valor gurdado en tabla preferencias
        int posicion = dataAdapter.getPosition(tipo_moneda);
        spinner.setSelection(posicion);
        selectMoneda = tipo_moneda;
        baseDeDatos.close();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                //para cuando selecione el mismo nombre no haga nada
                if (parent.getItemAtPosition(position).equals(selectMoneda)) {
                    //no hacer nada
                } else {

                    selectMoneda = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion

                    //Conexion a la base de datos
                    AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Main2Activity_Ajustes.this, "BDCartera", null, 1);
                    SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                    fila = baseDeDatos.rawQuery("SELECT row_cuenta FROM preferencias ", null);
                    if (fila.moveToFirst()) {
                        int preferencias_rowid_cuenta = fila.getInt(0);

                        ContentValues modificar = new ContentValues();
                        modificar.put("tipo_moneda", selectMoneda);

                        // ahora hacemos la line apara poder modificar
                        baseDeDatos.update("preferencias", modificar, "row_cuenta=" + preferencias_rowid_cuenta, null);
                        baseDeDatos.close();

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    /*********************** para IMPORTAR la base de datos sqlite  *********************************/
    public void importDB(Context context) {
        try {
//            File sd = Environment.getExternalStorageDirectory();
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath(nombreDB);
                String backupDBPath = String.format("%s.bak", nombreDB);
                File currentDB = new File(sd, backupDBPath);

                // exportar
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
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.restauracionfallo),
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
            Log.i("Mensaje", "Se tiene permiso para leer!");
        }
    }


    /*************************BOTON ATRAS*********************************************/
    // para evitar que usen el boton de regresar de android paraq ue no reguresa los activitys
    //la palabra override es para sobre escribir metodos que ya estan establecidos

    // el codigo que esta e spara refrescar el activity sin necesidad d ellamarlo de nuevo y sin tener un pestañeo
    @Override
    public void onBackPressed() {
        finish();
        Intent siguiente = new Intent(this, MainActivity.class);
        siguiente.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // con la de arriba se elimian todas todas menos la que se llamo
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // con esta elimina solo las q estan delante de la que se llamo
        startActivity(siguiente);
    }


}