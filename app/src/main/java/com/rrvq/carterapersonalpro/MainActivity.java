package com.rrvq.carterapersonalpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //vista en general
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Spinner spinner;
    //para la tabla cuenta
    TextView tv_IngresoTotal, tv_GastoTotal, tv_saldo, tv_transferencia, tv_tporcentaje;
    String nombre_c;
    float total_c, disponible_c, gasto_c;
    int icoSpinner = R.drawable.s15, icoCuenta;
    int rowidC;
    String tipo_moneda = "";

    //para mostrar solo dos decimales
    DecimalFormat formato = new DecimalFormat("#.#");

    //Cursor para los select de las tablas
    Cursor fila, g;

    //para list iconos y texto de las categorias Gastos
    ImageButton[] castinImage;
    TextView[] castintv1, castintv1_1, castintvmonto;
    int[] btn_id = {R.id.btn_img_1, R.id.btn_img_2, R.id.btn_img_3, R.id.btn_img_4, R.id.btn_img_5, R.id.btn_img_6,
            R.id.btn_img_7, R.id.btn_img_8, R.id.btn_img_9, R.id.btn_img_10, R.id.btn_img_11, R.id.btn_img_12};
    /*int[] tv1_id = {R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5,
            R.id.tv6, R.id.tv7, R.id.tv8, R.id.tv9, R.id.tv10, R.id.tv11, R.id.tv12};*/
    int[] tv11_id = {R.id.tv1_1, R.id.tv2_2, R.id.tv3_3, R.id.tv4_4, R.id.tv5_5,
            R.id.tv6_6, R.id.tv7_7, R.id.tv8_8, R.id.tv9_9, R.id.tv10_10, R.id.tv11_11, R.id.tv12_12};
    /*int[] tv_monto = {R.id.tv1_monto, R.id.tv2_monto, R.id.tv3_monto, R.id.tv4_monto, R.id.tv5_monto,
            R.id.tv6_monto, R.id.tv7_monto, R.id.tv8_monto, R.id.tv9_monto, R.id.tv10_monto, R.id.tv11_monto, R.id.tv12_monto};*/

    /*ImageButton btn_img1;*/

    int[] mesString = {R.string.enero, R.string.febrero, R.string.marzo, R.string.abril, R.string.mayo,
            R.string.junio, R.string.julio, R.string.agosto, R.string.setiembre, R.string.octubre, R.string.noviembre, R.string.diciembre};

    ArrayList<String> meses = new ArrayList<String>();
    Spinner spinnerMES;
    int idSMES;
    int numANO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //*********************  Casting de los view ********************************/
        castin_view();
        numANO = Integer.parseInt(getAno());

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();


        fila = baseDeDatos.rawQuery("SELECT count(*) FROM preferencias", null);
        int icount_inicio = 0;
        if (fila.moveToFirst()) {
            icount_inicio = fila.getInt(0);
        }

        //si existen cuenta entra aqui si no al else
        if (icount_inicio > 0) {


            // recupero el tipo de moneda
            fila = baseDeDatos.rawQuery("SELECT tipo_moneda FROM preferencias", null);
            if (fila.moveToFirst()) {
                tipo_moneda = fila.getString(0);
            }

//            setvistaGastos();  // para ostrar los icnos de gastos

            setSpinnerMes();

        } else {
            baseDeDatos.close();
//            Intent Siguiente = new Intent(this, Main2Activity_Categorias_add.class);
            Intent Siguiente = new Intent(this, Bienvenida.class);
            startActivity(Siguiente);
            finish();
        }
    }

    /*********************  Casting de los view ********************************/
    public void castin_view() {
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.vistaNavegacion);
//        spinner = (MaterialBetterSpinner)findViewById(R.id.view_spinner);
        spinner = findViewById(R.id.view_spinner);

        //Casting lo que muestra de tabla cuenta
        tv_IngresoTotal = findViewById(R.id.tv_ingreso_total);
        tv_GastoTotal = findViewById(R.id.tv_gasto_total);
        tv_saldo = findViewById(R.id.tv_saldo);
        tv_transferencia = findViewById(R.id.tv_transferencia_gasto);
        tv_tporcentaje = findViewById(R.id.tv_transferenciaPorcentaje);

        spinnerMES = findViewById(R.id.spinnerMeses);

        // Castin de los botones de vista
        castinImage = new ImageButton[]{
                findViewById(R.id.btn_img_1), findViewById(R.id.btn_img_2),
                findViewById(R.id.btn_img_3), findViewById(R.id.btn_img_4),
                findViewById(R.id.btn_img_5), findViewById(R.id.btn_img_6),
                findViewById(R.id.btn_img_7), findViewById(R.id.btn_img_8),
                findViewById(R.id.btn_img_9), findViewById(R.id.btn_img_10),
                findViewById(R.id.btn_img_11), findViewById(R.id.btn_img_12)};
        // Castin de textview 1
        castintv1 = new TextView[]{
                findViewById(R.id.tv1), findViewById(R.id.tv2), findViewById(R.id.tv3),
                findViewById(R.id.tv4), findViewById(R.id.tv5), findViewById(R.id.tv6),
                findViewById(R.id.tv7), findViewById(R.id.tv8), findViewById(R.id.tv9),
                findViewById(R.id.tv10), findViewById(R.id.tv11), findViewById(R.id.tv12)};
        // Castin de texview 1_1
        castintv1_1 = new TextView[]{
                findViewById(R.id.tv1_1), findViewById(R.id.tv2_2), findViewById(R.id.tv3_3),
                findViewById(R.id.tv4_4), findViewById(R.id.tv5_5), findViewById(R.id.tv6_6),
                findViewById(R.id.tv7_7), findViewById(R.id.tv8_8), findViewById(R.id.tv9_9),
                findViewById(R.id.tv10_10), findViewById(R.id.tv11_11), findViewById(R.id.tv12_12)};
        // Castin de texview monto
        castintvmonto = new TextView[]{
                findViewById(R.id.tv1_monto), findViewById(R.id.tv2_monto), findViewById(R.id.tv3_monto),
                findViewById(R.id.tv4_monto), findViewById(R.id.tv5_monto), findViewById(R.id.tv6_monto),
                findViewById(R.id.tv7_monto), findViewById(R.id.tv8_monto), findViewById(R.id.tv9_monto),
                findViewById(R.id.tv10_monto), findViewById(R.id.tv11_monto), findViewById(R.id.tv12_monto)};

    }

    public void setvistaGastos() {

        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        //creamos para recuperar y comparar el ultimos spinner de la tabla preferencias
        fila = baseDeDatos.rawQuery("SELECT row_cuenta FROM preferencias", null);
        fila.moveToFirst();
        String row_c = fila.getString(0);

        fila = baseDeDatos.rawQuery("select rowid, nombre_cuenta, icon_cuenta, icon_spinner," +
                "disponible_cuenta from cuenta where rowid=" + row_c, null);

        if (fila.moveToFirst()) {
            rowidC = fila.getInt(0);
            nombre_c = fila.getString(1);
            icoCuenta = fila.getInt(2);
            icoSpinner = fila.getInt(3);
            disponible_c = fila.getFloat(4);

            if (disponible_c > 0) {
                tv_saldo.setText(getResources().getString(R.string.disponible) + " " + tipo_moneda + " " + formato.format(disponible_c));
            }
        }

        //creamos para recuperar y mostrar los iconos en la tabla icon gstos
        fila = baseDeDatos.rawQuery("SELECT icon_gasto, inombre_gasto FROM icono_gasto", null);
        if (fila.moveToFirst()) {
            int i = 0;
            do {
                castinImage[i].setImageResource(fila.getInt(0));
                castintv1[i].setText(fila.getString(1));
                castinImage[i].setOnClickListener(this);
                i++;
            } while (fila.moveToNext());
        }

        //SUMMOS LOS MONTOS DE GASTOS TRANSFERENCIAS DEL MES SPARA TENER EL TOTAL
        g = baseDeDatos.rawQuery("SELECT fecha_transf, monto_transf FROM transferencia where desde_row=" + rowidC, null);
        if (g.moveToFirst()) {
            do {
                String fe = g.getString(0);
                String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                int intfe = Integer.parseInt(compfe);

                String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                        String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                int intA = Integer.parseInt(comA);

                if (idSMES == intfe && numANO == intA) {
                    gasto_c = gasto_c + g.getFloat(1);
                }
            } while (g.moveToNext());
            if (gasto_c > 0) {
                tv_GastoTotal.setText(tipo_moneda + " " + formato.format(gasto_c));
            }
        }
        //SUMAMOS LOS MONTOS DE GASTOS DE ICONOS DEL MES PARA OBTENER EL TOTAL
        g = baseDeDatos.rawQuery("SELECT fecha_gasto, monto_gasto FROM gasto where row_cuenta=" + rowidC, null);
        if (g.moveToFirst()) {
            do {
                String fe = g.getString(0);
                String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                int intfe = Integer.parseInt(compfe);

                String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                        String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                int intA = Integer.parseInt(comA);

                if (idSMES == intfe && numANO == intA) {
                    gasto_c = gasto_c + g.getFloat(1);
                }
            } while (g.moveToNext());
            if (gasto_c > 0) {
                tv_GastoTotal.setText(tipo_moneda + " " + formato.format(gasto_c));
            }
        }

        //SUMAMOS TODOS LOS INGRESOS TRANSFERENCIAS DEL MES PARA OBTENER EL TOTAL
        g = baseDeDatos.rawQuery("SELECT fecha_transf, monto_transf FROM transferencia where hasta_row=" + rowidC, null);
        if (g.moveToFirst()) {
            do {
                String fe = g.getString(0);
                String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                int intfe = Integer.parseInt(compfe);

                String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                        String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                int intA = Integer.parseInt(comA);

                if (idSMES == intfe && numANO == intA) {
                    total_c = total_c + g.getFloat(1);
                }
            } while (g.moveToNext());
            if (total_c > 0) {
                tv_IngresoTotal.setText(tipo_moneda + " " + formato.format(total_c));
            }
        }

        //SUMAMOS TODOS LOS INGRESOS DEL MES PARA OBTENER EL TOTAL
        g = baseDeDatos.rawQuery("SELECT fecha_ingreso, monto_ingreso FROM ingreso where row_cuenta=" + rowidC, null);
        if (g.moveToFirst()) {
            do {
                String fe = g.getString(0);
                String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                int intfe = Integer.parseInt(compfe);

                String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                        String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                int intA = Integer.parseInt(comA);

                if (idSMES == intfe && numANO == intA) {
                    total_c = total_c + g.getFloat(1);
                }
            } while (g.moveToNext());
            if (total_c > 0) {
                tv_IngresoTotal.setText(tipo_moneda + " " + formato.format(total_c));
            }
        }

        // CALCULAMOS EL % DE LOS ICONOS DEL GASTO DEL MES
        fila = baseDeDatos.rawQuery("SELECT rowid, inombre_gasto FROM icono_gasto", null);
        if (fila.moveToFirst()) {
            int i = 0;
            int rowfila = 0, rowg = 0, rowc = 0;
            do {
                rowfila = fila.getInt(0);
                float porcen = 0;
                float tSuma = 0;
                g = baseDeDatos.rawQuery("SELECT row_icon_gasto, fecha_gasto, monto_gasto FROM gasto where row_cuenta=" + rowidC, null);
                if (g.moveToFirst()) {
                    float suma = 0, porcentaje = 0;
                    do {
                        rowg = g.getInt(0);

                        if (rowfila == rowg) {

                            String fe = g.getString(1);

                            String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                            int intfe = Integer.parseInt(compfe);

                            String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                                    String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                            int intA = Integer.parseInt(comA);

                            if (idSMES == intfe && numANO == intA) {
                                suma = suma + g.getFloat(2);
                            }

                        }

                    } while (g.moveToNext());
                    tSuma = suma;
                    porcentaje = (suma / gasto_c) * 100;
                    porcen = porcentaje;
                }

                if (porcen > 0) {
                    castintv1_1[i].setText(formato.format(porcen) + " " + getResources().getString(R.string.porcentaje));
                    castintvmonto[i].setText(tipo_moneda + " " + formato.format(tSuma));
                }

                i++;
            } while (fila.moveToNext());
        }

        // CALCULAMOS EL % DE LAS TRASNFERENCIAS DE ESTE MES
        fila = baseDeDatos.rawQuery("SELECT fecha_transf, monto_transf FROM transferencia WHERE desde_row=" + rowidC, null);
        if (fila.moveToFirst()) {
            float montoT = 0;
            float porcenT = 0;
            do {

                String fe = fila.getString(0);

                String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                int intfe = Integer.parseInt(compfe);

                String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                        String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                int intA = Integer.parseInt(comA);

                if (idSMES == intfe && numANO == intA) {
                    montoT = montoT + fila.getFloat(1);
                }

            } while (fila.moveToNext());
            porcenT = (montoT / gasto_c) * 100;

            if (porcenT > 0) {
                tv_tporcentaje.setText(formato.format(porcenT) + " " + getResources().getString(R.string.porcentaje));
                tv_transferencia.setText("- " + tipo_moneda + " " + formato.format(montoT));
            }
        }

        //*********************  Toolbar y menu lateral ********************************/
        toolbarMenu();
        //*********************  Spinner ********************************/
        menuSpinner();

        baseDeDatos.close();
    }

    /*********************  SPINNER DE LOS MESES ********************************/
    public void setSpinnerMes() {

        for (int i = 0; i < mesString.length; i++) {

            meses.add(getString(mesString[i]));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_meses, meses);
        // ahora hay que asignar al spiner el opjeto adapter que cremos en la linea de arriba
        adapter.setDropDownViewResource(R.layout.spinner_seleccion);
        spinnerMES.setAdapter(adapter);

        // para colocar un valor predeterminado en el spinner para que muestre el ultimo valor gurdado en tabla preferencias
        for (int i = 0; i <= mesString.length; i++) {

            int d = Integer.parseInt(getMes());
            if (d == i) {

                int posicion = adapter.getPosition(getString(mesString[i - 1]));
                spinnerMES.setSelection(posicion);

            }

        }

        spinnerMES.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("MES")) {
                    //no hacer nada
                } else {
                    String label = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion
                    int spinnerPosicion = parent.getSelectedItemPosition();

                    spinnerPosicion++;
                    idSMES = spinnerPosicion;
                    gasto_c = 0;
                    total_c = 0;
                    for (int i = 0; i < tv11_id.length; i++) {
                        castintv1_1[i].setText("");
                        castintvmonto[i].setText("");
                    }
                    tv_transferencia.setText(getResources().getString(R.string.cero));
                    tv_IngresoTotal.setText(getResources().getString(R.string.cero));
                    tv_GastoTotal.setText(getResources().getString(R.string.cero));
//                    tv_tporcentaje.setText(getResources().getString(R.string.transferenciagasto));
                    tv_tporcentaje.setText("");
                    setvistaGastos();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    /*********************  BOTON INGRESOS ********************************/
    public void btnIngreso(View vista) {
        String rowidCuenta = String.valueOf(rowidC);
        Intent intent = new Intent(this, Main2Activity_Ingreso.class);
        intent.putExtra("row_id_cuenta", rowidCuenta);
        startActivity(intent);
    }

    /*********************  Boton Grafica ********************************/
    public void btnGrafica(View vista) {
        String rowidCuenta = String.valueOf(rowidC);
        Intent intent = new Intent(this, Main2Activity_Grafica.class);
        String idMES = String.valueOf(idSMES);
        intent.putExtra("row_id_cuenta", rowidCuenta);
        intent.putExtra("idMES", idMES);
        startActivity(intent);
    }

    /*********************  Toolbar y menu lateral ********************************/
    public void toolbarMenu() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Toogle de hamburguesa
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abrir, R.string.cerrar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_seleccion, listaSpinner);
        spinner.setAdapter(dataAdapter);

        // para colocar un valor predeterminado en el spinner para que muestre el ultimo valor gurdado en tabla preferencias
        int posicion = dataAdapter.getPosition(nombre_c);
        spinner.setSelection(posicion);
        baseDeDatos.close();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //para colocar e icono al estilo drawableLeft
                ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(icoSpinner, 0, 0, 0);

                //para cuando selecione el mismo nombre no haga nada
                if (parent.getItemAtPosition(position).equals(nombre_c)) {
                    //no hacer nada
                } else {
                    //String label = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion
                    int spinnerPosicion = parent.getSelectedItemPosition();

                    //Conexion a la base de datos
                    AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(MainActivity.this, "BDCartera", null, 1);
                    SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

                    fila = baseDeDatos.rawQuery("SELECT row_cuenta FROM preferencias ", null);
                    if (fila.moveToFirst()) {
                        int preferencias_rowid_cuenta = fila.getInt(0);

                        spinnerPosicion++;
                        ContentValues modificar = new ContentValues();
                        modificar.put("row_cuenta", spinnerPosicion);

                        // ahora hacemos la line apara poder modificar
                        baseDeDatos.update("preferencias", modificar, "row_cuenta=" + preferencias_rowid_cuenta, null);
                        baseDeDatos.close();

                        Intent Siguiente = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(Siguiente);
                        finish();

                        // para restar el activity sis pestañeo
                        /*finish();
                        MainActivity.this.recreate();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);*/

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*********************  MENU LATERAL ********************************/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Intent cambiar;
        switch (menuItem.getItemId()) {
            case R.id.item_categorias:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.categoriaToas), Toast.LENGTH_SHORT).show();
                cambiar = new Intent(this, Main2Activity_Categorias.class);
                startActivity(cambiar);
                break;
            case R.id.item_cuentas:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.cuentasToas), Toast.LENGTH_SHORT).show();
                cambiar = new Intent(this, Main2Activity_Cuentas.class);
                startActivity(cambiar);
                break;
            case R.id.item_fechas:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.fechasToas), Toast.LENGTH_SHORT).show();
                cambiar = new Intent(this, Main2Activity_Calendario.class);
                startActivity(cambiar);
                break;
            case R.id.item_ajustes:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.ajustesToas), Toast.LENGTH_SHORT).show();
                cambiar = new Intent(this, Main2Activity_Ajustes.class);
                startActivity(cambiar);
                break;
            default:
                break;
        }
        return false;
    }

    /*********************  Icono Action Bar ********************************/
    public boolean onCreateOptionsMenu(Menu vista) {
        getMenuInflater().inflate(R.menu.item_transferir, vista);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem vista) {
        int id = vista.getItemId();

        if (id == R.id.btn_transferir) {
            Toast.makeText(this, getResources().getString(R.string.transferenciaToas), Toast.LENGTH_SHORT).show();

            Intent Siguiente = new Intent(this, Main2Activity_Transferencia.class);
            startActivity(Siguiente);
            return true;
        }
        return super.onOptionsItemSelected(vista);
    }

    /*********************  Obtener el MES ********************************/
    private String getMes() {             // se vería así: miercoles 26/09/2018 05:30 p.m.
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /*********************  Obtener el AÑO ********************************/
    private String getAno() {             // se vería así: miercoles 26/09/2018 05:30 p.m.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /*********************  PAra escuchar los click de los botones de gastos ********************************/
    @Override
    public void onClick(View v) {
        //*********************  Conexion a la base de datos  ********************************/
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // Parametros para gastos del icono seleccionado
        fila = baseDeDatos.rawQuery("SELECT rowid FROM icono_gasto", null);
        if (fila.moveToFirst()) {
            int i = 0;
            do {
                if (v.getId() == btn_id[i]) {
                    int row = fila.getInt(0);
                    String rowString = String.valueOf(row);
                    String rowidCuenta = String.valueOf(rowidC);

                    Intent intent = new Intent(this, Main2Activity_Gasto.class);
                    intent.putExtra("row_id_cuenta", rowidCuenta);
                    intent.putExtra("row_id_icono", rowString);
                    startActivity(intent);
                }
                i++;
            } while (fila.moveToNext());
        }


        baseDeDatos.close();
    }
}

