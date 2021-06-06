package com.rrvq.carterapersonalpro;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Clase adaptador de la lista de alumnos.
public class AdaptadorListaExp extends BaseExpandableListAdapter {

    // Variables miembro.
    ArrayList<GrupoPadre> mGrupos; // Nombres de los grupos.
    ArrayList<ArrayList<GrupoHijo>> mHijos; // Alumnos por grupo.
    LayoutInflater mInflador;
    int mColorGasto;
    int mColorIngreso;

    //para mostrar solo dos decimales
    DecimalFormat formato = new DecimalFormat("#.#");

    // Clase interna privada contenedor de vistas de hijo.
    private static class ContenedorVistasHijo {
        TextView tv_montoHijo;
        TextView tv_descripcionHijo;
        TextView tv_fechaHijo;
    }

    // Clase interna privada contenedor de vistas de grupo.
    private static class ContenedorVistasGrupo {
        ImageView imgIndicador;
        ImageView imgIconoGasto;
        TextView tv_nombrePadre;
        TextView tv_montoPadre;
    }

    // Constructor.
    public AdaptadorListaExp(Context contexto, ArrayList<GrupoPadre> grupos,
                             ArrayList<ArrayList<GrupoHijo>> hijos) {
        this.mGrupos = grupos;
        this.mHijos = hijos;
        // Se obtiene un inflador de layouts.
        mInflador = LayoutInflater.from(contexto);
        // Se obtienen los colores de menor y mayor de edad.
        mColorGasto = ContextCompat.getColor(contexto, R.color.colorRojo);
        mColorIngreso = ContextCompat.getColor(contexto, R.color.colorPrimary);
    }

    // Retorna el objeto de datos de un hijo de un grupo.
    @Override
    public GrupoHijo getChild(int posGrupo, int posHijo) {
        return mHijos.get(posGrupo).get(posHijo);
    }

    // Retorna el id de un hijo de un grupo
    @Override
    public long getChildId(int posGrupo, int posHijo) {
        // No gestionamos los ids.
        return 0;
    }

    // Cuando se va a pintar un hijo de un grupo.
    @Override
    public View getChildView(int posGrupo, int posHijo,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ContenedorVistasHijo contenedor;
        // Si no se puede reciclar.
        if (convertView == null) {
            // Se infla el layout para el hijo.
            convertView = mInflador.inflate(R.layout.lista_hijo,
                    parent, false);
            // Se obtienen las vistas y se almacenan en el contenedor.
            contenedor = new ContenedorVistasHijo();
            contenedor.tv_montoHijo = convertView.findViewById(R.id.tv_montoHijo);
            contenedor.tv_descripcionHijo = convertView.findViewById(R.id.tv_descripcionHijo);
            contenedor.tv_fechaHijo = convertView.findViewById(R.id.tv_fechaHijo);
            // El contenedor se almacena en el tag del hijo.
            convertView.setTag(contenedor);
        } else {
            // Si se recicla, se obtiene el contenedor desde el tag del
            // hijo.
            contenedor = (ContenedorVistasHijo) convertView.getTag();
        }
        // Se escriben los valores en las vistas.
        GrupoHijo GHijo = mHijos.get(posGrupo).get(posHijo);
        contenedor.tv_montoHijo.setText(String.valueOf(formato.format(GHijo.getMontoHijo()))); // convieto a string
        contenedor.tv_descripcionHijo.setText(GHijo.getDescripcionHijo());
        contenedor.tv_fechaHijo.setText(GHijo.getFechaHijo());

        // Se retorna la vista correspondiente al hijo.
        return convertView;
    }

    // Retorna cuántos hijos tiene un grupo.
    @Override
    public int getChildrenCount(int posGrupo) {
        return mHijos.get(posGrupo).size();
    }

    // Retorna un ArrayList con todos los hijos de un grupo.
    @Override
    public ArrayList<GrupoHijo> getGroup(int posGrupo) {
        return mHijos.get(posGrupo);
    }

    // Retorna el número de grupos.
    @Override
    public int getGroupCount() {
        return mHijos.size();
    }

    // Retorna el id de un grupo.
    @Override
    public long getGroupId(int posGrupo) {
        // No gestionamos los ids.
        return 0;
    }

    // Cuando se va a pintar el encabezado de un grupo.
    @Override
    public View getGroupView(int posGrupo, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ContenedorVistasGrupo contenedor;
        // Si no se puede reciclar.
        if (convertView == null) {
            // Se infla del layout del grupo.
            convertView = mInflador.inflate(R.layout.lista_padre,
                    parent, false);
            // Se obtienen las vistas y se almacenan en el contenedor.
            contenedor = new ContenedorVistasGrupo();
            contenedor.imgIndicador = convertView.findViewById(R.id.imgIndicador);
            contenedor.imgIconoGasto = convertView.findViewById(R.id.imgIconoGasto);
            contenedor.tv_nombrePadre = convertView.findViewById(R.id.tv_nombrePadre);
            contenedor.tv_montoPadre =  convertView.findViewById(R.id.tv_montoPadre);

            // El contenedor se almacena en el tag de la vista grupo.
            convertView.setTag(contenedor);
        } else {
            // Si se recicla, se obtiene el contenedor desde la prop Tag de
            // vista grupo.
            contenedor = (ContenedorVistasGrupo) convertView.getTag();
        }
        // Se escriben los valores en las vistas.
        GrupoPadre GPadre = mGrupos.get(posGrupo);
//        contenedor.tv_nombrePadre.setText(mGrupos.get(posGrupo));
        contenedor.imgIconoGasto.setImageResource(GPadre.getimgPadre());
        contenedor.tv_nombrePadre.setText(GPadre.getnombrePadre());
        contenedor.tv_montoPadre.setText(String.valueOf(formato.format(GPadre.getmontoPadre())));

        // este fragmento de codigo lo usare para la condicion si es gasto o ingreso **********************************
        if (GPadre.getgastoIngreso() == 0) {
            contenedor.tv_montoPadre.setTextColor(mColorGasto);
        } else if (GPadre.getgastoIngreso() == 1) {
            contenedor.tv_montoPadre.setTextColor(mColorIngreso);
        }

        // Si el grupo no tiene hijos se oculta el icono de despliegue y la
        // cabecera de columnas.
        if (getChildrenCount(posGrupo) == 0) {
            contenedor.imgIndicador.setVisibility(View.INVISIBLE);
        } else {
            // Se hace visible el indicador de expansión.
            contenedor.imgIndicador.setVisibility(View.VISIBLE);
            // Si el grupo está expandido se muestra el icono de colapsar
            // y la cabecera de columnas.
            if (isExpanded) {
                contenedor.imgIndicador.setImageResource(R.drawable.flecha16_arriba);
            } else {
                // Si el grupo no está expandido se muestra el icono de
                // expandir
                // y se oculta la cabecera de columnas.
                contenedor.imgIndicador.setImageResource(R.drawable.flecha16_abajo);
            }
        }
        // Se retorna la vista correspondiente al grupo.
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // Comportamiento por defecto.
        return false;
    }

    @Override
    public boolean isChildSelectable(int posGrupo, int posHijo) {
        // Comportamiento por defecto.
        return true;
    }

    // Cuando se colapsa un grupo
    @Override
    public void onGroupCollapsed(int posGrupo) {
        // Si se desea que siempre aparezca expandida, se expande cuando se
        // trate de colapsar.
        // lstAlumnos.expandGroup(posGrupo);
    }

}