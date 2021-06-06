package com.rrvq.carterapersonalpro;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Main2Activity_Transferencia_edit extends AppCompatActivity implements View.OnClickListener {

    private static final String CERO = "0";
    private static final String BARRA = "/";
    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    EditText et_desc;  // para relacionar con la vista
    ImageButton ib_fecha, ib_borrar;
    TextView tv_fecha, tv_disponible;
    String guardaFecha;

    String row_id_transferencia;
    int desde_T, hasta_T;
    String fecha_T, desc_T;
    float monto_T;

    Toolbar toolbar;
    Cursor fila;
    Spinner spinner1, spinner2;
    int icoSpinner1 = R.drawable.s15;
    int icoSpinner2 = R.drawable.s15;
    int spinnerP1, spinnerP2;
    String nombre_s1, nombre_s2;

    // para la calculadora
    TableLayout tableLayout;
    TextView tv_monto;
    ImageButton btn_mostrar;
    ConstraintLayout btn_container;
    Button[] castinCalculadora;
    //para la calculadora
    double resultado;
    String operador = "", mostrar, reserva = "";
    int cont = 0;

    //para mostrar solo dos decimales
    DecimalFormat formato = new DecimalFormat("#.#");
    public Activity fa;  // para llamar y fializar el activity desde otro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__transferencia_edit);

        fa = this; // para finalizar el activity llamandolo desde otro activity
        // Casting de los view
        castin_view();

        //Toolbar
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" +
                getResources().getString(R.string.editarTransferencia) + "</font>"));


        row_id_transferencia = getIntent().getStringExtra("row_id_transferencia");

        // escuchamos los btn relacionados a la calculadora
        ib_fecha.setOnClickListener(this);
        ib_borrar.setOnClickListener(this);
        btn_mostrar.setOnClickListener(this);
        btn_container.setOnClickListener(this);
        for (int i = 0; i < 16; i++) {
            castinCalculadora[i].setOnClickListener(this);
        }

        // conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // recupero los datos del ingreso
        fila = baseDeDatos.rawQuery("SELECT fecha_transf, monto_transf, desc_transf, desde_row, hasta_row FROM transferencia WHERE rowid=" + row_id_transferencia, null);
        if (fila.moveToFirst()) {
            fecha_T = fila.getString(0);
            monto_T = fila.getFloat(1);
            desc_T = fila.getString(2);
            desde_T = fila.getInt(3);
            hasta_T = fila.getInt(4);

            // recupero y mustro la informacion de la transferencia seleccionado
            tv_fecha.setText(fecha_T);
            tv_monto.setText(formato.format(monto_T));
            et_desc.setText(desc_T);

        }


        // Spinners
        spinnerDesde();
        spinnerHasta();

    }

    /*********************  Casting de los view ********************************/
    public void castin_view() {
        toolbar = findViewById(R.id.toolbar);
        spinner1 = findViewById(R.id.view_spinner1);
        spinner2 = findViewById(R.id.view_spinner2);

        ib_fecha = findViewById(R.id.ib_fecha);
        tv_fecha = findViewById(R.id.tv_fecha);
        ib_borrar = findViewById(R.id.ib_borrar);
        tv_monto = findViewById(R.id.tv_monto);
        et_desc = findViewById(R.id.et_descripcion);
        tv_disponible = findViewById(R.id.tv_disponible);

        //para mostrar el teclado o no
        tableLayout = findViewById(R.id.tableLayout1);
        btn_mostrar = findViewById(R.id.btn_mostrar);

        btn_container = findViewById(R.id.btn_container);

        //Castin de la calculadora
        castinCalculadora = new Button[]{
                findViewById(R.id.btn_uno), findViewById(R.id.btn_dos),
                findViewById(R.id.btn_tres), findViewById(R.id.btn_cuatro),
                findViewById(R.id.btn_cinco), findViewById(R.id.btn_seis),
                findViewById(R.id.btn_siete), findViewById(R.id.btn_ocho),
                findViewById(R.id.btn_nueve), findViewById(R.id.btn_cero),
                findViewById(R.id.btn_mas), findViewById(R.id.btn_menos),
                findViewById(R.id.btn_por), findViewById(R.id.btn_entre),
                findViewById(R.id.btn_punto), findViewById(R.id.btn_igual)};
    }

    /*********************  Casting de los view ********************************/
    public void editTransferencia(View vista) {
        // conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();


        if (desde_T == 0) {

            Toast.makeText(this, getResources().getString(R.string.nopuedeeliminarfaltaunacuenta), Toast.LENGTH_SHORT).show();

        } else {

            String tvFecha = tv_fecha.getText().toString();
            String tvMonto = tv_monto.getText().toString();
            String etDescripcion = et_desc.getText().toString();


            if (!tvFecha.isEmpty() && !tvMonto.isEmpty()) {

                float monto = Float.parseFloat(tvMonto);
                if (monto > 0) {
                    // si los espines son iguales
                    if (!nombre_s1.equals(nombre_s2)) {

                        // si la primera SI es igual y la segunda SI
                        if (desde_T == spinnerP1 && hasta_T == spinnerP2) {

                            float tDisponible1 = 0, tDisponible2 = 0;

                            fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP1, null);
                            if (fila.moveToFirst()) {
                                tDisponible1 = fila.getFloat(0);

                            }
                            fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP2, null);
                            if (fila.moveToFirst()) {
                                tDisponible2 = fila.getFloat(0);

                            }

//                    ACTUALIZAMOS LOS VALORES DE LA TABLA DE GASTOS
                            ContentValues editT = new ContentValues();
                            editT.put("fecha_transf", tvFecha);
                            editT.put("monto_transf", monto);
                            editT.put("desc_transf", etDescripcion);
                            editT.put("desde_row", spinnerP1);
                            editT.put("hasta_row", spinnerP2);

                            if (monto_T > monto) {
                                // a disponible 1 le sumo
                                // a disponible 2 le resto

                                tDisponible1 = (tDisponible1 - monto) + monto_T;
                                tDisponible2 = (tDisponible2 - monto_T) + monto;

                                if (tDisponible1 >= 0 && tDisponible2 >= 0) {

                                    baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                    // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                    ContentValues addCuenta1 = new ContentValues();
                                    addCuenta1.put("disponible_cuenta", tDisponible1);
                                    baseDeDatos.update("cuenta", addCuenta1, "rowid=" + spinnerP1, null);

                                    // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                    ContentValues addCuenta2 = new ContentValues();
                                    addCuenta2.put("disponible_cuenta", tDisponible2);
                                    baseDeDatos.update("cuenta", addCuenta2, "rowid=" + spinnerP2, null);

                                    Main2Activity_Calendario.fa.finish();
                                    Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                    startActivity(Siguiente);
                                    Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                    baseDeDatos.close();
                                    finish();

                                } else {
                                    Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                }

                            } else if (monto_T < monto) {
                                // a disponible 1 le resto
                                // a disponible 2 le sumo

                                tDisponible1 = (tDisponible1 + monto_T) - monto;
                                tDisponible2 = (tDisponible2 + monto) - monto_T;

                                if (tDisponible1 >= 0 && tDisponible2 >= 0) {

                                    baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                    // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                    ContentValues addCuenta1 = new ContentValues();
                                    addCuenta1.put("disponible_cuenta", tDisponible1);
                                    baseDeDatos.update("cuenta", addCuenta1, "rowid=" + spinnerP1, null);

                                    // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                    ContentValues addCuenta2 = new ContentValues();
                                    addCuenta2.put("disponible_cuenta", tDisponible2);
                                    baseDeDatos.update("cuenta", addCuenta2, "rowid=" + spinnerP2, null);

                                    Main2Activity_Calendario.fa.finish();
                                    Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                    startActivity(Siguiente);
                                    Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                    baseDeDatos.close();
                                    finish();

                                } else {
                                    Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                }
                            } else if (monto_T == monto) {

                                baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                Main2Activity_Calendario.fa.finish();
                                Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                startActivity(Siguiente);
                                Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                baseDeDatos.close();
                                finish();
                            }
                        } else
                            // si la primera SI es igual y la segunda NO
                            if (desde_T == spinnerP1 && hasta_T != spinnerP2) {

                                float tDisponible1 = 0, tDisponible2 = 0;

                                fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP1, null);
                                if (fila.moveToFirst()) {
                                    tDisponible1 = fila.getFloat(0);

                                }
                                fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP2, null);
                                if (fila.moveToFirst()) {
                                    tDisponible2 = fila.getFloat(0);

                                }
                                fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + hasta_T, null);
                                float tdE = 0;
                                if (fila.moveToFirst()) {
                                    tdE = fila.getFloat(0);

                                    tdE = tdE - monto_T;

                                }


                                if (tdE >= 0) {


//                    ACTUALIZAMOS LOS VALORES DE LA TABLA DE GASTOS
                                    ContentValues editT = new ContentValues();
                                    editT.put("fecha_transf", tvFecha);
                                    editT.put("monto_transf", monto);
                                    editT.put("desc_transf", etDescripcion);
                                    editT.put("desde_row", spinnerP1);
                                    editT.put("hasta_row", spinnerP2);

                                    if (monto_T > monto) {
                                        // a disponible 1 le sumo
                                        // a disponible 2 le resto

                                        tDisponible1 = (tDisponible1 - monto) + monto_T;
                                        tDisponible2 = tDisponible2 + monto;

                                        if (tDisponible1 >= 0 && tDisponible2 >= 0) {

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA que ya no deseo tener la transferencia
                                            ContentValues addCuenta = new ContentValues();
                                            addCuenta.put("disponible_cuenta", tdE);
                                            baseDeDatos.update("cuenta", addCuenta, "rowid=" + hasta_T, null);

                                            baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                            ContentValues addCuenta1 = new ContentValues();
                                            addCuenta1.put("disponible_cuenta", tDisponible1);
                                            baseDeDatos.update("cuenta", addCuenta1, "rowid=" + spinnerP1, null);

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                            ContentValues addCuenta2 = new ContentValues();
                                            addCuenta2.put("disponible_cuenta", tDisponible2);
                                            baseDeDatos.update("cuenta", addCuenta2, "rowid=" + spinnerP2, null);

                                            Main2Activity_Calendario.fa.finish();
                                            Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                            startActivity(Siguiente);
                                            Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                            baseDeDatos.close();
                                            finish();

                                        } else {
                                            Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                        }

                                    } else if (monto_T < monto) {
                                        // a disponible 1 le resto
                                        // a disponible 2 le sumo

                                        tDisponible1 = (tDisponible1 + monto_T) - monto;
                                        tDisponible2 = tDisponible2 + monto;

                                        if (tDisponible1 >= 0 && tDisponible2 >= 0) {

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA que ya no deseo tener la transferencia
                                            ContentValues addCuenta = new ContentValues();
                                            addCuenta.put("disponible_cuenta", tdE);
                                            baseDeDatos.update("cuenta", addCuenta, "rowid=" + hasta_T, null);

                                            baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                            ContentValues addCuenta1 = new ContentValues();
                                            addCuenta1.put("disponible_cuenta", tDisponible1);
                                            baseDeDatos.update("cuenta", addCuenta1, "rowid=" + spinnerP1, null);

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                            ContentValues addCuenta2 = new ContentValues();
                                            addCuenta2.put("disponible_cuenta", tDisponible2);
                                            baseDeDatos.update("cuenta", addCuenta2, "rowid=" + spinnerP2, null);

                                            Main2Activity_Calendario.fa.finish();
                                            Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                            startActivity(Siguiente);
                                            Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                            baseDeDatos.close();
                                            finish();

                                        } else {
                                            Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (monto_T == monto) {

                                        // a disponible 2 le sumo

                                        tDisponible2 = tDisponible2 + monto;

                                        if (tDisponible2 >= 0) {

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA que ya no deseo tener la transferencia
                                            ContentValues addCuenta = new ContentValues();
                                            addCuenta.put("disponible_cuenta", tdE);
                                            baseDeDatos.update("cuenta", addCuenta, "rowid=" + hasta_T, null);

                                            baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                            // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                            ContentValues addCuenta2 = new ContentValues();
                                            addCuenta2.put("disponible_cuenta", tDisponible2);
                                            baseDeDatos.update("cuenta", addCuenta2, "rowid=" + spinnerP2, null);

                                            Main2Activity_Calendario.fa.finish();
                                            Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                            startActivity(Siguiente);
                                            Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                            baseDeDatos.close();
                                            finish();

                                        } else {
                                            Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                } else {
                                    Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                }


                            } else
                                // si la primera NO es igual y la segunda SI
                                if (desde_T != spinnerP1 && hasta_T == spinnerP2) {

                                    float tDisponible1 = 0, tDisponible2 = 0;

                                    fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP1, null);
                                    if (fila.moveToFirst()) {
                                        tDisponible1 = fila.getFloat(0);

                                    }
                                    fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP2, null);
                                    if (fila.moveToFirst()) {
                                        tDisponible2 = fila.getFloat(0);

                                    }
                                    fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + desde_T, null);
                                    float tdE = 0;
                                    if (fila.moveToFirst()) {
                                        tdE = fila.getFloat(0);

                                        tdE = tdE + monto_T;

                                    }

                                    // a disponible 1 le resto

                                    float tD1 = tDisponible1;

                                    if (tD1 > 0) {


//                    ACTUALIZAMOS LOS VALORES DE LA TABLA DE GASTOS
                                        ContentValues editT = new ContentValues();
                                        editT.put("fecha_transf", tvFecha);
                                        editT.put("monto_transf", monto);
                                        editT.put("desc_transf", etDescripcion);
                                        editT.put("desde_row", spinnerP1);
                                        editT.put("hasta_row", spinnerP2);

                                        if (monto_T > monto) {
                                            // a disponible 1 le resto
                                            // a disponible 2 le resto

                                            tDisponible1 = tDisponible1 - monto;
                                            tDisponible2 = (tDisponible2 - monto_T) + monto;

                                            if (tDisponible1 >= 0 && tDisponible2 >= 0) {

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA que ya no deseo tener la transferencia
                                                ContentValues addCuenta = new ContentValues();
                                                addCuenta.put("disponible_cuenta", tdE);
                                                baseDeDatos.update("cuenta", addCuenta, "rowid=" + desde_T, null);

                                                baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                                ContentValues addCuenta1 = new ContentValues();
                                                addCuenta1.put("disponible_cuenta", tDisponible1);
                                                baseDeDatos.update("cuenta", addCuenta1, "rowid=" + spinnerP1, null);

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                                ContentValues addCuenta2 = new ContentValues();
                                                addCuenta2.put("disponible_cuenta", tDisponible2);
                                                baseDeDatos.update("cuenta", addCuenta2, "rowid=" + spinnerP2, null);

                                                Main2Activity_Calendario.fa.finish();
                                                Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                                startActivity(Siguiente);
                                                Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                                baseDeDatos.close();
                                                finish();

                                            } else {
                                                Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                            }

                                        } else if (monto_T < monto) {
                                            // a disponible 1 le resto
                                            // a disponible 2 le sumo

                                            tDisponible1 = tDisponible1 - monto;
                                            tDisponible2 = (tDisponible2 + monto) - monto_T;

                                            if (tDisponible1 >= 0 && tDisponible2 >= 0) {

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA que ya no deseo tener la transferencia
                                                ContentValues addCuenta = new ContentValues();
                                                addCuenta.put("disponible_cuenta", tdE);
                                                baseDeDatos.update("cuenta", addCuenta, "rowid=" + desde_T, null);

                                                baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                                ContentValues addCuenta1 = new ContentValues();
                                                addCuenta1.put("disponible_cuenta", tDisponible1);
                                                baseDeDatos.update("cuenta", addCuenta1, "rowid=" + spinnerP1, null);

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                                ContentValues addCuenta2 = new ContentValues();
                                                addCuenta2.put("disponible_cuenta", tDisponible2);
                                                baseDeDatos.update("cuenta", addCuenta2, "rowid=" + spinnerP2, null);

                                                Main2Activity_Calendario.fa.finish();
                                                Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                                startActivity(Siguiente);
                                                Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                                baseDeDatos.close();
                                                finish();

                                            } else {
                                                Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (monto_T == monto) {

                                            tDisponible1 = tDisponible1 - monto;

                                            if (tDisponible1 >= 0) {

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA que ya no deseo tener la transferencia
                                                ContentValues addCuenta = new ContentValues();
                                                addCuenta.put("disponible_cuenta", tdE);
                                                baseDeDatos.update("cuenta", addCuenta, "rowid=" + desde_T, null);


                                                baseDeDatos.update("transferencia", editT, "rowid=" + row_id_transferencia, null);

                                                // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                                                ContentValues addCuenta1 = new ContentValues();
                                                addCuenta1.put("disponible_cuenta", tDisponible1);
                                                baseDeDatos.update("cuenta", addCuenta1, "rowid=" + spinnerP1, null);

                                                Main2Activity_Calendario.fa.finish();
                                                Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                                                startActivity(Siguiente);
                                                Toast.makeText(this, R.string.transferenciaedit, Toast.LENGTH_SHORT).show();
                                                baseDeDatos.close();
                                                finish();
                                            } else {
                                                Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                            }


                                        }

                                    } else {
                                        Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                                    }


                                } else
                                    // si la primera NO es igual y la segunda NO
                                    if (desde_T != spinnerP1 && hasta_T != spinnerP2) {

                                        Toast.makeText(this, R.string.unespinerdebecoincidir, Toast.LENGTH_SHORT).show();
                                    }


                    } else {
                        Toast.makeText(this, R.string.spinneriguales, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, R.string.mayoracero, Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, R.string.fechaymontoyicono, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*********************  Flecha ATRAS color BLANCA ********************************/
    public void flechaBlanca() {

        // pra colocar la flecha de color blanco de volver
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // flecha de volver atras
    }

    /*********************  Spinner DESDE ********************************/
    public void spinnerDesde() {

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();


        if (desde_T == 0) {

            ArrayList<String> opciones = new ArrayList<>();
            opciones.add(getResources().getString(R.string.nodisponible));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_seleccion, opciones);
            // ahora hay que asignar al spiner el opjeto adapter que cremos en la linea de arriba
            spinner1.setAdapter(adapter);

        } else {

            fila = baseDeDatos.rawQuery("SELECT nombre_cuenta, icon_spinner FROM cuenta WHERE rowid=" + desde_T, null);
            if (fila.moveToFirst()) {
                nombre_s1 = fila.getString(0);
                icoSpinner1 = fila.getInt(1);
            }

            // llamo a a base de datos adminDB la declare global
            List<String> listaSpinner = adminDB.getAllLabels();
//        spinner.setCompoundDrawablesWithIntrinsicBounds( icoSpinner, 0, 0, 0 );
            // Creando adaptador para el spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_seleccion, listaSpinner);
            spinner1.setAdapter(dataAdapter);


            int posicion = dataAdapter.getPosition(nombre_s1);
            spinner1.setSelection(posicion);

            baseDeDatos.close();

            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Main2Activity_Transferencia_edit.this, "BDCartera", null, 1);
                    SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                    nombre_s1 = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion
                    spinnerP1 = parent.getSelectedItemPosition();
                    spinnerP1++;
                    fila = baseDeDatos.rawQuery("SELECT icon_spinner, disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP1, null);
                    if (fila.moveToFirst()) {
                        icoSpinner1 = fila.getInt(0);

                        tv_disponible.setText(fila.getString(1));
                    }

                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(icoSpinner1, 0, 0, 0);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    /*********************  Spinner HASTA ********************************/
    public void spinnerHasta() {

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        fila = baseDeDatos.rawQuery("SELECT nombre_cuenta, icon_spinner FROM cuenta WHERE rowid=" + hasta_T, null);
        if (fila.moveToFirst()) {
            nombre_s2 = fila.getString(0);
            icoSpinner2 = fila.getInt(1);
        }

        // llamo a a base de datos adminDB la declare global
        List<String> listaSpinner = adminDB.getAllLabels();
//        spinner.setCompoundDrawablesWithIntrinsicBounds( icoSpinner, 0, 0, 0 );
        // Creando adaptador para el spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_seleccion, listaSpinner);
        spinner2.setAdapter(dataAdapter);


        int posicion = dataAdapter.getPosition(nombre_s2);
        spinner2.setSelection(posicion);

        baseDeDatos.close();

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Main2Activity_Transferencia_edit.this, "BDCartera", null, 1);
                SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                nombre_s2 = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion
                spinnerP2 = parent.getSelectedItemPosition();
                spinnerP2++;

                fila = baseDeDatos.rawQuery("SELECT icon_spinner, disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP2, null);
                if (fila.moveToFirst()) {
                    icoSpinner2 = fila.getInt(0);
                }

                ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(icoSpinner2, 0, 0, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*********************  Obtener fecha al dar click con dialog********************************/
    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                guardaFecha = diaFormateado + BARRA + mesFormateado + BARRA + year;
                tv_fecha.setText(guardaFecha);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            //También puede cargar los valores que usted desee

        }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    /*********************  Icono Action Bar ********************************/
    public boolean onCreateOptionsMenu(Menu vista) {
        getMenuInflater().inflate(R.menu.item_delete, vista);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem vista) {
        int id = vista.getItemId();

        if (id == R.id.btn_eliminar) {

            // Diaologo para eliminar
            final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle(getResources().getString(R.string.eliminar));
            dialogo.setMessage(getResources().getString(R.string.segurodeeliminar) + "\n" + getResources().getString(R.string.perderanregistros));
            dialogo.setCancelable(false);
            //para el bootn aceptar del dialogo
            dialogo.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo, int id) {

                    AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Main2Activity_Transferencia_edit.this, "BDCartera", null, 1);
                    SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                    if (desde_T == 0) {

                        float tDisponible2 = 0;

                        fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + hasta_T, null);
                        if (fila.moveToFirst()) {
                            tDisponible2 = fila.getFloat(0);

                        }

                        tDisponible2 = tDisponible2 - monto_T;

                        if (tDisponible2 >= 0) {

                            // actualizamo el saldo disponible de la cuenta
                            ContentValues addCuenta2 = new ContentValues();
                            addCuenta2.put("disponible_cuenta", tDisponible2);
                            baseDeDatos.update("cuenta", addCuenta2, "rowid=" + hasta_T, null);

                            //eliminamos el ingreso deseado
                            baseDeDatos.delete("transferencia", "rowid=" + row_id_transferencia, null);

                            Main2Activity_Calendario.fa.finish();
                            Intent Siguiente = new Intent(Main2Activity_Transferencia_edit.this, Main2Activity_Calendario.class);
                            startActivity(Siguiente);
                            Toast.makeText(Main2Activity_Transferencia_edit.this, R.string.transferenciadelete, Toast.LENGTH_SHORT).show();
                            baseDeDatos.close();
                            finish();

                        } else {
                            Toast.makeText(Main2Activity_Transferencia_edit.this, R.string.nopuedeeliminaringreso, Toast.LENGTH_SHORT).show();
                            dialogo.cancel();
                        }

                    } else {

                        float tDisponible1 = 0, tDisponible2 = 0;

                        fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + desde_T, null);
                        if (fila.moveToFirst()) {
                            tDisponible1 = fila.getFloat(0);

                        }
                        fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + hasta_T, null);
                        if (fila.moveToFirst()) {
                            tDisponible2 = fila.getFloat(0);

                        }


                        tDisponible1 = tDisponible1 + monto_T;
                        tDisponible2 = tDisponible2 - monto_T;

                        if (tDisponible1 >= 0 && tDisponible2 >= 0) {

                            // actualizamo el saldo disponible de la cuenta
                            ContentValues addCuenta1 = new ContentValues();
                            addCuenta1.put("disponible_cuenta", tDisponible1);
                            baseDeDatos.update("cuenta", addCuenta1, "rowid=" + desde_T, null);

                            // actualizamo el saldo disponible de la cuenta
                            ContentValues addCuenta2 = new ContentValues();
                            addCuenta2.put("disponible_cuenta", tDisponible2);
                            baseDeDatos.update("cuenta", addCuenta2, "rowid=" + hasta_T, null);

                            //eliminamos el ingreso deseado
                            baseDeDatos.delete("transferencia", "rowid=" + row_id_transferencia, null);

                            Intent Siguiente = new Intent(Main2Activity_Transferencia_edit.this, Main2Activity_Calendario.class);
                            startActivity(Siguiente);
                            Toast.makeText(Main2Activity_Transferencia_edit.this, R.string.transferenciadelete, Toast.LENGTH_SHORT).show();
                            baseDeDatos.close();

                        } else {
                            Toast.makeText(Main2Activity_Transferencia_edit.this, R.string.nopuedeeliminaringreso, Toast.LENGTH_SHORT).show();
                            dialogo.cancel();
                        }
                    }


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

            return true;
        }
        return super.onOptionsItemSelected(vista);
    }

    @Override
    public void onClick(View v) {
        //Calculadora con iconos seleccionables
        switch (v.getId()) {
            case R.id.ib_fecha:
                obtenerFecha();
                break;
            case R.id.ib_borrar:
                tv_monto.setText("0");
                cont = 0;
                resultado = 0;
                operador = "";
                mostrar = "0";
                reserva = "";
                break;
            case R.id.btn_mostrar:
                tableLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_container:
                tableLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_cero:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("0");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "0";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_uno:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("1");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "1";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_dos:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("2");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "2";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_tres:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("3");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "3";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_cuatro:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("4");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "4";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_cinco:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("5");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "5";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_seis:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("6");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "6";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_siete:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("7");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "7";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_ocho:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("8");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "8";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_nueve:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("9");
                } else {
                    mostrar = tv_monto.getText().toString();
                    mostrar = mostrar + "9";
                    tv_monto.setText(mostrar);
                }
                break;
            case R.id.btn_mas:
                if (cont == 0) {
                    cont++;
                    operador = "+";
                    reserva = "0";
                    tv_monto.setText("");
                } else if (reserva.equals("")) {
                    reserva = tv_monto.getText().toString();
                    operador = "+";
                    tv_monto.setText("");
                } else {
                    operador = "+";
                    tv_monto.setText("");
                }
                break;
            case R.id.btn_menos:
                if (cont == 0) {
                    cont++;
                    operador = "-";
                    reserva = "0";
                    tv_monto.setText("");
                } else if (reserva.equals("")) {
                    reserva = tv_monto.getText().toString();
                    operador = "-";
                    tv_monto.setText("");
                } else {
                    operador = "+";
                    tv_monto.setText("");
                }
                break;
            case R.id.btn_por:
                if (cont == 0) {
                    cont++;
                    operador = "*";
                    reserva = "0";
                    tv_monto.setText("");
                } else if (reserva.equals("")) {
                    reserva = tv_monto.getText().toString();
                    operador = "*";
                    tv_monto.setText("");
                } else {
                    operador = "*";
                    tv_monto.setText("");
                }
                break;
            case R.id.btn_entre:
                if (cont == 0) {
                    cont++;
                    operador = "/";
                    reserva = "0";
                    tv_monto.setText("");
                } else if (reserva.equals("")) {
                    reserva = tv_monto.getText().toString();
                    operador = "/";
                    tv_monto.setText("");
                } else {
                    operador = "/";
                    tv_monto.setText("");
                }
                break;
            case R.id.btn_punto:
                if (cont == 0) {
                    cont++;
                    tv_monto.setText("0.");
                } else {
                    mostrar = tv_monto.getText().toString();

                    int pos = 0;
                    for (int i = 0; i < mostrar.length(); i++) {
                        if (".".charAt(0) == mostrar.charAt(i)) {
                            pos = 1;
                        }
                    }
                    if (pos == 0) {
                        mostrar = mostrar + ".";
                        tv_monto.setText(mostrar);
                    }
                }
                break;
            case R.id.btn_igual:
                if (reserva.equals("")) {

                } else {
                    mostrar = tv_monto.getText().toString();
                    if (operador.equals("-")) {
                        if (!tv_monto.getText().toString().equals("")) {
                            resultado = Double.parseDouble(reserva) - Double.parseDouble(tv_monto.getText().toString());
                            if (resultado < 0) {
                                Toast.makeText(this, R.string.numeronegativo, Toast.LENGTH_SHORT).show();
                            } else {
                                tv_monto.setText(String.valueOf(resultado));
                                operador = "";
                                reserva = "";
                            }
                        } else {
                            Toast.makeText(this, R.string.dosvalores, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (operador.equals("+")) {
                        if (!tv_monto.getText().toString().equals("")) {
                            resultado = Double.parseDouble(reserva) + Double.parseDouble(tv_monto.getText().toString());
                            tv_monto.setText(String.valueOf(resultado));
                            operador = "";
                            reserva = "";
                        } else {
                            Toast.makeText(this, R.string.dosvalores, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (operador.equals("/")) {

                        if (!tv_monto.getText().toString().equals("")) {
                            resultado = Double.parseDouble(reserva) / Double.parseDouble(tv_monto.getText().toString());
                            double res = Double.parseDouble(reserva);
                            double rtv = Double.parseDouble(tv_monto.getText().toString());
                            if (res > 0 && rtv == 0) {
                                Toast.makeText(this, R.string.noDividir, Toast.LENGTH_SHORT).show();
                            } else if (res == 0 && rtv == 0) {
                                Toast.makeText(this, R.string.resIndefinido, Toast.LENGTH_SHORT).show();
                            } else {
                                tv_monto.setText(String.valueOf(resultado));
                                operador = "";
                                reserva = "";
                            }
                        } else {
                            Toast.makeText(this, R.string.dosvalores, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (operador.equals("*")) {
                        if (!tv_monto.getText().toString().equals("")) {
                            resultado = Double.parseDouble(reserva) * Double.parseDouble(tv_monto.getText().toString());
                            tv_monto.setText(String.valueOf(resultado));
                            operador = "";
                            reserva = "";
                        } else {
                            Toast.makeText(this, R.string.dosvalores, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }


}
