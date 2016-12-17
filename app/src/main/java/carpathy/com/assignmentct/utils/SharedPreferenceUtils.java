package carpathy.com.assignmentct.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPreferenceUtils {
    SharedPreferences sharedPreferences;
    String sharedPreferenceName;
    SharedPreferences.Editor editor;
    Context context;

    public void initializeSharedPreference(Context context, String sharedPreferenceName) {
        this.context = context;
        this.sharedPreferenceName = sharedPreferenceName;
        sharedPreferences = context.getSharedPreferences(this.sharedPreferenceName, Context.MODE_PRIVATE);
    }

    public void updateSharedPreferenceStringSingle(String Key, String Value) {
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();
    }


    public void updateSharedPreferenceBooleanSingle(String key, Boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getSharedPreferenceBooleanName(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public String getSharedPreferenceStringName(String key) {
        return sharedPreferences.getString(key, null);
    }

    public Long getSharedPreferenceLongName(String key) {
        return sharedPreferences.getLong(key, 0);
    }


    public Long get(String key) {
        return sharedPreferences.getLong(key, 0);
    }
}
