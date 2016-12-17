package carpathy.com.assignmentct.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.utils.SharedPreferenceUtils;
import carpathy.com.assignmentct.adapters.SingleImageAdapterFbRV;
import carpathy.com.assignmentct.adapters.UpdateAdapters;
import carpathy.com.assignmentct.activities.MainActivity;
import carpathy.com.assignmentct.adapters.ClearAdaptersOnLogout;
import carpathy.com.assignmentct.adapters.FacebookAlbumsAdaptersRV;
import carpathy.com.assignmentct.controllers.AppController;
import carpathy.com.assignmentct.models.AlbumPictureModel;
import carpathy.com.assignmentct.models.FacebookAlbumIdMsg;
import carpathy.com.assignmentct.models.GenericImageModel;
import carpathy.com.assignmentct.utils.Constants;
import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class MyFbFrag extends Fragment implements View.OnClickListener {
    public static Stack<Integer> position_facebook_stack = new Stack<>();
    public static int ALBUMS_IS_SELECTED = 0;
    public static int ALBUM_PICTURES_IS_SELECTED = 1;
    int whatIsSelected = 0;
    LoginButton fbLoginBTN;
    public static Button backBtn;
    String Tag = "MyFbFrag";
    CallbackManager callbackManager;
    RecyclerView recyclerViewAlbums;
    RecyclerView recyclerViewPhotos;
    int lastLastitem, lastLastItemAlbumPicture;
    RelativeLayout loginRL, fbFragContentRL;
    String nextUrlForAlbumPictures;
    int visibleItemCountAlbums, firstVisibleItemAlbums, totalItemCountAlbums;
    int visibleItemCountAlbumPictures, firstVisibleItemAlbumPictures, totalItemCountAlbumPictures;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    int load_more_fb_albums = 1;
    int load_more_album_pictures = 1;
    //    ArrayList<data> dataModelArrayList = new ArrayList<>();
    AccessToken accessToken;
    static View myView;
    ImageView fbFragmentLoginIV;
    FacebookAlbumsAdaptersRV facebookAlbumsAdaptersRV;
    String profileUserId;
    ProfileTracker profileTracker;
    /*    Button facebookFragmentSubmitBTN;*/
    ArrayList<GenericImageModel> genericFbImageModelsAL;
    //    ArrayList<SingleImageFbModel> singleImageFbModelsAL;
    AccessTokenTracker accessTokenTracker;
    SingleImageAdapterFbRV singleImageAdapterFbRV;
    //    ArrayList<data> albumArrayListmodel;
    ArrayList<String> albumImageurls = new ArrayList<>();
    String nextUrl;
    RelativeLayout mainContentRL;
    ScaleInAnimator scaleInAnimator = new ScaleInAnimator();
    Button fblogoutBtn;
    ImageView imageviewtop, imageviewBottom;

    public MyFbFrag() {
        // Required empty public constructor
    }

    public static MyFbFrag newInstance(String param1, String param2) {
        MyFbFrag fragment = new MyFbFrag();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(this.getActivity());
//        LoginManager.getInstance().logOut();
//        LoginManager.getInstance().logOut();

        AccessToken accessToken1 = AccessToken.getCurrentAccessToken();
        if (accessToken1 != null) {

        }

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };
        EventBus.getDefault().registerSticky(this);
//        EventBus.getDefault().register(this);
//        accessTokenTracker.startTracking();


        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            }
        };

        accessTokenTracker.startTracking();

        profileTracker.startTracking();

    }

    void displayProfileMessage(Profile profile) {
        if (profile != null)
//            welcomeMessageTV.setText("Welcome "+profile.getName());
            fbFragContentRL.setVisibility(View.VISIBLE);
        try {
//            profileView.setProfileId(profile.getId());
        } catch (NullPointerException e) {
            e.printStackTrace();
//            profileView.setProfileId(user_id);
        }
    }

    @Override
    public void onStop() {
        load_more_album_pictures = 1;
        load_more_fb_albums = 1;
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_fb, container, false);
        myView = view;
        init(view);
        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
        sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
//        if (isLoggedIn()){}//// TODO: 22/9/16 if any problem in current scenario
        if (sharedPreferenceUtils.getSharedPreferenceBooleanName(Constants.LOGGED_IN_USING_FB) == true) {
            loginRL.setVisibility(View.GONE);
            imageviewtop.setVisibility(View.GONE);
            imageviewBottom.setVisibility(View.GONE);
//            lockTV.setVisibility(View.GONE);
            fbFragContentRL.setVisibility(View.VISIBLE);
            fblogoutBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.VISIBLE);
            getProfileDetails();
        } else {
            loginRL.setVisibility(View.VISIBLE);
            imageviewtop.setVisibility(View.VISIBLE);
            imageviewBottom.setVisibility(View.VISIBLE);
//            lockTV.setVisibility(View.VISIBLE);
            fbFragContentRL.setVisibility(View.GONE);
        }
        return view;
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void init(View view) {
        recyclerViewAlbums = (RecyclerView) view.findViewById(R.id.fbAlbumsRV);
        fblogoutBtn = (Button) view.findViewById(R.id.fblogoutBtn);
       /* facebookFragmentSubmitBTN = (Button) view.findViewById(R.id.fbFragSubmitBTN);*/
 /*       facebookFragmentSubmitBTN.setOnClickListener(this);*/
        loginRL = (RelativeLayout) view.findViewById(R.id.loginRL);
        imageviewBottom = (ImageView) view.findViewById(R.id.imageviewBottom);
        imageviewtop = (ImageView) view.findViewById(R.id.imageviewtop);
        mainContentRL = (RelativeLayout) view.findViewById(R.id.mainContentRL);
        fbFragContentRL = (RelativeLayout) view.findViewById(R.id.fragmentFbContentRL);
//        fbFragmentLoginIV = (RelativeLayout) view.findViewById(R.id.fragmentFbContentRL);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewPhotos = (RecyclerView) view.findViewById(R.id.facebookPhotosRV);
        backBtn = (Button) view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
//        linearLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        SlideInLeftAnimator slideInLeftAnimator = new SlideInLeftAnimator();
        slideInLeftAnimator.setAddDuration(2000);
        slideInLeftAnimator.setRemoveDuration(2000);
        recyclerViewAlbums.setItemAnimator(slideInLeftAnimator);
        recyclerViewPhotos.setLayoutManager(gridLayoutManager);
        recyclerViewPhotos.setHasFixedSize(true);
        recyclerViewAlbums.setLayoutManager(linearLayoutManager);
        recyclerViewAlbums.setHasFixedSize(true);
        fbFragmentLoginIV = (ImageView) view.findViewById(R.id.FbFragmentLoginIV);
//        fbFragmentLogoutIV = (ImageView) view.findViewById(R.id.fbFragmentLogoutIV);
        fbFragmentLoginIV.setOnClickListener(this);
        fblogoutBtn.setOnClickListener(this);
//        fbFragmentLogoutIV.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FbFragmentLoginIV: {
                fbLogin();

                break;
            }

            case R.id.fblogoutBtn:

                LoginManager.getInstance().logOut();
//                fbFragmentLogoutIV.setVisibility(View.GONE);
                fblogoutBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.GONE);
                fbFragContentRL.setVisibility(View.GONE);
                loginRL.setVisibility(View.VISIBLE);
                imageviewtop.setVisibility(View.VISIBLE);
                imageviewBottom.setVisibility(View.VISIBLE);
                SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
                sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
                sharedPreferenceUtils.updateSharedPreferenceBooleanSingle(Constants.LOGGED_IN_USING_FB, false);


                if (FacebookAlbumsAdaptersRV.albumPictureModelAL != null) {
                    FacebookAlbumsAdaptersRV.albumPictureModelAL.clear();
                }


                if (SingleImageAdapterFbRV.genericFbImageModelAL == null) {

                } else {
                    SingleImageAdapterFbRV.genericFbImageModelAL.clear();
                }


                break;


            case R.id.backBtn: {



                if (MainActivity.position_facebook_stack.empty()) // if stack is empty or was on fb albums part
                {

                } else if (MainActivity.position_facebook_stack.get(0) == 1) // if photos were shown from albums
                {
                    EventBus.getDefault().post(new Integer(1)); //come back to albums
                    MainActivity.position_facebook_stack.remove(0); // remove the value from stack
                }
                break;
            }


        }


    }


    private void fbLogin() {
        LoginManager.getInstance().logInWithReadPermissions(MyFbFrag.this, Arrays.asList("public_profile,email,user_photos"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginRL.setVisibility(View.GONE);
                imageviewtop.setVisibility(View.GONE);
                imageviewBottom.setVisibility(View.GONE);
//                lockTV.setVisibility(View.GONE);
          /*      facebookFragmentSubmitBTN.setVisibility(View.VISIBLE);*/
                SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
                sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
                sharedPreferenceUtils.updateSharedPreferenceBooleanSingle(Constants.LOGGED_IN_USING_FB, true);
                fbFragContentRL.setVisibility(View.VISIBLE);
                load_more_fb_albums = 1;
                load_more_album_pictures = 1;
                getProfileDetails();
                fblogoutBtn.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
//                callSnackbar("Login failure : ", Color.WHITE);
            }

            @Override
            public void onError(FacebookException error) {
                callSnackbar(String.valueOf(error), Color.WHITE);
            }
        });
    }

    private void getProfileDetails() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    String name = object.getString("name");
                    profileUserId = object.getString("id");
                    String link = object.getString("link");
//                    String birthday = object.getString("birthday");
                    String gender = object.getString("gender");
//                    String emailId = object.getString("email");
                    Log.d("onCompleted get albums", "onCompleted() called with: " + "object = [" + object + "], response = [" + response + "]");
                    getFbAlbums();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error e", e.toString());
                    Log.d("onError get albums", "onCompleted() called with: " + "object = [" + object + "], response = [" + response + "]");
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link,birthday,gender");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayProfileMessage(profile);
        init(myView);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        final ArrayList<AlbumPictureModel> albumPictureModelsAL = new ArrayList<AlbumPictureModel>();
        recyclerViewAlbums.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCountAlbums = linearLayoutManager.getChildCount();
                    totalItemCountAlbums = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    albumPictureModelsAL.clear();

                    firstVisibleItemAlbums = linearLayoutManager.findFirstVisibleItemPosition();
                    if (totalItemCountAlbums == (visibleItemCountAlbums + firstVisibleItemAlbums) && load_more_fb_albums == 1) {
                        load_more_fb_albums = 0;
                        getFbAlbumsOnScroll();
                    }
                }
            }
        });


        recyclerViewPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCountAlbumPictures = gridLayoutManager.getChildCount();
                    totalItemCountAlbumPictures = gridLayoutManager.getItemCount();
                    int lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                    albumPictureModelsAL.clear();

                    firstVisibleItemAlbumPictures = gridLayoutManager.findFirstVisibleItemPosition();
                    if (totalItemCountAlbumPictures == (visibleItemCountAlbumPictures + firstVisibleItemAlbumPictures) && load_more_album_pictures == 1) {
//                        if (lastLastItemAlbumPicture != lastVisibleItem) { //to avoid multiple calls for last item, declare it as a field in your Activity
//                            lastLastItemAlbumPicture = lastVisibleItem;
//                            getAlbumsPicsOnScroll();
//                            dialogUtil.progressDialog(getActivity(), "Please Wait");
//                            // Your async task here
//                        }
                        load_more_album_pictures = 0;
                        getAlbumsPicsOnScroll();
                    }
                }
            }
        });
    }

    private void getAlbumsPicsOnScroll() {
        final JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, nextUrlForAlbumPictures, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject object = response;

                long responseCode = object.optLong("responseCode");
                Boolean isNextAvailable = object.optJSONObject("paging").has("next");
                Log.e("nextUrl Album Pics : ", nextUrlForAlbumPictures);
                if (!isNextAvailable) {
                    load_more_album_pictures = 0;
                } else {
                    nextUrlForAlbumPictures = object.optJSONObject("paging").optString("next");
                    load_more_album_pictures = 1;
                }
                JSONArray dataArray = null;
                //fethcing the data in the json object
                dataArray = object.optJSONArray("data");
                genericFbImageModelsAL = new ArrayList<>();
                genericFbImageModelsAL.clear();

                for (int i = 0; i < dataArray.length(); i++) {
//                    SingleImageFbModel singleImageFbModel = new SingleImageFbModel();
                    GenericImageModel genericImageModel = new GenericImageModel();
                    try {
                        genericImageModel.setSource_id(dataArray.getJSONObject(i).optString("id"));
                        genericImageModel.setSource_path(dataArray.getJSONObject(i).optString("source"));
                        genericImageModel.setImageSelected(false);
                        genericImageModel.setSource_type(Constants.SOURCE_TYPE_FOR_FACEBOOK_IN_OUR_SERVER);
                        genericImageModel.setUpdateHapperned(false);
                        genericImageModel.setWhereIsSaved(Constants.SAVED_LOCALLY);
                        genericImageModel.setIsUploaded(false);
                        genericFbImageModelsAL.add(genericImageModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                singleImageAdapterFbRV.addToAdapter(genericFbImageModelsAL);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                callSnackbar(String.valueOf(error), Color.WHITE);

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().add(jsonObjectRequest);

    }

    private void getFbAlbumsOnScroll() {
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, nextUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject object = response;
//                                try {
                nextUrl = object.optJSONObject("paging").optString("next");
                Boolean isNextAvailable = object.optJSONObject("paging").has("next");
                Log.e("nextUrl : ", nextUrl);
                if (!isNextAvailable) {
                    load_more_fb_albums = 0;
                } else {
                    load_more_fb_albums = 1;
                }
//                                    System.out.println(nextUrl);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                    JSONObject object = response;
//                                    nextUrl = jsonObject.optJSONObject("paging").optString("next");
//                                    JSONArray jsonAlbumList = object.optJSONArray("data");
//                                    commonUtility.callLog("MyFbFrag : ", " album List ", jsonAlbumList.toString());

                ArrayList<AlbumPictureModel> albumPictureModelsAL = new ArrayList<AlbumPictureModel>();
                final JSONArray jsonAlbumList = object.optJSONArray("data");
                for (int i = 0; i < jsonAlbumList.length(); i++) {
                    try {

                        //fetching the inner data from data
                        JSONObject innerJsonObject = jsonAlbumList.getJSONObject(i);
                        final AlbumPictureModel albumPictureModel = new AlbumPictureModel();
                        albumPictureModel.setId(innerJsonObject.optString("id"));
                        albumPictureModel.setUrl(innerJsonObject.optJSONObject("picture").optJSONObject("data").optString("url"));
                        albumPictureModel.setName(innerJsonObject.optString("name"));
                        albumPictureModelsAL.add(albumPictureModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                facebookAlbumsAdaptersRV.addToAdapter(albumPictureModelsAL);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//                callSnackbar(String.valueOf(error), Color.WHITE);

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().add(jsonObjectRequest);
    }

    public void getFbAlbums() {


        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "/" + profileUserId + "/albums", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {

                Log.e("MyResponse", response.toString());
                //get the whole  response in json object

                JSONObject jsonObject = response.getJSONObject();
                //working to get the data in arralist
//                Type listType = new TypeToken<ArrayList<data>>() {
//                }.getType();
//               get the the data objects in arraylist
//                albumArrayListmodel = new Gson().fromJson(String.valueOf(jsonObject.optJSONArray("data")), listType);
//                nextUrl = jsonObject.optJSONObject("paging").optString("next");
                if (jsonObject.has("paging")) {
                    nextUrl = jsonObject.optJSONObject("paging").optString("next");
                } else {
                    nextUrl = null;
                }
//                commonUtility.callLog("MyFbFrag :", " album nextUrl = ", nextUrl);
                ArrayList<AlbumPictureModel> albumPictureModelsAL = new ArrayList<AlbumPictureModel>();

                final JSONArray jsonAlbumList = jsonObject.optJSONArray("data");
                for (int i = 0; i < jsonAlbumList.length(); i++) {
                    try {

                        //fetching the inner data from data
                        JSONObject innerJsonObject = jsonAlbumList.getJSONObject(i);
                        final AlbumPictureModel albumPictureModel = new AlbumPictureModel();
                        albumPictureModel.setId(innerJsonObject.optString("id"));
                        albumPictureModel.setUrl(innerJsonObject.optJSONObject("picture").optJSONObject("data").optString("url"));
                        albumPictureModel.setName(innerJsonObject.optString("name"));
                        albumPictureModelsAL.add(albumPictureModel);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

//                commonUtility.callLog("MyFbFrag", " facebook data model array list", albumArrayListmodel.toString());
                facebookAlbumsAdaptersRV = new FacebookAlbumsAdaptersRV(getActivity(), albumPictureModelsAL, loginRL, fbFragContentRL);
                recyclerViewAlbums.setAdapter(facebookAlbumsAdaptersRV);

                jsonObject.optJSONObject("graphObject");
//                nextUrl = jsonObject.optJSONObject("paging").optString("next");
                JSONArray albumList = jsonObject.optJSONArray("data");
            }
        });
        Bundle parameters2 = new Bundle();
        parameters2.putString("fields", "id,name,picture");
        parameters2.putString("type", "album");
        parameters2.putString("limit", "10");
        graphRequest.setParameters(parameters2);
        graphRequest.executeAsync();

        Bundle params = new Bundle();
        params.putString("type", "thumbnail");
        GraphRequest graphRequest1 = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/296196750421689/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject1 = response.getJSONObject();
//                                            albumPictureModel.setUrl(response);
                    }
                }
        );

        graphRequest1.executeAsync();

        String token = AccessToken.getCurrentAccessToken().getToken();
        String url = "https://graph.facebook.com/" + profileUserId + "/picture?access_token=" + token + "&type=normal";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//       onActivityResult() forward the login results to the callbackManager created in onCreate():
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onEvent(FacebookAlbumIdMsg ob) {
        EventBus.getDefault().removeAllStickyEvents();
        getPhotosBasedOnAlbumId(ob.getId());
        MainActivity.position_facebook_stack.add(1);
        whatIsSelected = ALBUMS_IS_SELECTED;
    }

    public void onEvent(Integer getFbAlbumsOnBackPress) {
        recyclerViewPhotos.setVisibility(View.GONE);
        recyclerViewAlbums.setVisibility(View.VISIBLE);
        whatIsSelected = ALBUM_PICTURES_IS_SELECTED;
    }

    private void getPhotosBasedOnAlbumId(String AlbumId) {
        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + AlbumId + "/photos", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                //converting the response to jsom object

                JSONObject jsonObject = response.getJSONObject();

                JSONArray dataArray = null;
                //fethcing the data in the json object
                dataArray = jsonObject.optJSONArray("data");
                try {
                    nextUrlForAlbumPictures = jsonObject.optJSONObject("paging").optString("next");
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
//                    Toast.makeText(getContext(),"No Photos In this Album",Toast.LENGTH_SHORT).show();
                    callSnackbar("No pics in this album", Color.WHITE);
                }

//                singleImageFbModelsAL = new ArrayList<>();
                genericFbImageModelsAL = new ArrayList<>();

                for (int i = 0; i < dataArray.length(); i++) {
//                    SingleImageFbModel singleImageFbModel = new SingleImageFbModel();
                    GenericImageModel genericImageModel = new GenericImageModel();
                    try {
                        genericImageModel.setSource_id(dataArray.getJSONObject(i).optString("id"));
                        genericImageModel.setUpdateHapperned(false);
                        genericImageModel.setWhereIsSaved(Constants.SAVED_LOCALLY);
                        genericImageModel.setSource_path(dataArray.getJSONObject(i).optString("source"));
                        genericImageModel.setImageSelected(false);
                        genericFbImageModelsAL.add(genericImageModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                singleImageAdapterFbRV = new SingleImageAdapterFbRV(getActivity(), genericFbImageModelsAL, loginRL, fbFragContentRL);
                recyclerViewPhotos.setAdapter(singleImageAdapterFbRV);
                recyclerViewAlbums.setVisibility(View.GONE);
                recyclerViewPhotos.setVisibility(View.VISIBLE);

//                recyclerViewAlbums.setAdapter();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "created_time,name,id,source");
        parameters.putString("limit", "15");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }


    public void onEvent(UpdateAdapters updateAdapters) {
        if (updateAdapters.getSent_by() != Constants.SOURCE_TYPE_FOR_FACEBOOK_IN_OUR_SERVER) {
            singleImageAdapterFbRV.notifyDataSetChanged();
        }

    }

    public void onEvent(ClearAdaptersOnLogout ob) {
        if (ob.getI() == 1) //if sent by album
        {
            facebookAlbumsAdaptersRV.clearAllAlbumData();
            facebookAlbumsAdaptersRV.notifyDataSetChanged();
            loginRL.setVisibility(View.VISIBLE);
            imageviewBottom.setVisibility(View.VISIBLE);
            imageviewtop.setVisibility(View.VISIBLE);
            load_more_fb_albums = 1;
//            lockTV.setVisibility(View.VISIBLE);
   /*         facebookFragmentSubmitBTN.setVisibility(View.GONE);*/
        }


        if (ob.getI() == 2) //if se {
        {
            singleImageAdapterFbRV.clearAllData();
            singleImageAdapterFbRV.notifyDataSetChanged();
            loginRL.setVisibility(View.GONE);
            imageviewBottom.setVisibility(View.GONE);
            imageviewtop.setVisibility(View.GONE);
//            lockTV.setVisibility(View.VISIBLE);
/*            facebookFragmentSubmitBTN.setVisibility(View.VISIBLE);*/
            load_more_album_pictures = 1;
        }

    }

    private void callSnackbar(String text, int color) {
        Snackbar snackbar = Snackbar.make(mainContentRL, text, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(color);
        snackbar.show();
    }


}


//package com.codeyeti.anaventures.fragments;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.codeyeti.anaventures.R;
//import com.codeyeti.anaventures.activities.SelectForUploadActivity;
//import com.codeyeti.anaventures.adapters.ClearAdaptersOnLogout;
//import com.codeyeti.anaventures.adapters.FacebookAlbumsAdaptersRV;
//import com.codeyeti.anaventures.adapters.SingleImageAdapterFbRV;
//import com.codeyeti.anaventures.constants.Constants;
//import com.codeyeti.anaventures.controller.AppController;
//import com.codeyeti.anaventures.models.AlbumPictureModel;
//import com.codeyeti.anaventures.models.FacebookAlbumIdMsg;
//import com.codeyeti.anaventures.models.GenericImageModel;
//import com.codeyeti.anaventures.models.TaggedImagesModel;
//import com.codeyeti.anaventures.models.UpdateAdapters;
//import com.codeyeti.anaventures.utils.CommonUtility;
//import com.codeyeti.anaventures.utils.DialogUtil;
//import com.codeyeti.anaventures.utils.SharedPreferenceUtils;
//import com.facebook.AccessToken;
//import com.facebook.AccessTokenTracker;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.HttpMethod;
//import com.facebook.Profile;
//import com.facebook.ProfileTracker;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.gson.GsonBuilder;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Stack;
//
//import de.greenrobot.event.EventBus;
//import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
//
///**
// * Created by palash on 22/7/16.
// */
//public class MyFbFrag extends Fragment implements View.OnClickListener {
//    public static Stack<Integer> position_facebook_stack = new Stack<>();
//    public static int ALBUMS_IS_SELECTED = 0;
//    public static int ALBUM_PICTURES_IS_SELECTED = 1;
//    int whatIsSelected = 0;
//    LoginButton fbLoginBTN;
//    Button backBtn;
//    String Tag = "MyFbFrag";
//    CallbackManager callbackManager;
//    RecyclerView recyclerViewAlbums;
//    RecyclerView recyclerViewPhotos;
//    int lastLastitem, lastLastItemAlbumPicture;
//    RelativeLayout loginRL, fbFragContentRL;
//    String nextUrlForAlbumPictures;
//    CommonUtility commonUtility = new CommonUtility();
//    final DialogUtil dialogUtil = new DialogUtil();
//    int visibleItemCountAlbums, firstVisibleItemAlbums, totalItemCountAlbums;
//    int visibleItemCountAlbumPictures, firstVisibleItemAlbumPictures, totalItemCountAlbumPictures;
//    LinearLayoutManager linearLayoutManager;
//    GridLayoutManager gridLayoutManager;
//    int load_more_fb_albums = 1;
//    int load_more_album_pictures = 1;
//    //    ArrayList<data> dataModelArrayList = new ArrayList<>();
//    AccessToken accessToken;
//    static View myView;
//    ImageView fbFragmentLoginIV;
//    FacebookAlbumsAdaptersRV facebookAlbumsAdaptersRV;
//    String profileUserId;
//    ProfileTracker profileTracker;
//    /*    Button facebookFragmentSubmitBTN;*/
//    ArrayList<GenericImageModel> genericFbImageModelsAL;
//    ArrayList<TaggedImagesModel> taggedImagesModelArrayList;
//    //    ArrayList<SingleImageFbModel> singleImageFbModelsAL;
//    AccessTokenTracker accessTokenTracker;
//    SingleImageAdapterFbRV singleImageAdapterFbRV;
//    //    ArrayList<data> albumArrayListmodel;
//    ArrayList<String> albumImageurls = new ArrayList<>();
//    String nextUrl;
//    RelativeLayout mainContentRL;
//    ScaleInAnimator scaleInAnimator = new ScaleInAnimator();
//    Button fblogoutBtn;
//    ImageView imageviewtop, imageviewBottom;
//    ArrayList<AlbumPictureModel> albumPictureModelsAL = new ArrayList<AlbumPictureModel>();
//    public MyFbFrag() {
//        // Required empty public constructor
//    }
//
//    public static MyFbFrag newInstance(String param1, String param2) {
//        MyFbFrag fragment = new MyFbFrag();
//        Bundle args = new Bundle();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        FacebookSdk.sdkInitialize(this.getActivity());
////        LoginManager.getInstance().logOut();
////        LoginManager.getInstance().logOut();
//
//        AccessToken accessToken1 = AccessToken.getCurrentAccessToken();
//        if (accessToken1 != null) {
//            commonUtility.callLog(Tag, " accessToken : ", accessToken1.toString());
//
//        }
//
//        callbackManager = CallbackManager.Factory.create();
//
//        accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                accessToken = currentAccessToken;
//            }
//        };
//        EventBus.getDefault().registerSticky(this);
////        EventBus.getDefault().register(this);
////        accessTokenTracker.startTracking();
//
//
//        profileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//            }
//        };
//
//        accessTokenTracker.startTracking();
//
//        profileTracker.startTracking();
//
//    }
//
//    void displayProfileMessage(Profile profile) {
//        if (profile != null)
////            welcomeMessageTV.setText("Welcome "+profile.getName());
//            fbFragContentRL.setVisibility(View.VISIBLE);
//        try {
////            profileView.setProfileId(profile.getId());
//        } catch (NullPointerException e) {
//            e.printStackTrace();
////            profileView.setProfileId(user_id);
//        }
//    }
//
//    @Override
//    public void onStop() {
//        load_more_album_pictures = 1;
//        load_more_fb_albums = 1;
//        super.onStop();
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_my_fb, container, false);
//        myView = view;
//        init(view);
//        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
//        sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
////        if (isLoggedIn()){}//// TODO: 22/9/16 if any problem in current scenario
//        if (sharedPreferenceUtils.getSharedPreferenceBooleanName(Constants.LOGGED_IN_USING_FB) == true) {
//            loginRL.setVisibility(View.GONE);
//            imageviewtop.setVisibility(View.GONE);
//            imageviewBottom.setVisibility(View.GONE);
////            lockTV.setVisibility(View.GONE);
//            fbFragContentRL.setVisibility(View.VISIBLE);
//            fblogoutBtn.setVisibility(View.VISIBLE);
//            backBtn.setVisibility(View.VISIBLE);
//            getProfileDetails();
//        } else {
//            loginRL.setVisibility(View.VISIBLE);
//            imageviewtop.setVisibility(View.VISIBLE);
//            imageviewBottom.setVisibility(View.VISIBLE);
////            lockTV.setVisibility(View.VISIBLE);
//            fbFragContentRL.setVisibility(View.GONE);
//        }
//        return view;
//    }
//
//    public boolean isLoggedIn() {
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        return accessToken != null;
//    }
//
//    private void init(View view) {
//        recyclerViewAlbums = (RecyclerView) view.findViewById(R.id.fbAlbumsRV);
//        fblogoutBtn = (Button) view.findViewById(R.id.fblogoutBtn);
//       /* facebookFragmentSubmitBTN = (Button) view.findViewById(R.id.fbFragSubmitBTN);*/
// /*       facebookFragmentSubmitBTN.setOnClickListener(this);*/
//        loginRL = (RelativeLayout) view.findViewById(R.id.loginRL);
//        imageviewBottom = (ImageView) view.findViewById(R.id.imageviewBottom);
//        imageviewtop = (ImageView) view.findViewById(R.id.imageviewtop);
//        mainContentRL = (RelativeLayout) view.findViewById(R.id.mainContentRL);
//        fbFragContentRL = (RelativeLayout) view.findViewById(R.id.fragmentFbContentRL);
////        fbFragmentLoginIV = (RelativeLayout) view.findViewById(R.id.fragmentFbContentRL);
//        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
////        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerViewPhotos = (RecyclerView) view.findViewById(R.id.facebookPhotosRV);
//        backBtn = (Button) view.findViewById(R.id.backBtn);
//        backBtn.setOnClickListener(this);
////        linearLayoutManager.setAutoMeasureEnabled(true);
//        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
//
//        SlideInLeftAnimator slideInLeftAnimator = new SlideInLeftAnimator();
//        slideInLeftAnimator.setAddDuration(2000);
//        slideInLeftAnimator.setRemoveDuration(2000);
//        recyclerViewAlbums.setItemAnimator(slideInLeftAnimator);
//        recyclerViewPhotos.setLayoutManager(gridLayoutManager);
//        recyclerViewPhotos.setHasFixedSize(true);
//        recyclerViewAlbums.setLayoutManager(linearLayoutManager);
//        recyclerViewAlbums.setHasFixedSize(true);
//        fbFragmentLoginIV = (ImageView) view.findViewById(R.id.FbFragmentLoginIV);
////        fbFragmentLogoutIV = (ImageView) view.findViewById(R.id.fbFragmentLogoutIV);
//        fbFragmentLoginIV.setOnClickListener(this);
//        fblogoutBtn.setOnClickListener(this);
////        fbFragmentLogoutIV.setOnClickListener(this);
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.FbFragmentLoginIV: {
//                fbLogin();
//
//                break;
//            }
//          /*  case R.id.fbFragSubmitBTN: {
//                if (AppController.getTotalCountImages() == 0) {
//                    Snackbar snackbar = Snackbar.make(mainContentRL, "Please select images first", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                } else {
//                    int size = AppController.getInstance().selectedGenericImageModelsAL.size();
//
//                    for (int i = 0; i < size; i++) {
//                        AppController.getInstance().selectedGenericImageModelsAL.get(i).setImageSelected(false);
//                    }
//                    CommonUtility commonUtility = new CommonUtility();
//                    commonUtility.callIntentClearFlagTop(getActivity(), SelectedPhotosPreviewActivity.class);
//                }
//
//
//                break;
//            }*/
////            case R.id.fbFragmentLogoutIV: {
////                LoginManager.getInstance().logOut();
//////                fbFragmentLogoutIV.setVisibility(View.GONE);
////                fbFragContentRL.setVisibility(View.GONE);
////                loginRL.setVisibility(View.VISIBLE);
////
////
////                SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
////                sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
////                sharedPreferenceUtils.updateSharedPreferenceBooleanSingle(Constants.LOGGED_IN_USING_FB, false);
////                break;
////            }
//
//            case R.id.fblogoutBtn: {
//
//                LoginManager.getInstance().logOut();
////                fbFragmentLogoutIV.setVisibility(View.GONE);
//                fblogoutBtn.setVisibility(View.GONE);
//                backBtn.setVisibility(View.GONE);
//                fbFragContentRL.setVisibility(View.GONE);
//                loginRL.setVisibility(View.VISIBLE);
//                imageviewtop.setVisibility(View.VISIBLE);
//                imageviewBottom.setVisibility(View.VISIBLE);
//
//
//                SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
//                sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
//                sharedPreferenceUtils.updateSharedPreferenceBooleanSingle(Constants.LOGGED_IN_USING_FB, false);
//                break;
//
//            }
//
//            case R.id.backBtn: {
//
////                Toast.makeText(getActivity(), "Hello World", Toast.LENGTH_SHORT).show();
//
//
//                if (SelectForUploadActivity.position_facebook_stack.empty()) // if stack is empty or was on fb albums part
//                {
//
//                } else if (SelectForUploadActivity.position_facebook_stack.get(0) == 1) // if photos were shown from albums
//                {
//                    EventBus.getDefault().post(new Integer(1)); //come back to albums
//                    SelectForUploadActivity.position_facebook_stack.remove(0); // remove the value from stack
//                }
//                break;
//            }
//
//
//        }
//
//
//    }
//
//
//    private void fbLogin() {
//        final DialogUtil dialogUtil = new DialogUtil();
//        dialogUtil.progressDialog(getActivity(), "Please Wait..");
//        LoginManager.getInstance().logInWithReadPermissions(MyFbFrag.this, Arrays.asList("public_profile,email,user_photos"));
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                dialogUtil.stopProgressDialog();
//                loginRL.setVisibility(View.GONE);
//                imageviewtop.setVisibility(View.GONE);
//                imageviewBottom.setVisibility(View.GONE);
////                lockTV.setVisibility(View.GONE);
//          /*      facebookFragmentSubmitBTN.setVisibility(View.VISIBLE);*/
//                SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
//                sharedPreferenceUtils.initializeSharedPreference(getActivity(), Constants.SHARED_PREFERENCE_NAME);
//                sharedPreferenceUtils.updateSharedPreferenceBooleanSingle(Constants.LOGGED_IN_USING_FB, true);
//                fbFragContentRL.setVisibility(View.VISIBLE);
//                load_more_fb_albums = 1;
//                load_more_album_pictures = 1;
//                getProfileDetails();
//                fblogoutBtn.setVisibility(View.VISIBLE);
//                backBtn.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onCancel() {
//                dialogUtil.stopProgressDialog();
//                LoginManager.getInstance().logOut();
//                commonUtility.callLog(Tag, "fb on cancel method", "running");
////                callSnackbar("Login failure : ", Color.WHITE);
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                dialogUtil.stopProgressDialog();
////                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//                callSnackbar(String.valueOf(error), Color.WHITE);
//            }
//        });
//    }
//
//    private void getProfileDetails() {
//        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//                try {
//
//                    String name = object.getString("name");
//                    profileUserId = object.getString("id");
//                    String link = object.getString("link");
////                    String birthday = object.getString("birthday");
//                    String gender = object.getString("gender");
////                    String emailId = object.getString("email");
//                    Log.d("onCompleted get albums", "onCompleted() called with: " + "object = [" + object + "], response = [" + response + "]");
//                    getFbAlbums();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e("Error e", e.toString());
//                    Log.d("onError get albums", "onCompleted() called with: " + "object = [" + object + "], response = [" + response + "]");
//                }
//            }
//        });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,email,link,birthday,gender");
//        graphRequest.setParameters(parameters);
//        graphRequest.executeAsync();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Profile profile = Profile.getCurrentProfile();
//        displayProfileMessage(profile);
//        init(myView);
//
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//
//        commonUtility = new CommonUtility();
//        commonUtility.callLog(Tag, "on Resume fn AccessToken =", String.valueOf(accessToken));
////        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
////        sharedPreferenceUtils.initializeSharedPreference(getActivity(),Constants.SHARED_PREFERENCE_NAME);
////        boolean logged_Using_fb = sharedPreferenceUtils.getSharedPreferenceBooleanName(Constants.LOGGED_IN_USING_FB);
////        if(logged_Using_fb)
////        {
////            getProfileDetails();
////            loginRL.setVisibility(View.GONE);
////            fbFragContentRL.setVisibility(View.VISIBLE);
////        }
//        final ArrayList<AlbumPictureModel> albumPictureModelsAL = new ArrayList<AlbumPictureModel>();
//        recyclerViewAlbums.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
////                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0) {
//                    visibleItemCountAlbums = linearLayoutManager.getChildCount();
//                    totalItemCountAlbums = linearLayoutManager.getItemCount();
//                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//
//                    albumPictureModelsAL.clear();
//                    commonUtility = new CommonUtility();
//
//                    firstVisibleItemAlbums = linearLayoutManager.findFirstVisibleItemPosition();
//                    commonUtility.callLog("MyFbFrag", " visibleItemCountAlbums = ", String.valueOf(visibleItemCountAlbums));
//                    commonUtility.callLog("MyFbFrag", " totalItemCountAlbums = ", String.valueOf(totalItemCountAlbums));
//                    commonUtility.callLog("MyFbFrag", " firstVisbleItem  = ", String.valueOf(firstVisibleItemAlbums));
//                    commonUtility.callLog("MyFbFrag : ", "Load More Fb Albums = ", String.valueOf(load_more_fb_albums));
//                    if (totalItemCountAlbums == (visibleItemCountAlbums + firstVisibleItemAlbums) && load_more_fb_albums == 1) {
//                        load_more_fb_albums = 0;
//                        dialogUtil.progressDialog(getActivity(), "Please Wait");
//                        getFbAlbumsOnScroll();
//                    }
//                }
//            }
//        });
//
//
//        recyclerViewPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {
//                    visibleItemCountAlbumPictures = gridLayoutManager.getChildCount();
//                    totalItemCountAlbumPictures = gridLayoutManager.getItemCount();
//                    int lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
//
//                    albumPictureModelsAL.clear();
//                    commonUtility = new CommonUtility();
//
//                    firstVisibleItemAlbumPictures = gridLayoutManager.findFirstVisibleItemPosition();
//                    commonUtility.callLog("MyFbFrag", " visibleItemCountAlbums = ", String.valueOf(visibleItemCountAlbums));
//                    commonUtility.callLog("MyFbFrag", " totalItemCountAlbums = ", String.valueOf(totalItemCountAlbums));
//                    commonUtility.callLog("MyFbFrag", " firstVisbleItem  = ", String.valueOf(firstVisibleItemAlbums));
//                    if (totalItemCountAlbumPictures == (visibleItemCountAlbumPictures + firstVisibleItemAlbumPictures) && load_more_album_pictures == 1) {
////                        if (lastLastItemAlbumPicture != lastVisibleItem) { //to avoid multiple calls for last item, declare it as a field in your Activity
////                            lastLastItemAlbumPicture = lastVisibleItem;
////                            getAlbumsPicsOnScroll();
////                            dialogUtil.progressDialog(getActivity(), "Please Wait");
////                            // Your async task here
////                        }
//                        load_more_album_pictures = 0;
//                        getAlbumsPicsOnScroll();
//                        dialogUtil.progressDialog(getActivity(), "Please Wait");
//                    }
//                }
//            }
//        });
//    }
//
//    private void getAlbumsPicsOnScroll() {
//        final CommonUtility commonUtility = new CommonUtility();
//        final JSONObject jsonObject = new JSONObject();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, nextUrlForAlbumPictures, jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                JSONObject object = response;
//                dialogUtil.stopProgressDialog();
//                commonUtility.callLog("MyFbFrag  : ", "response of albums : ", response.toString());
//
//                long responseCode = object.optLong("responseCode");
//                commonUtility.callLog("MyFbFrag : ", "responseCode = ", String.valueOf(responseCode));
//                Boolean isNextAvailable = object.optJSONObject("paging").has("next");
//                Log.e("nextUrl Album Pics : ", nextUrlForAlbumPictures);
//                if (!isNextAvailable) {
//                    load_more_album_pictures = 0;
//                } else {
//                    nextUrlForAlbumPictures = object.optJSONObject("paging").optString("next");
//                    load_more_album_pictures = 1;
//                }
//                JSONArray dataArray = null;
//                //fethcing the data in the json object
//                dataArray = object.optJSONArray("data");
//                genericFbImageModelsAL = new ArrayList<>();
//                genericFbImageModelsAL.clear();
//
//                for (int i = 0; i < dataArray.length(); i++) {
////                    SingleImageFbModel singleImageFbModel = new SingleImageFbModel();
//                    GenericImageModel genericImageModel = new GenericImageModel();
//                    try {
//                        genericImageModel.setSource_id(dataArray.getJSONObject(i).optString("id"));
//                        genericImageModel.setSource_path(dataArray.getJSONObject(i).optString("source"));
//                        genericImageModel.setImageSelected(false);
//                        genericImageModel.setSource_type(Constants.SOURCE_TYPE_FOR_FACEBOOK_IN_OUR_SERVER);
//                        genericImageModel.setUpdateHapperned(false);
//                        genericImageModel.setWhereIsSaved(Constants.SAVED_LOCALLY);
//                        genericImageModel.setIsUploaded(false);
//                        genericFbImageModelsAL.add(genericImageModel);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                singleImageAdapterFbRV.addToAdapter(genericFbImageModelsAL);
//                commonUtility.callLog("MyFbFrag", "response on scroll", response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dialogUtil.stopProgressDialog();
////                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//                callSnackbar(String.valueOf(error), Color.WHITE);
//
//            }
//        });
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().getRequestQueue().add(jsonObjectRequest);
//
//    }
//
//    private void getFbAlbumsOnScroll() {
//        JSONObject jsonObject = new JSONObject();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, nextUrl, jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                JSONObject object = response;
//                commonUtility.callLog(Tag, "fb login scroll response", response.toString());
//                dialogUtil.stopProgressDialog();
////                                try {
//                nextUrl = object.optJSONObject("paging").optString("next");
//                Boolean isNextAvailable = object.optJSONObject("paging").has("next");
//                Log.e("nextUrl : ", nextUrl);
//                if (!isNextAvailable) {
//                    load_more_fb_albums = 0;
//                } else {
//                    load_more_fb_albums = 1;
//                }
////                                    System.out.println(nextUrl);
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
////                                    JSONObject object = response;
////                                    nextUrl = jsonObject.optJSONObject("paging").optString("next");
//                commonUtility.callLog("MyFbFrag :", " album response = ", response.toString());
////                                    JSONArray jsonAlbumList = object.optJSONArray("data");
////                                    commonUtility.callLog("MyFbFrag : ", " album List ", jsonAlbumList.toString());
//
//                ArrayList<AlbumPictureModel> albumPictureModelsAL = new ArrayList<AlbumPictureModel>();
//                final JSONArray jsonAlbumList = object.optJSONArray("data");
//                for (int i = 0; i < jsonAlbumList.length(); i++) {
//                    try {
//
//                        //fetching the inner data from data
//                        JSONObject innerJsonObject = jsonAlbumList.getJSONObject(i);
//                        commonUtility.callLog("MyFbFrag : ", "response in object : ", innerJsonObject.optString("type"));
//                        final AlbumPictureModel albumPictureModel = new AlbumPictureModel();
//                        albumPictureModel.setId(innerJsonObject.optString("id"));
//                        albumPictureModel.setUrl(innerJsonObject.optJSONObject("picture").optJSONObject("data").optString("url"));
//                        albumPictureModel.setName(innerJsonObject.optString("name"));
//                        albumPictureModelsAL.add(albumPictureModel);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                facebookAlbumsAdaptersRV.addToAdapter(albumPictureModelsAL);
//
//
//                commonUtility.callLog("MyFbFrag", "response on scroll", response.toString());
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dialogUtil.stopProgressDialog();
////                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
////                callSnackbar(String.valueOf(error), Color.WHITE);
//
//            }
//        });
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().getRequestQueue().add(jsonObjectRequest);
//    }
//
//    public void getFbAlbums() {
//
//
//        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "/" + profileUserId + "/albums", new GraphRequest.Callback() {
//            @Override
//            public void onCompleted(GraphResponse response) {
//                commonUtility = new CommonUtility();
//                Log.e("MyResponse", response.toString());
//                //get the whole  response in json object
//
//                JSONObject jsonObject = response.getJSONObject();
//                //working to get the data in arralist
////                Type listType = new TypeToken<ArrayList<data>>() {
////                }.getType();
////               get the the data objects in arraylist
////                albumArrayListmodel = new Gson().fromJson(String.valueOf(jsonObject.optJSONArray("data")), listType);
////                nextUrl = jsonObject.optJSONObject("paging").optString("next");
//                if (jsonObject.has("paging")) {
//                    nextUrl = jsonObject.optJSONObject("paging").optString("next");
//                } else {
//                    nextUrl = null;
//                }
////                commonUtility.callLog("MyFbFrag :", " album nextUrl = ", nextUrl);
//
//
//                final JSONArray jsonAlbumList = jsonObject.optJSONArray("data");
//                for (int i = 0; i < jsonAlbumList.length(); i++) {
//                    try {
//
//                        //fetching the inner data from data
//                        JSONObject innerJsonObject = jsonAlbumList.getJSONObject(i);
//                        commonUtility.callLog("MyFbFrag : ", "response in object : ", innerJsonObject.optString("type"));
//                        final AlbumPictureModel albumPictureModel = new AlbumPictureModel();
//                        albumPictureModel.setId(innerJsonObject.optString("id"));
//                        albumPictureModel.setUrl(innerJsonObject.optJSONObject("picture").optJSONObject("data").optString("url"));
//                        albumPictureModel.setName(innerJsonObject.optString("name"));
//                        albumPictureModelsAL.add(albumPictureModel);
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                getTaggedImages();
//
////                commonUtility.callLog("MyFbFrag", " facebook data model array list", albumArrayListmodel.toString());
//                facebookAlbumsAdaptersRV = new FacebookAlbumsAdaptersRV(getActivity(), albumPictureModelsAL, loginRL, fbFragContentRL,taggedImagesModelArrayList);
//                recyclerViewAlbums.setAdapter(facebookAlbumsAdaptersRV);
//
//                jsonObject.optJSONObject("graphObject");
////                nextUrl = jsonObject.optJSONObject("paging").optString("next");
//                JSONArray albumList = jsonObject.optJSONArray("data");
//            }
//        });
//        Bundle parameters2 = new Bundle();
//        parameters2.putString("fields", "id,name,picture");
//        parameters2.putString("type", "album");
//        parameters2.putString("limit", "10");
//        graphRequest.setParameters(parameters2);
//        graphRequest.executeAsync();
//
//        Bundle params = new Bundle();
//        params.putString("type", "thumbnail");
//        GraphRequest graphRequest1 = new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/296196750421689/picture",
//                params,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                        JSONObject jsonObject1 = response.getJSONObject();
//                        commonUtility.callLog("MyFbFrag : ", " response of albums ", response.toString());
////                                            albumPictureModel.setUrl(response);
//                    }
//                }
//        );
//
//        graphRequest1.executeAsync();
//
//        String token = AccessToken.getCurrentAccessToken().getToken();
//        String url = "https://graph.facebook.com/" + profileUserId + "/picture?access_token=" + token + "&type=normal";
//        commonUtility = new CommonUtility();
//        commonUtility.callLog("MyFbFrag : ", "Access Token :  ", token);
//    }
//
//    public  void getTaggedImages(){
//
//
//
//        GraphRequest graphRequest1 = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "/" + profileUserId +"/photos/tagged?fields=picture,images,name", new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        Log.e("MyResponsePALASH", response.toString());
//                        try {
//                            JSONObject jsonObject=response.getJSONObject();
//                          String graphObject=  jsonObject.getString("graphObject");
//                            JSONObject jsonObject1=new JSONObject(graphObject);
//                            String data=jsonObject1.getString("data");
//
//                            JSONArray jsonArray=new JSONArray(data);
//
//                            for (int i = 0; i <jsonArray.length() ; i++) {
//                                if(jsonArray.getString(i).equalsIgnoreCase("images")){
//                                    taggedImagesModelArrayList = new ArrayList<TaggedImagesModel>(Arrays.asList(new GsonBuilder().create().fromJson("images",TaggedImagesModel[].class)));
//
//
//                                }
//
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//
//
//
//
//
//
//
//                    }
//                });
//        graphRequest1.executeAsync();
//    }
//
//
//
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////       onActivityResult() forward the login results to the callbackManager created in onCreate():
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void onEvent(FacebookAlbumIdMsg ob) {
//        EventBus.getDefault().removeAllStickyEvents();
//        getPhotosBasedOnAlbumId(ob.getId());
//        SelectForUploadActivity.position_facebook_stack.add(1);
//        whatIsSelected = ALBUMS_IS_SELECTED;
//    }
//
//    public void onEvent(Integer getFbAlbumsOnBackPress) {
//        recyclerViewPhotos.setVisibility(View.GONE);
//        recyclerViewAlbums.setVisibility(View.VISIBLE);
//        whatIsSelected = ALBUM_PICTURES_IS_SELECTED;
//    }
//
//    private void getPhotosBasedOnAlbumId(String AlbumId) {
//        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + AlbumId + "/photos", null, HttpMethod.GET, new GraphRequest.Callback() {
//            @Override
//
//
//                public void onCompleted(GraphResponse response) {
//                CommonUtility commonUtility = new CommonUtility();
//                commonUtility.callLog("MyFbFrag : ", " response get photos from album", response.toString());
//                //converting the response to jsom object
//
//                JSONObject jsonObject = response.getJSONObject();
//
//                JSONArray dataArray = null;
//                //fethcing the data in the json object
//                dataArray = jsonObject.optJSONArray("data");
//                try {
//                    nextUrlForAlbumPictures = jsonObject.optJSONObject("paging").optString("next");
//                } catch (NullPointerException ex) {
//                    ex.printStackTrace();
////                    Toast.makeText(getContext(),"No Photos In this Album",Toast.LENGTH_SHORT).show();
//                    callSnackbar("No pics in this album", Color.WHITE);
//                }
//
////                singleImageFbModelsAL = new ArrayList<>();
//                genericFbImageModelsAL = new ArrayList<>();
//
//                for (int i = 0; i < dataArray.length(); i++) {
////                    SingleImageFbModel singleImageFbModel = new SingleImageFbModel();
//                    GenericImageModel genericImageModel = new GenericImageModel();
//                    try {
//                        genericImageModel.setSource_id(dataArray.getJSONObject(i).optString("id"));
//                        genericImageModel.setUpdateHapperned(false);
//                        genericImageModel.setWhereIsSaved(Constants.SAVED_LOCALLY);
//                        genericImageModel.setSource_path(dataArray.getJSONObject(i).optString("source"));
//                        genericImageModel.setImageSelected(false);
//                        genericFbImageModelsAL.add(genericImageModel);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                singleImageAdapterFbRV = new SingleImageAdapterFbRV(getActivity(), genericFbImageModelsAL, loginRL, fbFragContentRL,null);
//                recyclerViewPhotos.setAdapter(singleImageAdapterFbRV);
//                recyclerViewAlbums.setVisibility(View.GONE);
//                recyclerViewPhotos.setVisibility(View.VISIBLE);
//
////                recyclerViewAlbums.setAdapter();
//                commonUtility.callLog("MyFbFrag", "Graph get Data in Album", dataArray.toString());
//            }
//        });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "created_time,name,id,source");
//        parameters.putString("limit", "15");
//        graphRequest.setParameters(parameters);
//        graphRequest.executeAsync();
//    }
//
//
//    public void onEvent(UpdateAdapters updateAdapters) {
//        if (updateAdapters.getSent_by() != Constants.SOURCE_TYPE_FOR_FACEBOOK_IN_OUR_SERVER) {
//            singleImageAdapterFbRV.notifyDataSetChanged();
//        }
//
//    }
//
//    public void onEvent(ClearAdaptersOnLogout ob) {
//        if (ob.getI() == 1) //if sent by album
//        {
//            facebookAlbumsAdaptersRV.clearAllAlbumData();
//            facebookAlbumsAdaptersRV.notifyDataSetChanged();
//            loginRL.setVisibility(View.VISIBLE);
//            imageviewBottom.setVisibility(View.VISIBLE);
//            imageviewtop.setVisibility(View.VISIBLE);
//            load_more_fb_albums = 1;
////            lockTV.setVisibility(View.VISIBLE);
//   /*         facebookFragmentSubmitBTN.setVisibility(View.GONE);*/
//        }
//
//
//        if (ob.getI() == 2) //if se {
//        {
//            singleImageAdapterFbRV.clearAllData();
//            singleImageAdapterFbRV.notifyDataSetChanged();
//            loginRL.setVisibility(View.GONE);
//            imageviewBottom.setVisibility(View.GONE);
//            imageviewtop.setVisibility(View.GONE);
////            lockTV.setVisibility(View.VISIBLE);
///*            facebookFragmentSubmitBTN.setVisibility(View.VISIBLE);*/
//            load_more_album_pictures = 1;
//        }
//
//    }
//
//    private void callSnackbar(String text, int color) {
//        Snackbar snackbar = Snackbar.make(mainContentRL, text, Snackbar.LENGTH_SHORT);
//        View view = snackbar.getView();
//        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//        tv.setTextColor(color);
//        snackbar.show();
//    }
//
//
//    public void setTaggedImages(ArrayList<TaggedImagesModel> taggedImagesModelArrayList) {
//        singleImageAdapterFbRV = new SingleImageAdapterFbRV(getActivity(), genericFbImageModelsAL, loginRL, fbFragContentRL,taggedImagesModelArrayList);
//        recyclerViewPhotos.setAdapter(singleImageAdapterFbRV);
//        recyclerViewAlbums.setVisibility(View.GONE);
//        recyclerViewPhotos.setVisibility(View.VISIBLE);
//
//
//    }
//}