package com.rrvq.carterapersonalpro;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Main2Activity_Cuentas extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Cursor fila;


    // id de los botones view
    ImageButton[] castinImageCuentas;
    TextView[] castintvCuentas;
    int[] btn_idCuentas = {R.id.btn_img_1, R.id.btn_img_2, R.id.btn_img_3, R.id.btn_img_4};

    public static Activity fa;  // para llamar y fializar el activity desde otro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__cuentas);

        fa = this; // para finalizar el activity llamandolo desde otro activity
        //*********************  Casting de los view ********************************/
        castin_view();
        //*********************  Toolbar  ********************************/
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.cuentas) + "</font>"));

        //*********************  Conexion a la base de datos  ********************************/
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        //*********************  Imagenes Categoria GASTOS ********************************/
        fila = baseDeDatos.rawQuery("SELECT nombre_cuenta, icon_cuenta FROM cuenta", null);
        if (fila.moveToFirst()) {
            int i = 0;
            do {
                castintvCuentas[i].setText(fila.getString(0));
                castinImageCuentas[i].setImageResource(fila.getInt(1));
                castinImageCuentas[i].setOnClickListener(this);
                i++;
            } while (fila.moveToNext());
        }

        baseDeDatos.close();

    }

    /*********************  Casting de los view ********************************/
    public void castin_view() {
        toolbar = findViewById(R.id.toolbar);

        // castin de las imagen button de gastos
        castinImageCuentas = new ImageButton[]{
                findViewById(R.id.btn_img_1), findViewById(R.id.btn_img_2),
                findViewById(R.id.btn_img_3), findViewById(R.id.btn_img_4)};
        // Castin de textview 1
        castintvCuentas = new TextView[]{
                findViewById(R.id.tv1), findViewById(R.id.tv2),
                findViewById(R.id.tv3), findViewById(R.id.tv4)};
    }

    /*********************  Flecha ATRAS color BLANCA ********************************/
    public void flechaBlanca() {

        // pra colocar la flecha de color blanco de volver
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // flecha de volver atras
    }

    //Verificar si va enviar datos del boton de categoria gastos o categoria cuentas

    /*********************  boton add categria gastos ********************************/
    public void btnAddCuentas(View vista) {

        //*********************  Conexion a la base de datos  ********************************/
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();
        fila = baseDeDatos.rawQuery("SELECT * FROM cuenta", null);
        int countGastos = fila.getCount();

        if (countGastos < 4) {
            baseDeDatos.close();
            Intent intent = new Intent(this, Main2Activity_Categorias_add.class);
            intent.putExtra("dato", "cuenta");
            startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.limitemaximo), Toast.LENGTH_SHORT).show();
        }
    }

    /*********************  Para recuperar los click de las imagebuttons ********************************/
    @Override
    public void onClick(View v) {

        //*********************  Conexion a la base de datos  ********************************/
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // GASTOS EDITAR Para los iconos de GASTOS
        fila = baseDeDatos.rawQuery("SELECT rowid FROM cuenta", null);
        if (fila.moveToFirst()) {
            int i = 0;
            do {
                if (v.getId() == btn_idCuentas[i]) {
                    int row = fila.getInt(0);

                    String rowString = String.valueOf(row);
                    Intent intent = new Intent(this, Main2Activity_Categorias_edit.class);
                    intent.putExtra("row_id", rowString);
                    // para saber que icono editar
                    intent.putExtra("dato", "cuenta");
                    startActivity(intent);
                }
                i++;
            } while (fila.moveToNext());
        }


        baseDeDatos.close();
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