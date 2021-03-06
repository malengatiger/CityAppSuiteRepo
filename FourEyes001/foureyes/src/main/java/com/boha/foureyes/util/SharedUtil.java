package com.boha.foureyes.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.boha.foureyes.dto.MunicipalityDTO;
import com.boha.foureyes.dto.MunicipalityStaffDTO;
import com.boha.foureyes.dto.ProfileInfoDTO;
import com.google.gson.Gson;


/**
 * Created by aubreyM on 15/01/13.
 */
public class SharedUtil {

    public static void setLanguageIndex(Context ctx, int languageIndex) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(LANGUAGE_INDEX, languageIndex);
        ed.commit();

        Log.w(LOG, "#### language index saved: " + languageIndex);

    }
    public static int getLanguageIndex(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int j = sp.getInt(LANGUAGE_INDEX, -1);
        Log.i(LOG, "#### language index retrieved: " + j);
        return j;
    }
    public static void setID(Context ctx, String id) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor ed = sp.edit();
        ed.putString(ID, id);
        ed.commit();

        Log.w(LOG, "#### id  saved: " + id);

    }
    public static String getID(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String j = sp.getString(ID, null);
        if (j == null) {
            Log.i(LOG, "#### id NOT retrieved: ");
            return null;
        }
        Log.i(LOG, "#### id retrieved: " + j);
        return j;
    }

    public static void storeRegistrationId(Context ctx, String id) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor ed = sp.edit();
        ed.putString(GCM, id);
        ed.commit();

        Log.w(LOG, "#### GCM registrationid  saved: " + id);

    }
    public static String getRegistrationID(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String j = sp.getString(GCM, null);
        if (j == null) {
            Log.i(LOG, "#### GCM id NOT retrieved: ");
            return null;
        }
        Log.i(LOG, "#### GCM id retrieved: " + j);
        return j;
    }

    public static void saveProfile(Context ctx, ProfileInfoDTO profileInfo) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        String json = gson.toJson(profileInfo);

        SharedPreferences.Editor ed = sp.edit();
        ed.putString(PROFILE, json);
        ed.commit();

        Log.w(LOG, "#### profile  saved: " + json);

    }
    public static ProfileInfoDTO getProfile(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String json = sp.getString(PROFILE, null);

        if (json == null) {
            return null;
        }
        Log.i(LOG, "#### profile retrieved: " + json);
        return gson.fromJson(json, ProfileInfoDTO.class);
    }
    static Gson gson = new Gson();
    static final String LANGUAGE_INDEX = "langIndex", SERVER_DEST = "server",
            ID = "id", MUNICIPALITY = "muni", MUNI_STAFF = "muniStaff",
            PROFILE = "profile", GCM = "gcm";

    public static void saveMunicipality(Context ctx, MunicipalityDTO m) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor ed = sp.edit();
        String s = gson.toJson(m);
        ed.putString(MUNICIPALITY, s);
        ed.commit();

        Log.w(LOG, "#### municipality  saved: " + s);

    }
    public static MunicipalityDTO getMunicipality(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String j = sp.getString(MUNICIPALITY, null);
        if (j == null) {
            return null;
        }
        Log.i(LOG, "#### muni retrieved: " + j);
        return gson.fromJson(j, MunicipalityDTO.class);
    }

    public static void saveMunicipalityStaff(Context ctx, MunicipalityStaffDTO m) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor ed = sp.edit();
        String s = gson.toJson(m);
        ed.putString(MUNI_STAFF, s);
        ed.commit();

        Log.w(LOG, "#### municipality staff saved: " + s);

    }
    public static MunicipalityStaffDTO getMunicipalityStaff(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String j = sp.getString(MUNI_STAFF, null);
        if (j == null) {
            return null;
        }
        Log.i(LOG, "#### muni staff retrieved: " + j);
        return gson.fromJson(j, MunicipalityStaffDTO.class);
    }
    static final String LOG = SharedUtil.class.getSimpleName();

    public static final int SERVER_SMARTCITY = 1, SERVER_MONITOR = 2;

    public static void saveServerDestination(Context ctx, int serverDestination) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(SERVER_DEST, serverDestination);
        ed.commit();

    }
    public static int getServerDestination(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int j = sp.getInt(SERVER_DEST, -1);

        return j;
    }
    public static String getServerHTTPUrl(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int j = sp.getInt(SERVER_DEST, -1);
        String url = null;
        switch (j) {
            case SERVER_MONITOR:
                url = Statics.HTTP_MONITOR;
                break;
            case SERVER_SMARTCITY:
                url = Statics.HTTP_SMARTCITY;
                break;
            case -1:
                url = Statics.HTTP_MONITOR;
                break;
        }


        return url;
    }
    public static String getServerWebsocketUrl(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int j = sp.getInt(SERVER_DEST, -1);
        String url = null;
        switch (j) {
            case SERVER_MONITOR:
                url = Statics.WEBSOCKET_MONITOR;
                break;
            case SERVER_SMARTCITY:
                url = Statics.WEBSOCKET_SMARTCITY;
                break;
            case -1:
                url = Statics.WEBSOCKET_MONITOR;
                break;
        }


        return url;
    }

}
