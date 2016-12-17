package carpathy.com.assignmentct.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;


import java.io.File;
import java.util.ArrayList;


import carpathy.com.assignmentct.interfaces.CommunicationInterface;
import carpathy.com.assignmentct.utils.Constants;
import carpathy.com.assignmentct.adapters.GaleryPhotosAdapter;
import carpathy.com.assignmentct.adapters.GalleryAlbumAdapter;
import carpathy.com.assignmentct.models.GalleryPhotoAlbum;
import carpathy.com.assignmentct.models.GenericImageModel;
import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.activities.MainActivity;
import de.greenrobot.event.EventBus;


public class GalerySelectFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    RecyclerView recyclerView;
    String contextName = "GalerySelectFragment : ";
    RelativeLayout mainContentRL;
    final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
    final String orderBy = MediaStore.Images.Media._ID;

    Button proceedBTN;
    Cursor imageCursor;
    ArrayList<GenericImageModel> genericGaleryImageModelsAL = new ArrayList<>();
    static int start = 0;
    static int end = 0;
    View LayoutView;
    int image_column_index;
    GridLayoutManager gridLayoutManager;
    private int totalCount;
    private GaleryPhotosAdapter galeryPhotosAdapter;
    int visibleItemCount, totalItemCount;
    ArrayList<GalleryPhotoAlbum> arrayListAlbums;
    ListView lvPhotoAlbum;
    private GalleryAlbumAdapter galleryAlbumAdapter;
    private ViewFlipper viewFlipperGallery;
    private boolean isImage = true;
    String bucketname = null;
    CommunicationInterface callback;
    Button backBtn;
    private static final int REQUEST_CODE = 1234;
    private boolean isFirstClick = true;


    public GalerySelectFragment() {

    }

    public static GalerySelectFragment newInstance(String param1, String param2) {
        GalerySelectFragment fragment = new GalerySelectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arrayListAlbums = new ArrayList<GalleryPhotoAlbum>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_galery, container, false);
        LayoutView = view;
        lvPhotoAlbum = (ListView) view.findViewById(R.id.fragment_create_gallery_listview);
        backBtn = (Button) view.findViewById(R.id.backBtn);
        lvPhotoAlbum.setOnItemClickListener(this);
        viewFlipperGallery = (ViewFlipper) view.findViewById(R.id.fragment_create_gallery_flipper);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstClick) {
                    //do nothing
                } else {
                    if (MainActivity.viewFlipper.getDisplayedChild() == 1) {
                        MainActivity.viewFlipper.setDisplayedChild(0);
                    } else if (MainActivity.viewFlipper.getDisplayedChild() == 0) {
                        MainActivity.viewFlipper.setDisplayedChild(1);
                    }
                }

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView = (RecyclerView) getView().findViewById(R.id.FragmentGaleryRV);
        mainContentRL = (RelativeLayout) getView().findViewById(R.id.mainContentRL);
        viewFlipperGallery.setDisplayedChild(0);
        isImage = true;


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();

                    totalItemCount = gridLayoutManager.getItemCount();

                    int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                    if (totalItemCount == firstVisibleItem + visibleItemCount) {

                        start = end;
                        int count_remaing = start - 100;
                        if (count_remaing < 100) {
                            end = 0;
                        } else {
                            end = start - 100;
                        }
                        getImagesFromGalaery(start, end);
                    }
                }
            }

        });
    }

    public void getImagesFromGalaery(int start, int end) {

        genericGaleryImageModelsAL.clear();
        for (int i = start - 1; i >= end; i--) {
            imageCursor.moveToPosition(i);
            int id = imageCursor.getInt(image_column_index);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageCursor.getString(dataColumnIndex), options);
            GenericImageModel genericImageModel = new GenericImageModel();
            genericImageModel.setSource_path(imageCursor.getString(dataColumnIndex));
            genericImageModel.setSelectedAtWhatNumber(0);
            genericImageModel.setSource_id(String.valueOf(id));
            genericImageModel.setWhereIsSaved(Constants.SAVED_LOCALLY);
            genericImageModel.setUpdateHapperned(false);
            genericImageModel.setIsUploaded(false);
            genericImageModel.setOrder_id(0l);
            genericImageModel.setImageSelected(false);
            genericImageModel.setSource_type(Constants.SOURCE_TYPE_FOR_GALERY_IN_OUR_SERVER);
            File file = new File(imageCursor.getString(dataColumnIndex));
            long fileLength = file.length();
            genericImageModel.setImage_size(fileLength / 1024); // in KB
            if (file != null)
                file = null;
            genericGaleryImageModelsAL.add(genericImageModel);
        }

        galeryPhotosAdapter.addDataToAdapter(genericGaleryImageModelsAL);
        galeryPhotosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    public void onEvent(Integer getGalleryAlbumsOnBackPress) {

        viewFlipperGallery.setDisplayedChild(0);
        isImage = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    private void getPhotoList() {

        // which image properties are we querying
        String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA};

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(datetaken) ASC";

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = getActivity().getContentResolver().query(images, PROJECTION_BUCKET,
                BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

        Log.v("ListingImages", " query count=" + cur.getCount());

        GalleryPhotoAlbum album;

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            String data;
            long bucketId;

            int bucketColumn = cur
                    .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur
                    .getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);

            int bucketIdColumn = cur
                    .getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

            do {
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                data = cur.getString(dataColumn);
                bucketId = cur.getInt(bucketIdColumn);

                if (bucket != null && bucket.length() > 0) {
                    album = new GalleryPhotoAlbum();
                    album.setBucketId(bucketId);
                    album.setBucketName(bucket);
                    album.setDateTaken(date);
                    String searchParams = null;
                    String bucketTemp = bucket;
                    searchParams = "bucket_display_name = \"" + bucket + "\"";

                    imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, searchParams, null, orderBy + " ASC");
                    imageCursor.moveToFirst();
                    image_column_index = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                    int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    try {
                        if (imageCursor.getCount() > 0) {
                            album.setFirstImage(imageCursor.getString(dataColumnIndex));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    this.totalCount = imageCursor.getCount(); //Total Count of images in andrid
                    album.setData(data);
                    album.setTotalCount(photoCountByAlbum(bucket));
                    arrayListAlbums.add(album);
                    // Do something with the values.
                    Log.v("ListingImages", " bucket=" + bucket
                            + "  date_taken=" + date + "  _data=" + data
                            + " bucket_id=" + bucketId);
                }

            } while (cur.moveToNext());
        }
        cur.close();
        setData();

    }

    private int photoCountByAlbum(String bucketName) {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = null;
            String bucket = bucketName;
            searchParams = "bucket_display_name = \"" + bucket + "\"";

            Cursor mPhotoCursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    searchParams, null, orderBy + " ASC");

            if (mPhotoCursor.getCount() > 0) {
                return mPhotoCursor.getCount();
            }
            mPhotoCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;

    }

    private void setData() {
        if (arrayListAlbums.size() > 0) {

            galleryAlbumAdapter = new GalleryAlbumAdapter(getActivity(), arrayListAlbums);
            lvPhotoAlbum.setAdapter(galleryAlbumAdapter);
            galleryAlbumAdapter.notifyDataSetChanged();


            if (galleryAlbumAdapter == null) {

            } else {

            }

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        askGalleryPermission();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (viewFlipperGallery.getDisplayedChild() == 0 && isImage) {
            isFirstClick = false;
            genericGaleryImageModelsAL.clear();
            bucketname = arrayListAlbums.get(position).getBucketName();
            recyclerView.setHasFixedSize(true);
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            galeryPhotosAdapter = new GaleryPhotosAdapter(getActivity());
            recyclerView.setAdapter(galeryPhotosAdapter);
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = null;
            String bucket = bucketname;
            searchParams = "bucket_display_name = \"" + bucket + "\"";

            imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, searchParams, null, orderBy + " ASC");
            image_column_index = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);

            this.totalCount = imageCursor.getCount(); //Total Count of images in andrid

            start = totalCount;
            if (start < 100) {
                end = 0;
            } else {
                end = start - 100;
            }
            getImagesFromGalaery(start, end);
            if (viewFlipperGallery.getDisplayedChild() > 0) {

                viewFlipperGallery.setDisplayedChild(0);
            }
            viewFlipperGallery.showNext();

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        callback = (CommunicationInterface) getActivity();
        callback.onSuccess(viewFlipperGallery);

    }

    public void askGalleryPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // perform your work
            getPhotoList();
        } else {
            // request for permission
            boolean isDeniedPreviously = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (isDeniedPreviously) {
                // show an explanation as to why you need this permission and again request for permission
                // if don't ask again box is checked, and we have again asked for permission it will directly call
                // onRequestPermissionResult with Permission_DENIED result
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE); // shows the dialog
            } else {
                // request for permission

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE); // shows the dialog

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            // if request is cancelled grantResults array is empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getPhotoList();
                // perform your work as permission is granted
            } else {
                Toast.makeText(getActivity(), "Permission Denied. Go to settings to change app permission", Toast.LENGTH_SHORT).show();
                // permission is denied so disable any functionality the app will use because of this permission
                //your app might show a dialog or snackbar explaining why it could not perform the user's requested action that needs that permission.
            }
        }
    }
}
