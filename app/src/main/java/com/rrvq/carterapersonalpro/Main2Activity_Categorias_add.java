package com.rrvq.carterapersonalpro;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Main2Activity_Categorias_add extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Cursor fila;

    ImageButton[] castinBtn;
    ImageView iv_seleccionada;
    EditText et_nombre;

    // recibimos lo que se envia de otro activity ara decidir que hacer
    String dato;

    int id_img = 0, id_img_spinner = 0; // para guardar el id de img de la cuenta
    int icount; //para comparar el cursor de la tabla preferencias

    //id de las imagenes de la categoria
    int[] imgGastosIngresos = {R.drawable.ca0, R.drawable.ca1, R.drawable.ca2, R.drawable.ca3, R.drawable.ca4, R.drawable.ca5, R.drawable.ca6, R.drawable.ca7, R.drawable.ca8, R.drawable.ca9,
            R.drawable.ca10, R.drawable.ca11, R.drawable.ca12, R.drawable.ca13, R.drawable.ca14, R.drawable.ca15, R.drawable.ca16, R.drawable.ca17, R.drawable.ca18, R.drawable.ca19,
            R.drawable.ca20, R.drawable.ca21, R.drawable.ca22, R.drawable.ca23, R.drawable.ca24, R.drawable.ca25, R.drawable.ca26, R.drawable.ca27, R.drawable.ca28, R.drawable.ca29,
            R.drawable.ca30, R.drawable.ca31, R.drawable.ca32, R.drawable.ca33, R.drawable.ca34, R.drawable.ca35, R.drawable.ca36, R.drawable.ca37, R.drawable.ca38, R.drawable.ca39,
            R.drawable.ca40, R.drawable.ca41, R.drawable.ca42, R.drawable.ca43, R.drawable.ca44, R.drawable.ca45, R.drawable.ca46, R.drawable.ca47, R.drawable.ca48, R.drawable.ca49,
            R.drawable.ca50, R.drawable.ca51, R.drawable.ca52, R.drawable.ca53, R.drawable.ca54, R.drawable.ca55, R.drawable.ca56, R.drawable.ca57, R.drawable.ca58, R.drawable.ca59,
            R.drawable.ca60, R.drawable.ca61, R.drawable.ca62, R.drawable.ca63, R.drawable.ca64, R.drawable.ca65, R.drawable.ca66, R.drawable.ca67, R.drawable.ca68, R.drawable.ca69,
            R.drawable.ca70, R.drawable.ca71, R.drawable.ca72, R.drawable.ca73, R.drawable.ca74, R.drawable.ca75, R.drawable.ca76, R.drawable.ca77, R.drawable.ca78, R.drawable.ca79};
    //id de las imagenes de la cuenta y el spinner
    int[] imgCuentas = {R.drawable.cu0, R.drawable.cu1, R.drawable.cu2, R.drawable.cu3, R.drawable.cu4, R.drawable.cu5, R.drawable.cu6, R.drawable.cu7, R.drawable.cu8, R.drawable.cu9,
            R.drawable.cu10, R.drawable.cu11, R.drawable.cu12, R.drawable.cu13, R.drawable.cu14, R.drawable.cu15};
    int[] imgSpinner = {R.drawable.s0, R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4, R.drawable.s5, R.drawable.s6, R.drawable.s7, R.drawable.s8, R.drawable.s9,
            R.drawable.s10, R.drawable.s11, R.drawable.s12, R.drawable.s13, R.drawable.s14, R.drawable.s15};
    // id de los botones view
    int[] btn_id = {R.id.btn_img_1, R.id.btn_img_2, R.id.btn_img_3, R.id.btn_img_4, R.id.btn_img_5, R.id.btn_img_6, R.id.btn_img_7, R.id.btn_img_8, R.id.btn_img_9, R.id.btn_img_10,
            R.id.btn_img_11, R.id.btn_img_12, R.id.btn_img_13, R.id.btn_img_14, R.id.btn_img_15, R.id.btn_img_16, R.id.btn_img_17, R.id.btn_img_18, R.id.btn_img_19, R.id.btn_img_20,
            R.id.btn_img_21, R.id.btn_img_22, R.id.btn_img_23, R.id.btn_img_24, R.id.btn_img_25, R.id.btn_img_26, R.id.btn_img_27, R.id.btn_img_28, R.id.btn_img_29, R.id.btn_img_30,
            R.id.btn_img_31, R.id.btn_img_32, R.id.btn_img_33, R.id.btn_img_34, R.id.btn_img_35, R.id.btn_img_36, R.id.btn_img_37, R.id.btn_img_38, R.id.btn_img_39, R.id.btn_img_40,
            R.id.btn_img_41, R.id.btn_img_42, R.id.btn_img_43, R.id.btn_img_44, R.id.btn_img_45, R.id.btn_img_46, R.id.btn_img_47, R.id.btn_img_48, R.id.btn_img_49, R.id.btn_img_50,
            R.id.btn_img_51, R.id.btn_img_52, R.id.btn_img_53, R.id.btn_img_54, R.id.btn_img_55, R.id.btn_img_56, R.id.btn_img_57, R.id.btn_img_58, R.id.btn_img_59, R.id.btn_img_60,
            R.id.btn_img_61, R.id.btn_img_62, R.id.btn_img_63, R.id.btn_img_64, R.id.btn_img_65, R.id.btn_img_66, R.id.btn_img_67, R.id.btn_img_68, R.id.btn_img_69, R.id.btn_img_70,
            R.id.btn_img_71, R.id.btn_img_72, R.id.btn_img_73, R.id.btn_img_74, R.id.btn_img_75, R.id.btn_img_76, R.id.btn_img_77, R.id.btn_img_78, R.id.btn_img_79, R.id.btn_img_80};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__categorias_add);

        //*********************  Casting de los view ********************************/
        castin_view();

        // apertura de a base de datos para que leea y escriba
        AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();
        // para verificar que no alla registros en la tabla inicio
        Cursor fila_preferencias = baseDeDatos.rawQuery("SELECT count(*) FROM preferencias", null);
        if (fila_preferencias.moveToFirst()) {
            icount = fila_preferencias.getInt(0);
        }

        if (icount > 0) {
            //*********************  Toolbar  ********************************/
            setSupportActionBar(toolbar);

            // recibimos lo que se envia de otro activity ara decidir que hacer
            dato = getIntent().getStringExtra("dato");

            // para mostrar los icnonos y el actionBar
            switch (dato) {
                case "gasto":
                    flechaBlanca();
                    getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.nuevoGasto) + "</font>"));

                    //*********************  Mostrar imagenes categorias y escuchar los clicks ********************************/
                    imagenesGastosIngresos();
                    break;
                case "ingreso":
                    flechaBlanca();
                    getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.nuevoIngreso) + "</font>"));

                    //*********************  Mostrar imagenes categorias y escuchar los clicks ********************************/
                    imagenesGastosIngresos();
                    break;
                case "cuenta":
                    flechaBlanca();
                    getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.nuevaCuenta) + "</font>"));

                    //*********************  Mostrar imagenes categorias y escuchar los clicks ********************************/
                    imagenesCuenta();
                    break;

                default:
                    break;
            }

            baseDeDatos.close();

        } else {

            //*********************  Toolbar  ********************************/
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + getResources().getString(R.string.crearcuenta) + "</font>"));

            //*********************  Mostrar imagenes de las Cuentas y spinner ********************************/
            imagenesCuenta();

        }
        baseDeDatos.close();

    }

    /*********************  Casting de los view ********************************/
    public void castin_view() {

        toolbar = findViewById(R.id.toolbar);
//        toolbar2 = (Toolbar)findViewById(R.id.toolbar2);
        iv_seleccionada = findViewById(R.id.iv_selecionada);
        et_nombre = findViewById(R.id.et_nombre);

        castinBtn = new ImageButton[]{
                findViewById(R.id.btn_img_1), findViewById(R.id.btn_img_2),
                findViewById(R.id.btn_img_3), findViewById(R.id.btn_img_4),
                findViewById(R.id.btn_img_5), findViewById(R.id.btn_img_6),
                findViewById(R.id.btn_img_7), findViewById(R.id.btn_img_8),
                findViewById(R.id.btn_img_9), findViewById(R.id.btn_img_10),
                findViewById(R.id.btn_img_11), findViewById(R.id.btn_img_12),
                findViewById(R.id.btn_img_13), findViewById(R.id.btn_img_14),
                findViewById(R.id.btn_img_15), findViewById(R.id.btn_img_16),
                findViewById(R.id.btn_img_17), findViewById(R.id.btn_img_18),
                findViewById(R.id.btn_img_19), findViewById(R.id.btn_img_20),
                findViewById(R.id.btn_img_21), findViewById(R.id.btn_img_22),
                findViewById(R.id.btn_img_23), findViewById(R.id.btn_img_24),
                findViewById(R.id.btn_img_25), findViewById(R.id.btn_img_26),
                findViewById(R.id.btn_img_27), findViewById(R.id.btn_img_28),
                findViewById(R.id.btn_img_29), findViewById(R.id.btn_img_30),
                findViewById(R.id.btn_img_31), findViewById(R.id.btn_img_32),
                findViewById(R.id.btn_img_33), findViewById(R.id.btn_img_34),
                findViewById(R.id.btn_img_35), findViewById(R.id.btn_img_36),
                findViewById(R.id.btn_img_37), findViewById(R.id.btn_img_38),
                findViewById(R.id.btn_img_39), findViewById(R.id.btn_img_40),
                findViewById(R.id.btn_img_41), findViewById(R.id.btn_img_42),
                findViewById(R.id.btn_img_43), findViewById(R.id.btn_img_44),
                findViewById(R.id.btn_img_45), findViewById(R.id.btn_img_46),
                findViewById(R.id.btn_img_47), findViewById(R.id.btn_img_48),
                findViewById(R.id.btn_img_49), findViewById(R.id.btn_img_50),
                findViewById(R.id.btn_img_51), findViewById(R.id.btn_img_52),
                findViewById(R.id.btn_img_53), findViewById(R.id.btn_img_54),
                findViewById(R.id.btn_img_55), findViewById(R.id.btn_img_56),
                findViewById(R.id.btn_img_57), findViewById(R.id.btn_img_58),
                findViewById(R.id.btn_img_59), findViewById(R.id.btn_img_60),
                findViewById(R.id.btn_img_61), findViewById(R.id.btn_img_62),
                findViewById(R.id.btn_img_63), findViewById(R.id.btn_img_64),
                findViewById(R.id.btn_img_65), findViewById(R.id.btn_img_66),
                findViewById(R.id.btn_img_67), findViewById(R.id.btn_img_68),
                findViewById(R.id.btn_img_69), findViewById(R.id.btn_img_70),
                findViewById(R.id.btn_img_71), findViewById(R.id.btn_img_72),
                findViewById(R.id.btn_img_73), findViewById(R.id.btn_img_74),
                findViewById(R.id.btn_img_75), findViewById(R.id.btn_img_76),
                findViewById(R.id.btn_img_77), findViewById(R.id.btn_img_78),
                findViewById(R.id.btn_img_79), findViewById(R.id.btn_img_80)};
    }

    /*********************  Flecha ATRAS color BLANCA ********************************/
    public void flechaBlanca() {

        // pra colocar la flecha de color blanco de volver
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // flecha de volver atras
    }


    /*********************  Icono Action Bar ********************************/
    public boolean onCreateOptionsMenu(Menu vista) {
        getMenuInflater().inflate(R.menu.item_add, vista);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem vista) {
        int id = vista.getItemId();

        if (id == R.id.btn_agregar) {

            // apertura de a base de datos para que leea y escriba
            AdminSQLiteOpenHelper adminDB = new AdminSQLiteOpenHelper(this, "BDCartera", null, 1);
            SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();

            //para guardar solo si es una cuenta nueva
            if (icount == 0) {
                String nombre = et_nombre.getText().toString();
                if (!nombre.isEmpty() && id_img != 0) {

                    //agrego datos a la primera entrada y para recuperar spinner
                    ContentValues add_preferencias = new ContentValues();
                    add_preferencias.put("row_cuenta", 1);
                    add_preferencias.put("tipo_moneda", "$");
                    baseDeDatos.insert("preferencias", null, add_preferencias);

                    //agrego datos de la primera cuenta que se va a crear
                    ContentValues add_cuenta = new ContentValues();
                    add_cuenta.put("nombre_cuenta", nombre);
//                    add_cuenta.put("fecha_cuenta", getDate()); // para guardar que la cree en una class privada abajo
                    add_cuenta.put("icon_cuenta", id_img);
                    add_cuenta.put("icon_spinner", id_img_spinner);
                    add_cuenta.put("disponible_cuenta", 0);
                    baseDeDatos.insert("cuenta", null, add_cuenta);

                    // INGRESOS agrego los iconos que se cargaran por defecto a la primera entrada para los ingresos
                    ContentValues icon_i1 = new ContentValues();
                    icon_i1.put("icon_ingreso", R.drawable.ca29);
                    icon_i1.put("inombre_ingreso", "Depositos");
                    ContentValues icon_i2 = new ContentValues();
                    icon_i2.put("icon_ingreso", R.drawable.ca1);
                    icon_i2.put("inombre_ingreso", "Salario");
                    ContentValues icon_i3 = new ContentValues();
                    icon_i3.put("icon_ingreso", R.drawable.ca30);
                    icon_i3.put("inombre_ingreso", "Propina");
                    ContentValues icon_i4 = new ContentValues();
                    icon_i4.put("icon_ingreso", R.drawable.ca0);
                    icon_i4.put("inombre_ingreso", "Ahorro");
                    baseDeDatos.insert("icono_ingreso", null, icon_i1);
                    baseDeDatos.insert("icono_ingreso", null, icon_i2);
                    baseDeDatos.insert("icono_ingreso", null, icon_i3);
                    baseDeDatos.insert("icono_ingreso", null, icon_i4);

                    // GASTOS agrego los iconos que se cargaran por defecto a la primera entrada ara los gastos
                    ContentValues icon_1 = new ContentValues();
                    icon_1.put("icon_gasto", R.drawable.ca36);
                    icon_1.put("inombre_gasto", "Casa");
                    ContentValues icon_2 = new ContentValues();
                    icon_2.put("icon_gasto", R.drawable.ca23);
                    icon_2.put("inombre_gasto", "Mercado");
                    ContentValues icon_3 = new ContentValues();
                    icon_3.put("icon_gasto", R.drawable.ca6);
                    icon_3.put("inombre_gasto", "Carro");
                    ContentValues icon_4 = new ContentValues();
                    icon_4.put("icon_gasto", R.drawable.ca5);
                    icon_4.put("inombre_gasto", "Gasolina");
                    ContentValues icon_5 = new ContentValues();
                    icon_5.put("icon_gasto", R.drawable.ca22);
                    icon_5.put("inombre_gasto", "Restaurante");
                    ContentValues icon_6 = new ContentValues();
                    icon_6.put("icon_gasto", R.drawable.ca2);
                    icon_6.put("inombre_gasto", "Bodega");
                    ContentValues icon_7 = new ContentValues();
                    icon_7.put("icon_gasto", R.drawable.ca3);
                    icon_7.put("inombre_gasto", "Pareja");
                    ContentValues icon_8 = new ContentValues();
                    icon_8.put("icon_gasto", R.drawable.ca40);
                    icon_8.put("inombre_gasto", "Ropa");
                    ContentValues icon_9 = new ContentValues();
                    icon_9.put("icon_gasto", R.drawable.ca8);
                    icon_9.put("inombre_gasto", "Taxi");
                    ContentValues icon_10 = new ContentValues();
                    icon_10.put("icon_gasto", R.drawable.ca31);
                    icon_10.put("inombre_gasto", "Facturas");
                    ContentValues icon_11 = new ContentValues();
                    icon_11.put("icon_gasto", R.drawable.ca69);
                    icon_11.put("inombre_gasto", "Gym");
                    ContentValues icon_12 = new ContentValues();
                    icon_12.put("icon_gasto", R.drawable.ca44);
                    icon_12.put("inombre_gasto", "Salud");
                    baseDeDatos.insert("icono_gasto", null, icon_1);
                    baseDeDatos.insert("icono_gasto", null, icon_2);
                    baseDeDatos.insert("icono_gasto", null, icon_3);
                    baseDeDatos.insert("icono_gasto", null, icon_4);
                    baseDeDatos.insert("icono_gasto", null, icon_5);
                    baseDeDatos.insert("icono_gasto", null, icon_6);
                    baseDeDatos.insert("icono_gasto", null, icon_7);
                    baseDeDatos.insert("icono_gasto", null, icon_8);
                    baseDeDatos.insert("icono_gasto", null, icon_9);
                    baseDeDatos.insert("icono_gasto", null, icon_10);
                    baseDeDatos.insert("icono_gasto", null, icon_11);
                    baseDeDatos.insert("icono_gasto", null, icon_12);


                    baseDeDatos.close();

                    Intent Siguiente = new Intent(this, MainActivity.class);
                    startActivity(Siguiente);
                    Toast.makeText(this, getResources().getString(R.string.cuentaadd), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.debeagregarnombreyimagen), Toast.LENGTH_SHORT).show();
                }
            } else if (icount > 0) {
                String nombre;
                switch (dato) {
                    case "gasto":
                        nombre = et_nombre.getText().toString();

                        if (!nombre.isEmpty() && id_img != 0) {

                            //agrego datos a la primera entrada y para recuperar spinner
                            ContentValues add_iGastos = new ContentValues();
                            add_iGastos.put("icon_gasto", id_img);
                            add_iGastos.put("inombre_gasto", nombre);
                            baseDeDatos.insert("icono_gasto", null, add_iGastos);

                            baseDeDatos.close();

                            Main2Activity_Categorias.fa.finish();
                            Intent Siguiente = new Intent(this, Main2Activity_Categorias.class);
                            startActivity(Siguiente);
                            Toast.makeText(this, getResources().getString(R.string.gastoadd), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.debeagregarnombreyimagen), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "ingreso":
                        nombre = et_nombre.getText().toString();

                        if (!nombre.isEmpty() && id_img != 0) {

                            //agrego datos a la primera entrada y para recuperar spinner
                            ContentValues add_iIngreso = new ContentValues();
                            add_iIngreso.put("icon_ingreso", id_img);
                            add_iIngreso.put("inombre_ingreso", nombre);
                            baseDeDatos.insert("icono_ingreso", null, add_iIngreso);

                            baseDeDatos.close();

                            Main2Activity_Categorias.fa.finish();
                            Intent Siguiente = new Intent(this, Main2Activity_Categorias.class);
                            startActivity(Siguiente);
                            Toast.makeText(this, getResources().getString(R.string.ingresoadd), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.debeagregarnombreyimagen), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "cuenta":
                        nombre = et_nombre.getText().toString();

                        if (!nombre.isEmpty() && id_img != 0) {

                            //agrego datos de la primera cuenta que se va a crear
                            ContentValues add_cuenta = new ContentValues();
                            add_cuenta.put("nombre_cuenta", nombre);
//                    add_cuenta.put("fecha_cuenta", getDate()); // para guardar que la cree en una class privada abajo
                            add_cuenta.put("icon_cuenta", id_img);
                            add_cuenta.put("icon_spinner", id_img_spinner);
                            add_cuenta.put("disponible_cuenta", 0);
                            baseDeDatos.insert("cuenta", null, add_cuenta);

                            baseDeDatos.close();

                            Main2Activity_Cuentas.fa.finish();
                            Intent Siguiente = new Intent(this, Main2Activity_Cuentas.class);
                            startActivity(Siguiente);
                            Toast.makeText(this, getResources().getString(R.string.cuentaadd), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.debeagregarnombreyimagen), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }

            return true;
        }
        return super.onOptionsItemSelected(vista);
    }

    /*********************  Mostrar imagenes categoria gasto y ingresos ********************************/
    public void imagenesGastosIngresos() {

        for (int i = 0; i < imgGastosIngresos.length; i++) {
            // aqui le desimos que se pare en la posicion del arreglo y me muestre ese nombre de la carpeta drawable
            //idimg[i] = getResources().getIdentifier(imagenes[i], "drawable", getPackageName());
            castinBtn[i].setImageResource(imgGastosIngresos[i]);
            //para escuchar los click d elos botones
            castinBtn[i].setOnClickListener(this);
        }
    }

    /*********************  Mostrar imagenes de las Cuentas y spinner ********************************/
    public void imagenesCuenta() {

        for (int i = 0; i < imgCuentas.length; i++) {
            // aqui le desimos que se pare en la posicion del arreglo y me muestre ese nombre de la carpeta drawable
            //idimg[i] = getResources().getIdentifier(imagenes[i], "drawable", getPackageName());
            castinBtn[i].setImageResource(imgCuentas[i]);
            //para escuchar los click d elos botones
            castinBtn[i].setOnClickListener(this);
        }
    }

    /*********************  Listener click de los imagebutton ********************************/
    @Override
    public void onClick(View v) {

//        int click = v.getId();
        // Para seleccionar solo si es una nueva cuenta
        if (icount == 0) {
            for (int i = 0; i < imgCuentas.length; i++) {

                if (v.getId() == btn_id[i]) {
                    iv_seleccionada.setImageResource(imgCuentas[i]);
                    id_img = imgCuentas[i];
                    id_img_spinner = imgSpinner[i];
                }
            }
        } else if (icount > 0) {
            //modelo
            switch (dato) {
                case "gasto":
                    for (int i = 0; i < imgGastosIngresos.length; i++) {

                        if (v.getId() == btn_id[i]) {
                            iv_seleccionada.setImageResource(imgGastosIngresos[i]);
                            id_img = imgGastosIngresos[i];
                        }
                    }
                    break;
                case "ingreso":
                    for (int i = 0; i < imgGastosIngresos.length; i++) {
                        if (v.getId() == btn_id[i]) {
                            iv_seleccionada.setImageResource(imgGastosIngresos[i]);
                            id_img = imgGastosIngresos[i];
                        }
                    }
                    break;
                case "cuenta":
                    for (int i = 0; i < imgCuentas.length; i++) {

                        if (v.getId() == btn_id[i]) {
                            iv_seleccionada.setImageResource(imgCuentas[i]);
                            id_img = imgCuentas[i];
                            id_img_spinner = imgSpinner[i];
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

   /* private String getDate() {             // se vería así: miercoles 26/09/2018 05:30 p.m.
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm  a", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }*/


    //*************************BOTON ATRAS*********************************************/
    // para evitar que usen el boton de regresar de android paraq ue no reguresa los activitys
    //la palabra override es para sobre escribir metodos que ya estan establecidos
    /*@Override
    public void onBackPressed(){
        //aqui damos instrucciones en dado caso als necesitemos
    }*/
}
