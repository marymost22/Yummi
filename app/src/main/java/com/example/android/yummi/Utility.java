package com.example.android.yummi;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.android.yummi.data.ComedoresContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Clase con utilidades de apoyo
 * Created by David Campos Rodríguez <a href='mailto:david.campos@rai.usc.es'>david.campos@rai.usc.es</a> on 07/02/2016.
 */
public class Utility {
    public static final long MES_EN_MILLIS = 32L * 24L * 60L * 60L * 1000L;

    public static long fechaHoy() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long fechaAnteayer() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_YEAR, -2);
        return c.getTimeInMillis();
    }

    public static long normalizarFecha(String fecha) {
        try {
            SimpleDateFormat dF = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dF.parse(fecha);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long normalizarHora(String hora) {
        try {
            SimpleDateFormat dF = new SimpleDateFormat("HH:mm:ss");
            Date date = dF.parse(hora);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Integer normalizarDiaSemana(String dia) {
        switch(dia.toLowerCase()) {
            case "lunes": return 0;
            case "martes": return 1;
            case "miercoles": return 2;
            case "jueves": return 3;
            case "viernes": return 4;
            case "sabado": return 5;
            case "domingo": return 6;
            default: return -1;
        }
    }

    public static String denormalizarFecha(long fecha) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(fecha));
    }

    public static String denormalizarHora(long hora) {
        return new SimpleDateFormat("HH:mm").format(new Date(hora));
    }

    public static String denormalizarDiaSemana(Context context, Integer dia) {
        switch(dia) {
            case 0: return context.getString(R.string.lunes);
            case 1: return context.getString(R.string.martes);
            case 2: return context.getString(R.string.miercoles);
            case 3: return context.getString(R.string.jueves);
            case 4: return context.getString(R.string.viernes);
            case 5: return context.getString(R.string.sabado);
            case 6: return context.getString(R.string.domingo);
            default: return context.getString(R.string.dia_desconocido);
        }
    }

    public static void logearBase(Context context) {
        ContentResolver cR = context.getContentResolver();
        Cursor c = cR.query(ComedoresContract.PlatosEntry.CONTENT_URI,
                null, null, null, null);
        Log.d("LOG_BASE", "Num elementos en platos: " + c.getCount());
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                Log.d("LOG_BASE", "Tipo plato: " + c.getString(c.getColumnIndex(ComedoresContract.PlatosEntry.COLUMN_TIPO)));
                c.moveToNext();
            }
        }
        c.close();
        c = cR.query(ComedoresContract.ComedoresEntry.CONTENT_URI,
                null, null, null, null);
        Log.d("LOG_BASE", "Num elementos en comedores: " + c.getCount());
        c.close();
        c = cR.query(ComedoresContract.TiposMenuEntry.CONTENT_URI,
                null, null, null, null);
        Log.d("LOG_BASE", "Num elementos en tiposmenu: " + c.getCount());
        c.close();
        c = cR.query(ComedoresContract.ElementosEntry.CONTENT_URI,
                null, null, null, null);
        Log.d("LOG_BASE", "Num elementos en elementos: " + c.getCount());
        c.close();
        c = cR.query(ComedoresContract.TenerEntry.CONSULTA_URI,
                null, null, null, null);
        Log.d("LOG_BASE", "Num elementos en tener: " + c.getCount());
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                Log.d("LOG_BASE", "Tener: " + c.getString(c.getColumnIndex(ComedoresContract.TenerEntry.COLUMN_COMEDOR)));
                Log.d("LOG_BASE", "Tener: " + c.getString(c.getColumnIndex(ComedoresContract.TenerEntry.COLUMN_FECHA)));
                Log.d("LOG_BASE", "Tener: " + c.getString(c.getColumnIndex(ComedoresContract.TenerEntry.COLUMN_PLATO)));
                c.moveToNext();
            }
        }
        c.close();
        c = cR.query(ComedoresContract.TienenEntry.CONSULTA_URI,
                null, null, null, null);
        Log.d("LOG_BASE", "Num elementos en tienen: " + c.getCount());
        c.close();
    }

    public static boolean horaActualEn(long ini, long fin) {
        Calendar c = Calendar.getInstance();
        Calendar cIni = Calendar.getInstance();
        cIni.setTimeInMillis(ini);
        Calendar cFin = Calendar.getInstance();
        cFin.setTimeInMillis(fin);
        int minutosActuales = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
        return ( minutosActuales >= cIni.get(Calendar.HOUR_OF_DAY) * 60 + cIni.get(Calendar.MINUTE) &&
                 minutosActuales <= cFin.get(Calendar.HOUR_OF_DAY) * 60 + cFin.get(Calendar.MINUTE));
    }

    public static boolean conectadoWifi(Context context) {
        final ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return infoWifi.isConnectedOrConnecting();
    }

    public static boolean conectado(Context context) {
        final ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        return (info != null && info.isConnectedOrConnecting());
    }

    public static boolean diaActualEn(int diaIni, int diaFin) {
        Calendar c = Calendar.getInstance();
        // Nuestros días van de lunes=0 a domingo=6
        // Los de calendar van de domingo=1 a sabado=7,
        // le sumamos 5 y le hacemos módulo 7
        int dia = (c.get(Calendar.DAY_OF_WEEK) + 5)%7;
        if( diaFin >= diaIni ) {
            return diaIni <= dia && dia <= diaFin;
        } else {
            return dia <= diaIni || dia >= diaFin;
        }
    }
}
