package carpathy.com.assignmentct.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Stack;

import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.controllers.AppController;
import carpathy.com.assignmentct.utils.Constants;
import carpathy.com.assignmentct.utils.SharedPreferenceUtils;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();
    WebView webView;
    RelativeLayout webViewMainContentRL;
    Stack<Integer> insideTraceStack = new Stack<>();
    ProgressBar progressBar;
    String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webViewMainContentRL = (RelativeLayout) findViewById(R.id.webViewMainContentRL);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        loadInstagramLoginPage();

    }

    private void getAccessTokenFromOurServer() {
        final SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
        sharedPreferenceUtils.initializeSharedPreference(getApplicationContext(), Constants.SHARED_PREFERENCE_NAME);
        String email = "akapoor_91@yahoo.com";

        String url = null;
        try {
            url = Constants.GET_ACCESS_TOKEN_FROM_OUR_SERVER +
                    "?email=" + URLEncoder.encode(email, "UTF-8") +
                    "&type=instagram";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.d(TAG, "response : " + response);

                    Boolean hasStatus = response.has("status");
                    if (hasStatus) {
                        String status = response.getString("status");
                        if (status.equals("fail")) {
                        } else {

                            JSONObject jsonObject = response.getJSONObject("data");
                            access_token = jsonObject.getString("access_token");
                            sharedPreferenceUtils.initializeSharedPreference(getApplicationContext(), Constants.SHARED_PREFERENCE_NAME);
                            sharedPreferenceUtils.updateSharedPreferenceStringSingle(Constants.PRFERENCE_INSTAGRAM_ACCESS_TOKEN, access_token);
                            finish();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().add(jsonObjectRequest);
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    public void removeAccessToken(Context context) {


        if (access_token != null && access_token.trim().length() != 0) {
            access_token = "";
            webView = (WebView) findViewById(R.id.webView1);
            webView.clearHistory();
            webView.clearFormData();
            webView.clearSslPreferences();
            webView.clearCache(true);
            insideTraceStack.add(1);
        } else {
            Toast.makeText(context, "Access token is null ", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadInstagramLoginPage() {

        String email = "akapoor_91@yahoo.com";
        String url = Constants.GET_ACCESS_TOKEN_INSTAGRAM + email + "&response_type=code";


        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("code=")) {
                    getAccessTokenFromOurServer();
                }
            }
        };

        CookieSyncManager.createInstance(WebViewActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieManager.getInstance().removeAllCookie();
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);
        webView.clearMatches();
        webView.clearHistory();
        webView.clearFormData();
        webView.clearSslPreferences();
        webView.clearCache(true);
        webView.loadUrl(url);

    }
}