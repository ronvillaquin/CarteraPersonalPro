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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Main2Activity_Ingreso extends AppCompatActivity implements View.OnClickListener {

    private static final String CERO = "0";
    private static final String BARRA = "/";
    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    Toolbar toolbar;
    Cursor fila;

    String row_id_cuenta;

    EditText et_desc;  // para relacionar con la vista
    ImageButton ib_fecha, ib_borrar;
    TextView tv_fecha;
    String guardaFecha;

    ImageView iv_cuenta;
    TextView tv_cuenta;

    //para los iconos y texto de las categorias ingresos
    TextView[] castintv1;
    ImageButton[] castinImage;
    int btn_select = 0;
    ImageButton imgSelect;
    int[] btn_id = {R.id.btn_img_1, R.id.btn_img_2, R.id.btn_img_3, R.id.btn_img_4};
//    int[] tv1_id = {R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4};

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
        setContentView(R.layout.activity_main2__ingreso);

        //Casting de los view
        castin_view();

        // Toolbar
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.nuevoIngreso) + "</font>"));

        // recupero y mustro la fecha actual
        guardaFecha = getDate();
        tv_fecha.setText(guardaFecha);

        row_id_cuenta = getIntent().getStringExtra("row_id_cuenta");

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        //Mostramos la cuenta y el icono
        fila = baseDeDatos.rawQuery("SELECT nombre_cuenta, icon_spinner FROM cuenta WHERE rowid=" + row_id_cuenta, null);
        if (fila.moveToFirst()) {
            //muestro la cuenta que recibi de la sqlite
            tv_cuenta.setText(fila.getString(0));
            iv_cuenta.setImageResource(fila.getInt(1));
        }

        //creamos para recuperar y mostrar los iconos en la tabla icon ingresos
        fila = baseDeDatos.rawQuery("SELECT rowid, icon_ingreso, inombre_ingreso FROM icono_ingreso", null);
        if (fila.moveToFirst()) {
            int i = 0;
            btn_select = fila.getInt(0);
            do {
                castinImage[i].setImageResource(fila.getInt(1));
                castintv1[i].setText(fila.getString(2));
                castinImage[i].setOnClickListener(this);
                // selecciono por defecto el primer icono
                castinImage[0].setBackgroundResource(R.color.opacoTransparente);
                imgSelect = castinImage[0];
                i++;
            } while (fila.moveToNext());
        }

        // escuchamos los btn relacionados a la calculadora
        ib_fecha.setOnClickListener(this);
        ib_borrar.setOnClickListener(this);
        btn_mostrar.setOnClickListener(this);
        btn_container.setOnClickListener(this);
        for (int i = 0; i < 16; i++) {
            castinCalculadora[i].setOnClickListener(this);
        }


    }

    /*********************  Casting de los view ********************************/
    public void castin_view() {
        toolbar = findViewById(R.id.toolbar);
        ib_fecha = findViewById(R.id.ib_fecha);
        tv_fecha = findViewById(R.id.tv_fecha);
        ib_borrar = findViewById(R.id.ib_borrar);
        tv_monto = findViewById(R.id.tv_monto);
        et_desc = findViewById(R.id.et_descripcion);
        iv_cuenta = findViewById(R.id.iv_cuenta);
        tv_cuenta = findViewById(R.id.tv_cuenta);

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

        // Castin de los botones de vista
        castinImage = new ImageButton[]{
                findViewById(R.id.btn_img_1), findViewById(R.id.btn_img_2),
                findViewById(R.id.btn_img_3), findViewById(R.id.btn_img_4)};
        // Castin de textview 1
        castintv1 = new TextView[]{
                findViewById(R.id.tv1), findViewById(R.id.tv2), findViewById(R.id.tv3),
                findViewById(R.id.tv4)};
    }

    /*********************  BOTON PARA INGRESAR ********************************/
    public void addIngreso(View vista) {
        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        String tvFecha = tv_fecha.getText().toString();
        String tvMonto = tv_monto.getText().toString();
        String etDescripcion = et_desc.getText().toString();

        if (!tvFecha.isEmpty() && !tvMonto.isEmpty() && btn_select != 0) {

            float monto = Float.parseFloat(tvMonto);
            if (monto > 0) {
                fila = baseDeDatos.rawQuery("SELECT disponible_cuenta FROM cuenta WHERE rowid=" + row_id_cuenta, null);
                float tDisponible = 0;
                if (fila.moveToFirst()) {
                    tDisponible = monto + fila.getFloat(0);
                }

                // verificar cuandos e va a gastar que el monto a gastar no sea mayor al disonible en la cuenta
                ContentValues addIngreso = new ContentValues();
                addIngreso.put("row_cuenta", row_id_cuenta);
                addIngreso.put("row_icon_ingreso", btn_select);
                addIngreso.put("fecha_ingreso", tvFecha);
                addIngreso.put("desc_ingreso", etDescripcion);
                addIngreso.put("monto_ingreso", monto);
                baseDeDatos.insert("ingreso", null, addIngreso);

                ContentValues addCuenta = new ContentValues();
                addCuenta.put("disponible_cuenta", tDisponible);
                baseDeDatos.update("cuenta", addCuenta, "rowid=" + row_id_cuenta, null);

                Intent Siguiente = new Intent(this, MainActivity.class);
                Siguiente.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Siguiente);
                Toast.makeText(this, R.string.ingresoadd, Toast.LENGTH_SHORT).show();
                baseDeDatos.close();
                finish();
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

    /*********************  Obtener fecha al dar click con dialog********************************/
    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el d??a obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                guardaFecha = diaFormateado + BARRA + mesFormateado + BARRA + year;
                tv_fecha.setText(guardaFecha);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            //Tambi??n puede cargar los valores que usted desee

        }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    /*********************  Obtener fecha en el oncreate ********************************/
    private String getDate() {             // se ver??a as??: miercoles 26/09/2018 05:30 p.m.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onClick(View v) {

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // Parametros para gastos del icono seleccionado
        fila = baseDeDatos.rawQuery("SELECT rowid FROM icono_ingreso", null);
        if (fila.moveToFirst()) {
            int i = 0;
            do {
                if (v.getId() == btn_id[i]) {
                    btn_select = fila.getInt(0);
                    castinImage[i].setBackgroundResource(R.color.opacoTransparente);
                    imgSelect = castinImage[i];

                } else {
                    castinImage[i].setBackgroundResource(R.color.transparente);
                }
                i++;
            } while (fila.moveToNext());
        }
        imgSelect.setBackgroundResource(R.color.opacoTransparente);

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

        baseDeDatos.close();
    }
}
