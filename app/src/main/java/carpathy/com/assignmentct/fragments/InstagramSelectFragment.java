package carpathy.com.assignmentct.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;


import java.util.ArrayList;
import java.util.Arrays;

import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.utils.SharedPreferenceUtils;
import carpathy.com.assignmentct.activities.WebViewActivity;
import carpathy.com.assignmentct.models.WebViewUrl;
import carpathy.com.assignmentct.adapters.ClearAdaptersOnLogout;
import carpathy.com.assignmentct.adapters.InstagramPhotosAdapter;
import carpathy.com.assignmentct.controllers.AppController;
import carpathy.com.assignmentct.models.GenericImageModel;
import carpathy.com.assignmentct.models.InstagramModel;
import carpathy.com.assignmentct.utils.Constants;
import de.greenrobot.event.EventBus;


public class InstagramSelectFragment extends Fragment implements View.OnClickListener, InstagramPhotosAdapter.MyInstagramLogoutInterface {

    private static final String TAG = InstagramSelectFragment.class.getSimpleName();

    ArrayList<InstagramModel> instagramModels;
    public static RecyclerView recyclerView;
    Button instagramProceedBTN;
    String phoneNum;
    RelativeLayout mainContentRL;
    public static RelativeLayout instaLoginRL, instaPicsRL;
    public static ImageView fbLoginIV;
    ArrayList<InstagramModel> instagramModelsAL = new ArrayList<>();
    ArrayList<GenericImageModel> instagramGenericImageModelsAl = new ArrayList<>();

    InstagramModel instagramModel;
    ArrayList<String> urls = new ArrayList<>();
    TextView lockTV;
    public static Button instalogoutBtn;
    public static ImageView imageviewtop, imageviewBottom;
    InstagramPhotosAdapter instagramPhotosAdapter;

//    private OnFragmentInteractionListener mListener;

    public InstagramSelectFragment() {
        // Required empty public constructor
    }


    public static InstagramSelectFragment newInstance(String param1, String param2) {
        InstagramSelectFragment fragment = new InstagramSelectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();

        if (getArguments() != null) {
            phoneNum = "9910775101";

        }
        EventBus.getDefault().register(this);

        init_ArrayList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instagram, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.instagramPhotosRV);
        recyclerView.setHasFixedSize(true);
        instaLoginRL = (RelativeLayout) view.findViewById(R.id.InstaLoginRL);
        instaPicsRL = (RelativeLayout) view.findViewById(R.id.instagramImagesRL);
        mainContentRL = (RelativeLayout) view.findViewById(R.id.mainContentRL);
        fbLoginIV = (ImageView) view.findViewById(R.id.instagramLoginIV);
        instalogoutBtn = (Button) view.findViewById(R.id.instalogoutBtn);
        instalogoutBtn.setOnClickListener(this);
        imageviewtop = (ImageView) view.findViewById(R.id.imageviewtop);
        imageviewBottom = (ImageView) view.findViewById(R.id.imageviewBottom);
        fbLoginIV.setOnClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated");
        checkIsLoggedInOrNot();


    }

    public void getImageFromServer(String instagramAccessTokenMsg) {
        final String access_token = instagramAccessTokenMsg;
        String url = Constants.GET_INSTAGRAM_IMAGES_FROM_SERVER_IP + access_token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                instagramModelsAL = new ArrayList<>(Arrays.asList(new GsonBuilder().create().fromJson(response, InstagramModel[].class)));
                int size = instagramModelsAL.size();
                instagramGenericImageModelsAl.clear();
                for (int i = 0; i < size; i++) {
                    GenericImageModel genericImageModel = new GenericImageModel();
                    genericImageModel.setSource_type(Constants.SOURCE_TYPE_FOR_INSTAGRAM_IN_OUR_SERVER);
                    genericImageModel.setSource_id(instagramModelsAL.get(i).getMedia_id());
                    genericImageModel.setOrder_id(0l);
                    genericImageModel.setCount(0);
                    genericImageModel.setIsUploaded(false);
                    genericImageModel.setSelectedAtWhatNumber(0);
                    genericImageModel.setSource_path(instagramModelsAL.get(i).getUrl());
                    genericImageModel.setSelectedCount(0);
                    genericImageModel.setImageSelected(false);
                    instagramGenericImageModelsAl.add(genericImageModel);
                }
                instagramPhotosAdapter = new InstagramPhotosAdapter(getActivity(), instagramGenericImageModelsAl);
                recyclerView.setAdapter(instagramPhotosAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Snackbar snackbar = Snackbar.make(mainContentRL, "Error", Snackbar.LENGTH_SHORT);
                View view = snackbar.getView();
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.RED);
                snackbar.show();
                snackbar.setAction("retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getImageFromServer(access_token);
                    }
                });
                snackbar.show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().add(stringRequest);
    }


    @Override
    public void onResume() {
        super.onResume();
        checkIsLoggedInOrNot();
    }

    private void init_ArrayList() {
        instagramModels = new ArrayList<>();

        for (int i = 0; i <= urls.size() - 1; i++) {
            instagramModel = new InstagramModel();
            instagramModel.setUrl(urls.get(i));
            instagramModel.setChecked(false);
            instagramModels.add(instagramModel);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.InstaLoginRL:
                callInstagramForToken();
                break;

            case R.id.instagramLoginIV:
                callInstagramForToken();
                break;

            case R.id.instalogoutBtn:
//                Toast.makeText(getActivity(), "Logout button clicked", Toast.LENGTH_SHORT).show();
                SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
                sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
                sharedPreferenceUtils.updateSharedPreferenceStringSingle(Constants.PRFERENCE_INSTAGRAM_ACCESS_TOKEN, null);
                checkIsLoggedInOrNot();

                if (instagramGenericImageModelsAl != null) {
                    instagramGenericImageModelsAl.clear();
                    recyclerView.setAdapter(null);
//                    notifyDataSetChanged();
                }

                String cookieString = "sessionid=''";
                CookieManager.getInstance().setCookie("instagram.com", cookieString);

        }

    }

    private void callInstagramForToken() {

        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
        sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);

        String email = "akapoor_91@yahoo.com";
        String url = Constants.GET_ACCESS_TOKEN_INSTAGRAM + email + "&response_type=code";
        EventBus.getDefault().postSticky(new WebViewUrl(url));
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        startActivity(intent);
        Log.d(TAG, "callInstagramForToken");
    }

    //on instagram logout

    public void onEvent(ClearAdaptersOnLogout ob) {

//        Toast.makeText(getActivity(), "On Event1", Toast.LENGTH_SHORT).show();
        if (ob.getI() == 3) // clear Instagram Adapters
        {
            Log.d(TAG, "clearInstaImages called");
            checkIsLoggedInOrNot();
        }
    }


    @Override
    public void clearInstaImages() {
        Log.d(TAG, "clearInstaImages called");
        Toast.makeText(getActivity(), "called", Toast.LENGTH_SHORT).show();

    }

    /**
     * shows either login or logout button
     */
    public void checkIsLoggedInOrNot() {
        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
        sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
        String access_token = sharedPreferenceUtils.getSharedPreferenceStringName(Constants.PRFERENCE_INSTAGRAM_ACCESS_TOKEN);
        if (access_token == null) {
            instaLoginRL.setVisibility(View.VISIBLE);
            instaPicsRL.setVisibility(View.GONE);
            instalogoutBtn.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            imageviewtop.setVisibility(View.VISIBLE);
            imageviewBottom.setVisibility(View.VISIBLE);
            fbLoginIV.setVisibility(View.VISIBLE);

        } else if (access_token.trim().length() == 0) {
            instaLoginRL.setVisibility(View.VISIBLE);
            instaPicsRL.setVisibility(View.GONE);
            lockTV.setVisibility(View.VISIBLE);
            instagramProceedBTN.setVisibility(View.GONE);
            lockTV.setVisibility(View.VISIBLE);
            instalogoutBtn.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            imageviewtop.setVisibility(View.VISIBLE);
            imageviewBottom.setVisibility(View.VISIBLE);
            fbLoginIV.setVisibility(View.VISIBLE);
        } else {
            instaLoginRL.setVisibility(View.GONE);
            instaPicsRL.setVisibility(View.VISIBLE);
            instalogoutBtn.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            imageviewtop.setVisibility(View.GONE);
            imageviewBottom.setVisibility(View.GONE);
            fbLoginIV.setVisibility(View.GONE);
            getImageFromServer(access_token);
        }
    }
}
