package br.inatel.icc.gigasecurity.gigamonitor.util;

/**
 * Created by rinaldo.bueno on 29/08/2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

import br.inatel.icc.gigasecurity.gigamonitor.model.FavoritePair;

public class ComplexPreferences {

    private static ComplexPreferences complexPreferences;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    Type typeOfObject = new TypeToken<Object>() {
    }.getType();

    public ComplexPreferences(Context context, String namePreferences, int mode) {
        this.context = context;
        if (namePreferences == null || namePreferences.equals("")) {
            namePreferences = "complex_preferences";
        }
        preferences = context.getSharedPreferences(namePreferences, mode);
        editor = preferences.edit();
    }

    public static ComplexPreferences getComplexPreferences(Context context,
                                                           String namePreferences, int mode) {

        if (complexPreferences == null) {
            complexPreferences = new ComplexPreferences(context,
                    namePreferences, mode);
        }

        return complexPreferences;
    }

    public void putObject(String key, Object object) {
        if(object == null){
            throw new IllegalArgumentException("object is null");
        }
        if(key.equals("") || key == null){
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, GSON.toJson(object));
    }

    /*public void putObject(String key, LinkedList<Pair<Integer, Integer>> list) {
        if(list == null){
            throw new IllegalArgumentException("object is null");
        }
        if(key.equals("") || key == null){
            throw new IllegalArgumentException("key is empty or null");
        }
        Type listType = new TypeToken<LinkedList<Pair>>(){}.getType();
        editor.putString(key, GSON.toJson(list, listType));

    }*/

    public void apply() {
        editor.apply();
    }

    public <T> T getObject(String key, Class<T> a) {

        String gson = preferences.getString(key, null);
        if (gson == null || gson.equals("{}")) {
            return null;
        } else {
            try{
                if(key.equals("FavoritesList")){
                    Type listType = new TypeToken<ArrayList<FavoritePair>>(){}.getType();
                    ArrayList<FavoritePair> list = GSON.fromJson(gson, listType);
                    return GSON.fromJson(gson, listType);
                }else
                    return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storaged with key " + key + " is instanceof other class");
            }
        }
    }

    public static void setObject(Context ctx, String key, Object obj){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,new Gson().toJson(obj));
        editor.commit();
    }

    public static <T> Object getObject (Context ctx, String key, Class<T> a){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String sobj = preferences.getString(key, "");
        if(sobj.equals(""))return null;
        else return new Gson().fromJson(sobj, a);
    }

}