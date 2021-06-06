package com.rrvq.carterapersonalpro;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main2Activity_Transferencia extends AppCompatActivity implements View.OnClickListener {

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

    Toolbar toolbar;
    Cursor fila;
    Spinner spinner1, spinner2;
    int icoSpinner1 = R.drawable.s15;
    int icoSpinner2 = R.drawable.s15;
    int spinnerP1, spinnerP2;
    String nombre_s1, nombre_s2;
    String tipo_moneda;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__transferencia);

        // Casting de los view
        castin_view();

        // Toolbar
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.nuevaTransferencia) + "</font>"));

        // recupero y mustro la fecha actual
        guardaFecha = getDate();
        tv_fecha.setText(guardaFecha);

        // escuchamos los btn relacionados a la calculadora
        ib_fecha.setOnClickListener(this);
        ib_borrar.setOnClickListener(this);
        btn_mostrar.setOnClickListener(this);
        btn_container.setOnClickListener(this);
        for (int i = 0; i < 16; i++) {
            castinCalculadora[i].setOnClickListener(this);
        }


        //Spinner
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
    public void addTransferencia(View vista) {
        // Conexion a la base de datos sqlite
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        String tvFecha = tv_fecha.getText().toString();
        String tvMonto = tv_monto.getText().toString();
        String etDescripcion = et_desc.getText().toString();

        if (!tvFecha.isEmpty() && !tvMonto.isEmpty()) {

            float monto = Float.parseFloat(tvMonto);
            if (monto > 0) {
                if (!nombre_s1.equals(nombre_s2)) {

                    //verifico el saldo e la primera cuenta para commparar  con el mont que desea tranferir
                    // si es mayor e monto al saldo no se puede

                    fila = baseDeDatos.rawQuery("SELECT rowid, disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP1, null);
                    float tDisponible1 = 0;
                    int rowC1 = 0;
                    if (fila.moveToFirst()) {
                        rowC1 = fila.getInt(0);
                        tDisponible1 = fila.getFloat(1);

                    }

                    if (tDisponible1 >= monto) {

                        fila = baseDeDatos.rawQuery("SELECT rowid, disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP2, null);
                        float tDisponible2 = 0;
                        int rowC2 = 0;
                        if (fila.moveToFirst()) {
                            rowC2 = fila.getInt(0);
                            tDisponible2 = fila.getFloat(1);
                        }

                        tDisponible1 = tDisponible1 - monto;

                        tDisponible2 = tDisponible2 + monto;

                        ContentValues addC1 = new ContentValues();
                        addC1.put("disponible_cuenta", tDisponible1);
                        baseDeDatos.update("cuenta", addC1, "rowid=" + rowC1, null);

                        ContentValues addC2 = new ContentValues();
                        addC2.put("disponible_cuenta", tDisponible2);
                        baseDeDatos.update("cuenta", addC2, "rowid=" + rowC2, null);

                        ContentValues addT = new ContentValues();
                        addT.put("fecha_transf", tvFecha);
                        addT.put("monto_transf", monto);
                        addT.put("desc_transf", etDescripcion);
                        addT.put("desde_row", rowC1);
                        addT.put("hasta_row", rowC2);
                        baseDeDatos.insert("transferencia", null, addT);


                        Intent Siguiente = new Intent(this, MainActivity.class);
                        Siguiente.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Siguiente);
                        Toast.makeText(this, R.string.transferenciaadd, Toast.LENGTH_SHORT).show();
                        baseDeDatos.close();
                        finish();

                    } else {
                        Toast.makeText(this, R.string.montomayoradisponible, Toast.LENGTH_SHORT).show();
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

        // recupero el tipo de moneda
        fila = baseDeDatos.rawQuery("SELECT tipo_moneda FROM preferencias", null);
        if (fila.moveToFirst()) {
            tipo_moneda = fila.getString(0);
        }

        // llamo a a base de datos adminDB la declare global
        List<String> listaSpinner = adminDB.getAllLabels();
//        spinner.setCompoundDrawablesWithIntrinsicBounds( icoSpinner, 0, 0, 0 );
        // Creando adaptador para el spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_seleccion, listaSpinner);
        spinner1.setAdapter(dataAdapter);

        baseDeDatos.close();

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Main2Activity_Transferencia.this, "BDCartera", null, 1);
                SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                nombre_s1 = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion
                spinnerP1 = parent.getSelectedItemPosition();
                spinnerP1++;
                fila = baseDeDatos.rawQuery("SELECT icon_spinner FROM cuenta WHERE rowid=" + spinnerP1, null);
                if (fila.moveToFirst()) {
                    icoSpinner1 = fila.getInt(0);
                }

                ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(icoSpinner1, 0, 0, 0);

                fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + spinnerP1, null);
                if (fila.moveToFirst()) {
                    String mostrar = tipo_moneda + " " + fila.getString(0);
                    tv_disponible.setText(mostrar);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*********************  Spinner HASTA ********************************/
    public void spinnerHasta() {

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // llamo a a base de datos adminDB la declare global
        List<String> listaSpinner = adminDB.getAllLabels();
//        spinner.setCompoundDrawablesWithIntrinsicBounds( icoSpinner, 0, 0, 0 );
        // Creando adaptador para el spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_seleccion, listaSpinner);
        spinner2.setAdapter(dataAdapter);

        baseDeDatos.close();

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Main2Activity_Transferencia.this, "BDCartera", null, 1);
                SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                nombre_s2 = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion
                spinnerP2 = parent.getSelectedItemPosition();
                spinnerP2++;
                fila = baseDeDatos.rawQuery("SELECT icon_spinner FROM cuenta WHERE rowid=" + spinnerP2, null);
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

    /*********************  Obtener fecha en el oncreate ********************************/
    private String getDate() {             // se vería así: miercoles 26/09/2018 05:30 p.m.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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
