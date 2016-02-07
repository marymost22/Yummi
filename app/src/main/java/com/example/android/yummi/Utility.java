package com.example.android.yummi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Clase con utilidades de apoyo
 * Created by David Campos Rodríguez <david.campos@rai.usc.es> on 07/02/2016.
 */
public class Utility {
    public static long fechaHoy() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
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
}
