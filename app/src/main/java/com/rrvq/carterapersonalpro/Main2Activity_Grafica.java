package com.rrvq.carterapersonalpro;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main2Activity_Grafica extends AppCompatActivity {

    Toolbar toolbar;
    Cursor fila, g;

    String row_id_cuenta, nombreC, idMES;
    int numANO, idSMES;

    AnyChartView anyChartView;
   /* String gastos[]={"hola", "pera", "gato"};
    float valores[]={10,50,30};*/

    // lo declaro global para usarlo en la clase y el oncreate
    List<DataEntry> dataEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__grafica);

        toolbar = findViewById(R.id.toolbar);
        anyChartView = findViewById(R.id.any_chart_view);

        //Toolbar menu lateral
        setSupportActionBar(toolbar);
        flechaBlanca();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+ getResources().getString(R.string.graficaGastos) +"</font>"));

        numANO = Integer.parseInt(getAno());
        row_id_cuenta = getIntent().getStringExtra("row_id_cuenta");
        idMES = getIntent().getStringExtra("idMES");
        idSMES = Integer.parseInt(idMES);


        //Conexion a la base de datos
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

        fila = baseDeDatos.rawQuery("SELECT nombre_cuenta FROM cuenta WHERE rowid=" + row_id_cuenta, null);
        if (fila.moveToFirst()) {
            nombreC = fila.getString(0);
        }

        // calculamos el porcentaje para  los gastos de la cuenta y del mes
        fila = baseDeDatos.rawQuery("SELECT rowid, inombre_gasto FROM icono_gasto", null);
        int rowfila, rowg;
        if (fila.moveToFirst()) {
            do {
                rowfila = fila.getInt(0);
                float tSuma = 0;
                g = baseDeDatos.rawQuery("SELECT row_icon_gasto, fecha_gasto, monto_gasto FROM gasto where row_cuenta=" + row_id_cuenta, null);
                if (g.moveToFirst()) {
                    float suma = 0;
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
                }
                if (tSuma > 0) {
                    String gasto = fila.getString(1);
                    dataEntries.add(new ValueDataEntry(gasto, tSuma));  // para ingresar dato al list del pastel
                }

            } while (fila.moveToNext());
        }

        // calculamos el porcentaje para la transferencias del mes
        fila = baseDeDatos.rawQuery("SELECT fecha_transf, monto_transf FROM transferencia WHERE desde_row=" + row_id_cuenta, null);
        float montoT = 0;
        if (fila.moveToFirst()) {
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

            if (montoT > 0) {
                String t = getResources().getString(R.string.trasnsferenciaGrafica);
                dataEntries.add(new ValueDataEntry(t, montoT));  // para ingresar dato al list del pastel
            }
        }


        // GRAFICO DE PASTEL
        graficoPastel();
    }

    /*********************  Flecha ATRAS color BLANCA ********************************/
    public void flechaBlanca() {

        // pra colocar la flecha de color blanco de volver
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // flecha de volver atras
    }

    /*********************  Boton Gastos ********************************/
    public void btnGastos(View vista) {
        Intent Siguiente = new Intent(this, MainActivity.class);
        Siguiente.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(Siguiente);
    }

    /*********************  GRAFICO DE PASTEL ********************************/
    public void graficoPastel() {

        Pie pie = AnyChart.pie();


        //para escuchar los clcks de los pie
        /*pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(Main2Activity_Grafica.this, event.getData().get("x") + ":"
                        + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });*/

        /*List<DataEntry> dataEntries = new ArrayList<>();

        for (int i=0; i<gastos.length; i++){
            dataEntries.add(new ValueDataEntry(gastos[i], valores[i]));
        }*/

        pie.data(dataEntries);

        pie.title(getResources().getString(R.string.gastomes) + " -- " + nombreC);
        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text(getResources().getString(R.string.categoria))
                .padding(0d, 0d, 10d, 0d);

        /*// para cambiar formatoa  los tooltips
        pie.tooltip().useHtml(true);
        pie.tooltip().background("green");
        pie.tooltip().format("Monto: {%value} \\n Porcentaje: {%YPercentOfTotal} %");*/

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                .align(Align.CENTER);

        anyChartView.setChart(pie);
    }


    /*********************  Obtener el AÑO ********************************/
    private String getAno() {             // se vería así: miercoles 26/09/2018 05:30 p.m.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
