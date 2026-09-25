package com.lieruce.realestatemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
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
    public static String getTodayDateOld(){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }
    public static String getTodayDateNew(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Instant today = Instant.now();
        return df.format(today);

    }

    /**
     * Vérification de la connexion réseau (Legacy method kept for backward compatibility/defense)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    /**
     * Modern and reliable network connectivity check using ConnectivityManager and NetworkCapabilities.
     * Checks for active internet capability and validation across Wi-Fi, Cellular, or Ethernet.
     * @param context Application or Activity context
     * @return true if active internet connection is available, false otherwise
     */
    public static boolean isInternetAvailableNew(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        // Get the currently active network connection
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;

        // Retrieve network capabilities for the active network
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        if (capabilities == null) return false;

        // Verify transport type (Wi-Fi, Cellular, or Ethernet)
        boolean hasTransport = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

        // Verify internet capability and validated connection
        boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        boolean isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        return hasTransport && hasInternet && isValidated;
    }

    /**
     * Checks whether GPS or Network location providers are enabled on the device.
     * @param context Application or Activity context
     * @return true if location services are enabled, false otherwise
     */
    public static boolean isGpsEnabled(Context context) {
        if (context == null) return false;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return false;

        boolean isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGps || isNetwork;
    }

}
