package com.example.android.yummi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.yummi.data.ComedoresContract;
import com.example.android.yummi.services.ComedoresService;

import java.util.ArrayList;
import java.util.List;


public class PricesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PricesActivityFragment.class.getSimpleName();
    private LinearLayout linearLayout;

    public static final String PROMO_COMEDOR = "promo";
    public static final String ID_COMEDOR = "ID";

    public static final String[] COLUMNAS_MENU = {
            ComedoresContract.TiposMenuEntry.TABLE_NAME + "." + ComedoresContract.TiposMenuEntry._ID,
            ComedoresContract.TiposMenuEntry.COLUMN_NOMBRE,
            ComedoresContract.TiposMenuEntry.COLUMN_PRECIO
    };

    public static final int COL_MENU_ID = 0;
    public static final int COL_MENU_NOMBRE = 1;
    public static final int COL_MENU_PRECIO = 2;
    public static final int COL_MENU_COMEDOR = 3;

    public static final String[] COLUMNAS_ELEMENTOS = {
            ComedoresContract.ElementosEntry.COLUMN_TIPO,
            ComedoresContract.ElementosEntry.COLUMN_NOMBRE
    };

    public static final int COL_ELEM_TIPO = 0;
    public static final int COL_ELEM_NOMBRE = 1;

    private  AdapterMenu mAdapter;

    private long mComedorId = -1;
    private long mMenuId= -1;
    private String mComedorPromo = "null";


    private static final int LOADER_COLUMNAS_MENU = 0;
    private static final int DEMAS_LOADERS_BASE = 1;
    private int mDemasLoaders = DEMAS_LOADERS_BASE;

    private List<Long> mIdsMenus;

    public PricesActivityFragment() {
    }

    public static class ViewHolderMenuItem {
        public  TextView mViewMenuNombre;
        public  TextView mViewMenuPrecio;
        public  TextView mViewMenuElementos;

        public ViewHolderMenuItem(View view){
            mViewMenuNombre = (TextView) view.findViewById(R.id.menu_name);
            mViewMenuPrecio = (TextView) view.findViewById(R.id.menu_price);
            mViewMenuElementos = (TextView) view.findViewById(R.id.menu_elements);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mComedorId = arguments.getLong(ID_COMEDOR);
            mComedorPromo = arguments.getString(PROMO_COMEDOR);
            //Comprobamos si ha actualizado los comedores este mes
            Cursor c = getActivity().getContentResolver().query(
                    ComedoresContract.ComedoresEntry.CONTENT_URI,
                    new String[]{ComedoresContract.ComedoresEntry.COLUMN_LAST_ACT},
                    ComedoresContract.ComedoresEntry._ID + " = ?", new String[]{Long.toString(mComedorId)},
                    null);
            if(c.moveToFirst()) {
                Long lastSync = c.getLong(0);
                if (lastSync == null || System.currentTimeMillis() - lastSync >= Utility.MES_EN_MILLIS) {
                    //Si hace un mes que no se actualiza (32 días más bien), actualizamos
                    Intent lanzarServicio = new Intent(getActivity(), ComedoresService.class);
                    lanzarServicio.putExtra(ComedoresService.KEY_TIPO, ComedoresService.TIPO_CONSULTA_MENUS);
                    lanzarServicio.putExtra(ComedoresService.KEY_ID, mComedorId);
                    getActivity().startService(lanzarServicio);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_COLUMNAS_MENU, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_prices, container, false);

        RecyclerView recyclerView = ((RecyclerView) rootView.findViewById(R.id.menus_view));
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        mAdapter = new AdapterMenu(getActivity(), mComedorPromo);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_COLUMNAS_MENU) {
            return new CursorLoader(
                    getActivity(),
                    ComedoresContract.TiposMenuEntry.buildTipoMenuByComedorUri(mComedorId),
                    COLUMNAS_MENU,
                    null, null,
                    null);
        } else {
            return new CursorLoader(
                    getActivity(),
                    ComedoresContract.ElementosEntry.buildElementosByMenuUri(mIdsMenus.get(id-DEMAS_LOADERS_BASE)),
                    COLUMNAS_ELEMENTOS,
                    null, null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_COLUMNAS_MENU) {
            if (data.moveToFirst()) {
                mIdsMenus = new ArrayList<>(data.getCount());
                while (!data.isAfterLast()) {
                    long idMenu = data.getLong(COL_MENU_ID);

                    // Iniciamos servicio para descargar sus elementos
                    Intent serv = new Intent(getActivity(), ComedoresService.class);
                    serv.putExtra(ComedoresService.KEY_TIPO, ComedoresService.TIPO_CONSULTA_ELEMENTOS);
                    serv.putExtra(ComedoresService.KEY_ID, idMenu);
                    getActivity().startService(serv);

                    //Iniciamos loader para cargar sus elementos
                    mIdsMenus.add(idMenu);
                    Log.d(LOG_TAG, "Iniciado loader " + mDemasLoaders + ", idMenu= " + idMenu);
                    getLoaderManager().initLoader(mDemasLoaders++, null, this);
                    data.moveToNext();
                }
            }
            mAdapter.swapCursor(data);
        } else {
            Log.d(LOG_TAG, "Load elementos finalizado: recibidos " + data.getCount());
            mAdapter.setElementosMenu(
                    mIdsMenus.get(loader.getId() - DEMAS_LOADERS_BASE),
                    data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}





