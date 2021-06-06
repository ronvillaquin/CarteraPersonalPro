package com.rrvq.carterapersonalpro;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.util.Calendar;

public class Main2Activity_Gasto_edit extends AppCompatActivity implements View.OnClickListener {

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

    String row_id_gasto;
    int rowidC_G, rowidI_G;
    String fecha_G, desc_G;
    float monto_G;

    EditText et_desc;  // para relacionar con la vista
    ImageButton ib_fecha, ib_borrar;
    TextView tv_fecha;
    String guardaFecha;

    ImageView iv_cuenta;
    TextView tv_cuenta, tv_disponible;

    //para mostrar solo dos decimales
    DecimalFormat formato = new DecimalFormat("#.#");

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
        setContentView(R.layout.activity_main2__gasto_edit);

        //Casting de los view
        castin_view();

        //  Toolbar
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" +
                getResources().getString(R.string.editgasto) + "</font>"));


        ib_fecha.setOnClickListener(this);
        ib_borrar.setOnClickListener(this);
        btn_mostrar.setOnClickListener(this);
        btn_container.setOnClickListener(this);
        for (int i = 0; i < 16; i++) {
            castinCalculadora[i].setOnClickListener(this);
        }

        row_id_gasto = getIntent().getStringExtra("row_id_gasto");


        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // recupero los datos del gasto
        fila = baseDeDatos.rawQuery("SELECT row_cuenta, row_icon_gasto, fecha_gasto, desc_gasto, monto_gasto FROM gasto WHERE rowid=" + row_id_gasto, null);
        if (fila.moveToFirst()) {
            rowidC_G = fila.getInt(0);
            rowidI_G = fila.getInt(1);
            fecha_G = fila.getString(2);
            desc_G = fila.getString(3);
            monto_G = fila.getFloat(4);
        }

        //Mostramos la cuenta y el icono
        fila = baseDeDatos.rawQuery("SELECT nombre_cuenta, icon_spinner, disponible_cuenta FROM cuenta WHERE rowid=" + rowidC_G, null);
        if (fila.moveToFirst()) {
            //muestro la cuenta que recibi de la sqlite
            tv_cuenta.setText(fila.getString(0));
            iv_cuenta.setImageResource(fila.getInt(1));
            tv_disponible.setText(fila.getString(2));
        }

        // recupero y mustro la informacion del gasto seleccionado
        tv_fecha.setText(fecha_G);
        tv_monto.setText(String.valueOf(formato.format(monto_G)));
        et_desc.setText(desc_G);


        baseDeDatos.close();

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
        tv_disponible = findViewById(R.id.tv_disponible);

        //para mostrar el teclado o no
        tableLayout = findViewById(R.id.tableLayout1);
        btn_mostrar = findViewById(R.id.btn_mostrar);

        btn_container = findViewById(R.id.btn_container);

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

    /*********************  Añadir gasto ********************************/
    public void editGasto(View vista) {
        // Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        String tvFecha = tv_fecha.getText().toString();
        String tvMonto = tv_monto.getText().toString();
        String etDescripcion = et_desc.getText().toString();

        if (!tvFecha.isEmpty() && !tvMonto.isEmpty()) {

            float monto = Float.parseFloat(tvMonto);
            if (monto > 0) {

                float tDisponible = Float.parseFloat(tv_disponible.getText().toString());

//                ACTUALIZAMOS LOS VALORES DE LA TABLA DE GASTOS
                ContentValues addGasto = new ContentValues();
                addGasto.put("row_cuenta", rowidC_G);
                addGasto.put("row_icon_gasto", rowidI_G);
                addGasto.put("fecha_gasto", tvFecha);
                addGasto.put("desc_gasto", etDescripcion);
                addGasto.put("monto_gasto", monto);


                if (monto_G > monto) {

                    tDisponible = (tDisponible - monto) + monto_G;

                    if (tDisponible >= 0) {

                        baseDeDatos.update("gasto", addGasto, "rowid=" + row_id_gasto, null);

                        // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                        ContentValues addCuenta = new ContentValues();
                        addCuenta.put("disponible_cuenta", tDisponible);
                        baseDeDatos.update("cuenta", addCuenta, "rowid=" + rowidC_G, null);

                        Main2Activity_Calendario.fa.finish();
                        Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                        startActivity(Siguiente);
                        Toast.makeText(this, R.string.gastoedit, Toast.LENGTH_SHORT).show();
                        baseDeDatos.close();
                        finish();
                    } else {
                        Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                    }

                } else if (monto_G < monto) {

                    tDisponible = (tDisponible + monto_G) - monto;

                    if (tDisponible >= 0) {

                        baseDeDatos.update("gasto", addGasto, "rowid=" + row_id_gasto, null);

                        // ACTUALIZAMO LOS VALORES DEL SALDO DISPONIBLE DE LA CUENTA
                        ContentValues addCuenta = new ContentValues();
                        addCuenta.put("disponible_cuenta", tDisponible);
                        baseDeDatos.update("cuenta", addCuenta, "rowid=" + rowidC_G, null);

                        Main2Activity_Calendario.fa.finish();
                        Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                        startActivity(Siguiente);
                        Toast.makeText(this, R.string.gastoedit, Toast.LENGTH_SHORT).show();
                        baseDeDatos.close();
                        finish();
                    } else {
                        Toast.makeText(this, R.string.gastomayordisponible, Toast.LENGTH_SHORT).show();
                    }
                } else if (monto_G == monto) {

                    baseDeDatos.update("gasto", addGasto, "rowid=" + row_id_gasto, null);

                    Main2Activity_Calendario.fa.finish();
                    Intent Siguiente = new Intent(this, Main2Activity_Calendario.class);
                    startActivity(Siguiente);
                    Toast.makeText(this, R.string.gastoedit, Toast.LENGTH_SHORT).show();
                    baseDeDatos.close();
                    finish();
                }


            } else {
                Toast.makeText(this, R.string.mayoracero, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, R.string.fechaymonto, Toast.LENGTH_SHORT).show();
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

        // apertura de a base de datos para que leea y escriba
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        if (id == R.id.btn_eliminar) {

            //DIALOGO PARA ELIMINAR
            final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle(getResources().getString(R.string.eliminar));
            dialogo.setMessage(getResources().getString(R.string.segurodeeliminar) + "\n" + getResources().getString(R.string.perderanregistros));
            dialogo.setCancelable(false);
            //para el bootn aceptar del dialogo
            dialogo.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo, int id) {

                    AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(Main2Activity_Gasto_edit.this, "BDCartera", null, 1);
                    SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                    // recibo el disponible de la cuenta
                    float disponibleCuenta = Float.parseFloat(tv_disponible.getText().toString());

                    disponibleCuenta = disponibleCuenta + monto_G;

                    // actualizamo el saldo disponible de la cuenta
                    ContentValues addCuenta = new ContentValues();
                    addCuenta.put("disponible_cuenta", disponibleCuenta);
                    baseDeDatos.update("cuenta", addCuenta, "rowid=" + rowidC_G, null);

                    //eliminamos el gasto deseado
                    baseDeDatos.delete("gasto", "rowid=" + row_id_gasto, null);

                    Main2Activity_Calendario.fa.finish();
                    Intent Siguiente = new Intent(Main2Activity_Gasto_edit.this, MainActivity.class);
                    startActivity(Siguiente);
                    Toast.makeText(Main2Activity_Gasto_edit.this, R.string.gastodelete, Toast.LENGTH_SHORT).show();
                    baseDeDatos.close();
                    finish();

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

            baseDeDatos.close();
            return true;
        }
        return super.onOptionsItemSelected(vista);
    }

    /***************************   PARA LA CALCULADORAAAA  *****************************/
    @Override
    public void onClick(View v) {

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
