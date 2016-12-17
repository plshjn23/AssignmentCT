package carpathy.com.assignmentct.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.ViewFlipper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import carpathy.com.assignmentct.controllers.AppController;
import carpathy.com.assignmentct.adapters.ClearAdaptersOnLogout;
import carpathy.com.assignmentct.interfaces.CommunicationInterface;
import carpathy.com.assignmentct.adapters.FacebookAlbumsAdaptersRV;
import carpathy.com.assignmentct.adapters.GaleryPhotosAdapter;
import carpathy.com.assignmentct.fragments.GalerySelectFragment;
import carpathy.com.assignmentct.adapters.InstagramPhotosAdapter;
import carpathy.com.assignmentct.fragments.InstagramSelectFragment;
import carpathy.com.assignmentct.fragments.MyFbFrag;
import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.adapters.SingleImageAdapterFbRV;
import carpathy.com.assignmentct.adapters.UpdateAdapters;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements android.support.v7.app.ActionBar.TabListener, InstagramPhotosAdapter.Update, GaleryPhotosAdapter.UpdateByGalery, SingleImageAdapterFbRV.UpdateByFacebookInterface, FacebookAlbumsAdaptersRV.MyFbLogoutInterface, SingleImageAdapterFbRV.MyFbLogoutInterfaceImage, InstagramPhotosAdapter.MyInstagramLogoutInterface, CommunicationInterface {


    public static Stack<Integer> position_facebook_stack = new Stack<>();
    private ViewPager viewPager;
    Toolbar toolbar;
    RelativeLayout relativemain;
    TextView toolbarNEXT;
    public static TextView toolbarTV;
    private TabLayout tabLayout;
    private int currentTab = 0;
    public static ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("carpathy.com.assignmentct", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

        viewPager = (ViewPager) findViewById(R.id.socialVP);
        relativemain = (RelativeLayout) findViewById(R.id.relativemain);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        toolbarTV = (TextView) findViewById(R.id.toolbarTV);

        setupViewPager();

        tabLayout.setupWithViewPager(viewPager);
    }


    public void setupViewPager() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalerySelectFragment(), getResources().getString(R.string.gallery));
        adapter.addFragment(new MyFbFrag(), getResources().getString(R.string.facebook));
        adapter.addFragment(new InstagramSelectFragment(), getResources().getString(R.string.instagram));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }


    @Override
    public void onBackPressed() {
        AppController.getInstance().selectedGenericImageModelsAL.clear();
        AppController.getInstance().selectedGaleryImageModelsAL.clear();
        AppController.setTotalCountImages(0);
        super.onBackPressed();


    }


    @Override
    protected void onPause() {
        Log.i("yash", "onPause");
        super.onPause();
        currentTab = viewPager.getCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "select");
        viewPager.setCurrentItem(currentTab);
    }


    @Override
    public void fn(int sent_by) {
        EventBus.getDefault().post(new UpdateAdapters(sent_by)); //sent_by Instagram
    }

    @Override
    public void updateOtherAdapter(int sent_by) {
        EventBus.getDefault().post(new UpdateAdapters(sent_by)); //sent_by Galery
    }


    @Override
    public void sent_by_facebook(int facebook) {
        EventBus.getDefault().post(new UpdateAdapters(facebook));
    }


    @Override
    public void clearfbAdapters() {
        EventBus.getDefault().post(new ClearAdaptersOnLogout(1));
    }

    @Override
    public void clearfbAdaptersFromImage() {
        EventBus.getDefault().post(new ClearAdaptersOnLogout(2));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int i = viewPager.getCurrentItem();
        if (i == 3) {

        }
    }

    @Override
    public void clearInstaImages() {
        EventBus.getDefault().post(new ClearAdaptersOnLogout(3));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppController.getInstance().selectedGenericImageModelsAL.clear();
                AppController.getInstance().selectedGaleryImageModelsAL.clear();
                AppController.setTotalCountImages(0);
                onBackPressed();
                break;
        }
        return true;

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onSuccess(ViewFlipper viewFlipperGallery) {

        viewFlipper = viewFlipperGallery;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}


