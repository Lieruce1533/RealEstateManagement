package com.lieruce.realestatemanager;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    private static double dollarEuroRate = 0.812;  // Default Dollar to Euro conversion rate
    private static double euroDollarRate = 1.231;  // Default Euro to Dollar conversion rate

    /**
     * Updates the dynamic USD to Euro exchange rate fetched from the API.
     */
    public static void setDollarEuroRate(double rate) {
        dollarEuroRate = rate;
        if (rate > 0) {
            // Automatically calculate the reverse rate so they always stay perfectly in sync!
            euroDollarRate = 1.0 / rate;
        }
    }



    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * dollarEuroRate);
    }
    /**
     * Conversion d'un prix d'un bien immobilier (Euros vers Dollars)Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param euros
     * @return
     */

    public static int convertEuroToDollar(int euros){
        return (int) Math.round(euros * euroDollarRate);
    }


    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }
    public static String getTodayDateNew(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        return today.format(df);

    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

}
