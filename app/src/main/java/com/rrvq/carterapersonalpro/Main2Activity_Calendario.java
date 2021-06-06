package com.rrvq.carterapersonalpro;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Main2Activity_Calendario extends AppCompatActivity {

    private static final String CERO = "0";
    private static final String BARRA = "/";
    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    Toolbar toolbar;
    Cursor fila, g;
    Spinner spinnerMes, spinnerAno;
    int idSMES, numANO;
    String row_id_cuenta, guardaFecha;
    ImageButton ib_fecha;
    String tipo_moneda;

    int comparaMostrar = 0;

    AdaptadorListaExp mAdaptador;

    TextView tv_fecha, tv_gastoTotal, tv_ingresoTotal;
    float totalGasto = 0, totalIngreso = 0;

    int[] mesString = {R.string.MESs, R.string.enero, R.string.febrero, R.string.marzo, R.string.abril, R.string.mayo,
            R.string.junio, R.string.julio, R.string.agosto, R.string.setiembre, R.string.octubre, R.string.noviembre, R.string.diciembre};

    ArrayList<String> meses = new ArrayList<>();
    ArrayList<String> anos = new ArrayList<>();

    //para mostrar solo dos decimales
    DecimalFormat formato = new DecimalFormat("#.#");
    public static Activity fa;  // para llamar y fializar el activity desde otro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__calendario);

        fa = this; // para finalizar el activity llamandolo desde otro activity
        //*********************  Casting de los view ********************************/
        castin_view();
        //*********************  Toolbar  ********************************/
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.fechas) + "</font>"));

        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // recibo la cuenta que esta seleccionada
        fila = baseDeDatos.rawQuery("SELECT row_cuenta FROM preferencias", null);
        if (fila.moveToFirst()) {
            int id = fila.getInt(0);
            row_id_cuenta = String.valueOf(id);
        }

        //*********************  MOSTAR EL ESPINNER DE LOS MESES ********************************/
        setSpinnerMes();
        setSpinnerAno();


        //*********************  MOSTRAR LA LISTA EXANDIBLE ********************************/
        initVistas();

        baseDeDatos.close();

    }

    /*********************  Casting de los view ********************************/
    public void castin_view() {
        toolbar = findViewById(R.id.toolbar);

        spinnerMes = findViewById(R.id.view_spinnerMes);
        spinnerAno = findViewById(R.id.view_spinnerAno);

        tv_fecha = findViewById(R.id.tv_fecha);
        ib_fecha = findViewById(R.id.ib_fecha);

        tv_gastoTotal = findViewById(R.id.tv_gasto_total);
        tv_ingresoTotal = findViewById(R.id.tv_ingreso_total);


    }

    /*********************  Flecha ATRAS color BLANCA ********************************/
    public void flechaBlanca() {

        // pra colocar la flecha de color blanco de volver
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // flecha de volver atras
    }

    public void fechasListas(View vistas) {

        if (vistas.getId() == R.id.ib_fecha){
            comparaMostrar = 3;
            obtenerFecha();
        }
    } // para mostrar por fechas

    public void setSpinnerMes() {

        for (int i = 0; i < mesString.length; i++) {

            meses.add(getString(mesString[i]));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_seleccion, meses);
        // ahora hay que asignar al spiner el opjeto adapter que cremos en la linea de arriba
        spinnerMes.setAdapter(adapter);

        // para colocar un valor predeterminado en el spinner para que muestre el ultimo valor gurdado en tabla preferencias
        for (int i = 1; i <= mesString.length; i++) {

            int d = Integer.parseInt(getMes());
            if (d == i) {

                int posicion = adapter.getPosition(getString(mesString[i]));
                spinnerMes.setSelection(posicion);

            }

        }

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals(getResources().getString(R.string.MESs))) {
                    //no hacer nada
                } else {
//                    String label = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion
                    /*int spinnerPosicion = parent.getSelectedItemPosition();
                    idSMES = spinnerPosicion;*/

                    idSMES = parent.getSelectedItemPosition();
                    comparaMostrar = 0;  // para comarar solo por mes
                    tv_fecha.setText("");
                    totalGasto = 0;
                    totalIngreso = 0;
                    initVistas();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void setSpinnerAno() {

        anos.add(getResources().getString(R.string.ano));
        for (int i = 5; i > 0; i--) {
            String a = getAno();
            int ano = Integer.parseInt(a);
            ano = ano - i;
            anos.add(String.valueOf(ano));
        }
        for (int i = 0; i < 5; i++) {
            String a = getAno();
            int ano = Integer.parseInt(a);
            ano = ano + i;
            anos.add(String.valueOf(ano));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_seleccion, anos);
        // ahora hay que asignar al spiner el opjeto adapter que cremos en la linea de arriba
        spinnerAno.setAdapter(adapter);

        int posicion = adapter.getPosition(getAno());
        spinnerAno.setSelection(posicion);

        spinnerAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals(getResources().getString(R.string.ano))) {
                    //no hacer nada
                } else {
                    String label = parent.getItemAtPosition(position).toString();  // recupera el nombre de la seleccion

                    numANO = Integer.parseInt(label);
                    comparaMostrar = 0;  // para comarar solo por mes y año
                    tv_fecha.setText("");
                    totalGasto = 0;
                    totalIngreso = 0;
                    initVistas();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // Obtiene e inicializa las vistas.
    private void initVistas() {
        ExpandableListView listaExp = this.findViewById(R.id.ViewlistaExpandible);
        // No se usarán los indicadores por defecto para grupos e hijos.
        if (listaExp != null) {
            listaExp.setGroupIndicator(null);
            listaExp.setChildIndicator(null);
            // Se obtienen los datos.
            ArrayList<GrupoPadre> grupos = new ArrayList<>();
            ArrayList<ArrayList<GrupoHijo>> hijos = new ArrayList<>();
            fillDatos(grupos, hijos);
            // Se crea el adaptador para la lista y se establece.
            mAdaptador = new AdaptadorListaExp(this, grupos, hijos);
            listaExp.setAdapter(mAdaptador);
            // para escuchar los clicks de los hijos
//            listaExp.setOnChildClickListener(this);
            //para escuchar los click de los items
            listaExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    // Se obtiene el hijo pulsado.
                    GrupoHijo GHijo = mAdaptador.getChild(groupPosition, childPosition);
                    // toas con la informacion que pulso el usuario o hijo que pulso
                    /*Toast.makeText(Main2Activity_Calendario.this, getResources().getString(R.string.info)+" "+GHijo.getMontoHijo()+" "+
                            GHijo.getDescripcionHijo()+" "+GHijo.getFechaHijo()+" "+formato.format(GHijo.getRowid())+" "+
                            GHijo.getTipo(), Toast.LENGTH_SHORT).show();*/
                    // Se retorna true para indicar que el evento ya ha sido gestionado.

                    Intent Siguiente;
                    String rowid;
                    switch (GHijo.getTipo()) {
                        case "t":
                            Siguiente = new Intent(Main2Activity_Calendario.this, Main2Activity_Transferencia_edit.class);
                            rowid = String.valueOf(GHijo.getRowid());
                            Siguiente.putExtra("row_id_transferencia", rowid);
                            startActivity(Siguiente);
                            break;
                        case "i":
                            Siguiente = new Intent(Main2Activity_Calendario.this, Main2Activity_Ingreso_edit.class);
                            rowid = String.valueOf(GHijo.getRowid());
                            Siguiente.putExtra("row_id_ingreso", rowid);
                            startActivity(Siguiente);
                            break;
                        case "g":
                            Siguiente = new Intent(Main2Activity_Calendario.this, Main2Activity_Gasto_edit.class);
                            rowid = String.valueOf(GHijo.getRowid());
                            Siguiente.putExtra("row_id_gasto", rowid);
                            startActivity(Siguiente);
                            break;
                    }

                    return true;
                }
            });
        }
    }

    // Obtiene los ArrayList de datos para grupos e hijos. Modifica los
    // parámetros recibidos.
    private void fillDatos(ArrayList<GrupoPadre> grupos,
                           ArrayList<ArrayList<GrupoHijo>> hijos) {

        ArrayList<GrupoHijo> grupoActual = null;

        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        // recupero el tipo de moneda
        fila = baseDeDatos.rawQuery("SELECT tipo_moneda FROM preferencias", null);
        if (fila.moveToFirst()) {
            tipo_moneda = fila.getString(0);
        }


        // Calculamos los montos para mostrar los INGRESOS de las TRANSFERENCIAS
        g = baseDeDatos.rawQuery("SELECT rowid, fecha_transf, monto_transf, desc_transf FROM transferencia where hasta_row=" + row_id_cuenta, null);
        if (g.moveToFirst()) {
            float montoPadre;
            float suma = 0;
            grupoActual = new ArrayList<>();
            do {

                int rowid = g.getInt(0);
                String fe = g.getString(1);
                Float m = g.getFloat(2);
                String d = g.getString(3);
                String tipo = "t";

                String f = "";
                for (int i = 0; i < 5; i++) {
                    f = f + String.valueOf(fe.charAt(i));
                }

                if (comparaMostrar == 0) { // mostrar mes y año

                    String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                    int intfe = Integer.parseInt(compfe);

                    String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                            String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                    int intA = Integer.parseInt(comA);

//                                if (getDate().charAt(3) == fe.charAt(3) && getDate().charAt(4) == fe.charAt(4)) {
                    if (idSMES == intfe && numANO == intA) {

                        grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                        suma = suma + g.getFloat(2);
                    }
                } else if (comparaMostrar == 3) { // mostrar por fecha exacta
                    if (fe.equals(guardaFecha)) {

                        grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                        suma = suma + g.getFloat(2);
                    }
                }

            } while (g.moveToNext());
            montoPadre = suma;

            if (montoPadre > 0) {
                totalIngreso = totalIngreso + montoPadre;
                int ic = R.drawable.transferencia24_barra;
                String n = getResources().getString(R.string.transferencia);
                grupos.add(new GrupoPadre(ic, n, montoPadre, 1));
                hijos.add(grupoActual);
            }

            // muestro el total del gasto
            String mostrar = getResources().getString(R.string.ingresoT) + " " + tipo_moneda + " " + formato.format(totalIngreso);
            tv_ingresoTotal.setText(mostrar);
        }


        // Calculamos los montos para mostrar los INGRESOS
        fila = baseDeDatos.rawQuery("SELECT rowid, icon_ingreso, inombre_ingreso FROM icono_ingreso", null);
        if (fila.moveToFirst()) {
            int rowfila, rowg;
            float montoPadre = 0;
            do {
                rowfila = fila.getInt(0);
                g = baseDeDatos.rawQuery("SELECT rowid, row_icon_ingreso, fecha_ingreso, desc_ingreso, monto_ingreso FROM ingreso where row_cuenta=" + row_id_cuenta, null);
                if (g.moveToFirst()) {
                    float suma = 0;
                    grupoActual = new ArrayList<>();
                    do {

                        int rowid = g.getInt(0);
                        rowg = g.getInt(1);
                        String fe = g.getString(2);
                        String d = g.getString(3);
                        Float m = g.getFloat(4);
                        String tipo = "i";

                        String f = "";
                        for (int i = 0; i < 5; i++) {
                            f = f + String.valueOf(fe.charAt(i));
                        }

                        if (rowfila == rowg) {

                            if (comparaMostrar == 0) { // mostrar mes y año

                                String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                                int intfe = Integer.parseInt(compfe);

                                String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                                        String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                                int intA = Integer.parseInt(comA);

//                                if (getDate().charAt(3) == fe.charAt(3) && getDate().charAt(4) == fe.charAt(4)) {
                                if (idSMES == intfe && numANO == intA) {

                                    grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                                    suma = suma + g.getFloat(4);
                                }
                            } else if (comparaMostrar == 3) { // mostrar por fecha exacta
                                if (fe.equals(guardaFecha)) {

                                    grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                                    suma = suma + g.getFloat(4);
                                }
                            }

                        }

                    } while (g.moveToNext());
                    montoPadre = suma;
                }
                if (montoPadre > 0) {
                    totalIngreso = totalIngreso + montoPadre;
                    int ic = fila.getInt(1);
                    String n = fila.getString(2);
                    grupos.add(new GrupoPadre(ic, n, montoPadre, 1));
                    hijos.add(grupoActual);
                }

            } while (fila.moveToNext());
            // muestro el total del gasto
            String mostrar = getResources().getString(R.string.ingresoT) + " " + tipo_moneda + " " + formato.format(totalIngreso);
            tv_ingresoTotal.setText(mostrar);

        }


        // Calculamos los montos para mostrar los GASTOS de las TRANSFERENCIAS
        g = baseDeDatos.rawQuery("SELECT rowid, fecha_transf, monto_transf, desc_transf FROM transferencia where desde_row=" + row_id_cuenta, null);
        if (g.moveToFirst()) {
            float montoPadre;
            float suma = 0;
            grupoActual = new ArrayList<>();
            do {

                int rowid = g.getInt(0);
                String fe = g.getString(1);
                Float m = g.getFloat(2);
                String d = g.getString(3);
                String f = "";
                String tipo = "t";

                for (int i = 0; i < 5; i++) {
                    f = f + String.valueOf(fe.charAt(i));
                }

                if (comparaMostrar == 0) { // mostrar mes y año

                    String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                    int intfe = Integer.parseInt(compfe);

                    String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                            String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                    int intA = Integer.parseInt(comA);

                    if (idSMES == intfe && numANO == intA) {
                        grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                        suma = suma + g.getFloat(2);
                    }
                } else if (comparaMostrar == 3) { // mostrar por fecha exacta
                    if (fe.equals(guardaFecha)) {
                        grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                        suma = suma + g.getFloat(2);
                    }
                }

            } while (g.moveToNext());
            montoPadre = suma;

            if (montoPadre > 0) {
                totalGasto = totalGasto + montoPadre;
                int ic = R.drawable.transferencia24_barra;
                String n = getResources().getString(R.string.transferencia);
                grupos.add(new GrupoPadre(ic, n, montoPadre, 0));
                hijos.add(grupoActual);
            }

            // muestro el total del gasto
            String mostrar = getResources().getString(R.string.gastototal) + " " + tipo_moneda + " " + formato.format(totalGasto);
            tv_gastoTotal.setText(mostrar);
        }


        // Calculamos los montos para mostrar los GASTOS
        fila = baseDeDatos.rawQuery("SELECT rowid, icon_gasto, inombre_gasto FROM icono_gasto", null);
        if (fila.moveToFirst()) {
            int rowfila, rowg;
            float montoPadre = 0;
            do {
                rowfila = fila.getInt(0);
                g = baseDeDatos.rawQuery("SELECT rowid, row_icon_gasto, fecha_gasto, desc_gasto, monto_gasto FROM gasto where row_cuenta=" + row_id_cuenta, null);
                if (g.moveToFirst()) {
                    float suma = 0;
                    grupoActual = new ArrayList<>();
                    do {
                        int rowid = g.getInt(0);
                        rowg = g.getInt(1);
                        String fe = g.getString(2);
                        String d = g.getString(3);
                        Float m = g.getFloat(4);
                        String tipo = "g";

                        String f = "";
                        for (int i = 0; i < 5; i++) {
                            f = f + String.valueOf(fe.charAt(i));
                        }


                        if (rowfila == rowg) {

                            if (comparaMostrar == 0) { // mostrar mes y año


                                String compfe = String.valueOf(fe.charAt(3)) + String.valueOf(fe.charAt(4));
                                int intfe = Integer.parseInt(compfe);

                                String comA = String.valueOf(fe.charAt(6)) + String.valueOf(fe.charAt(7)) +
                                        String.valueOf(fe.charAt(8)) + String.valueOf(fe.charAt(9));
                                int intA = Integer.parseInt(comA);

                                if (idSMES == intfe && numANO == intA) {

                                    grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                                    suma = suma + g.getFloat(4);
                                }
                            } else if (comparaMostrar == 3) { // mostrar por fecha exacta
                                if (fe.equals(guardaFecha)) {
                                    grupoActual.add(new GrupoHijo(rowid, m, d, f, tipo));
                                    suma = suma + g.getFloat(3);
                                }
                            }

                        }

                    } while (g.moveToNext());
                    montoPadre = suma;
                }
                if (montoPadre > 0) {
                    totalGasto = totalGasto + montoPadre;
                    int ic = fila.getInt(1);
                    String n = fila.getString(2);
                    grupos.add(new GrupoPadre(ic, n, montoPadre, 0));
                    hijos.add(grupoActual);
                }

            } while (fila.moveToNext());
            // muestro el total del gasto
            String mostrar = getResources().getString(R.string.gastototal) + " " + tipo_moneda + " " + formato.format(totalGasto);
            tv_gastoTotal.setText(mostrar);

        }


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
                spinnerMes.setSelection(0);
                spinnerAno.setSelection(0);
                idSMES = 0;
                numANO = 0;
                initVistas();

            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            //También puede cargar los valores que usted desee

        }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    //*********************  Obtener fecha en el oncreate ********************************/
    /*private String getDate() {             // se vería así: miercoles 26/09/2018 05:30 p.m.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }*/

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
