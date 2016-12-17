package carpathy.com.assignmentct.controllers;

/**
 * Created by om on 12/12/2016.
 */

import android.app.Application;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

import carpathy.com.assignmentct.models.GaleryImageModel;
import carpathy.com.assignmentct.models.GenericImageModel;

public class AppController extends Application {


    private RequestQueue mRequestQueue;
    private static int totalCountImages;
    public static ArrayList<GenericImageModel> selectedGenericImageModelsAL;
    public static ArrayList<GaleryImageModel> selectedGaleryImageModelsAL;
    Boolean isServiceRunning;
    private static AppController mInstance;


    public static int getTotalCountImages() {
        return totalCountImages;
    }

    public static void setTotalCountImages(int totalCountImages) {
        AppController.totalCountImages = totalCountImages;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        totalCountImages = 0;
        FacebookSdk.sdkInitialize(getApplicationContext());
        selectedGenericImageModelsAL = new ArrayList<>();
        selectedGaleryImageModelsAL = new ArrayList<>();
        isServiceRunning = false;


    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }


}