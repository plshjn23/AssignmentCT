package carpathy.com.assignmentct.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import carpathy.com.assignmentct.models.GalleryPhotoAlbum;
import carpathy.com.assignmentct.R;

public class GalleryAlbumAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<GalleryPhotoAlbum> galleryPhotoAlbumArrayList = new ArrayList<>();

    public GalleryAlbumAdapter(Context contenxt, ArrayList<GalleryPhotoAlbum> arrayListAlbums) {
        mContext = contenxt;
        galleryPhotoAlbumArrayList = arrayListAlbums;
    }

    private class ViewHolder {
        private TextView albumName, albumCount;
        private ImageView list_gallery_album_ib_right;
    }

    @Override
    public int getCount() {
        return galleryPhotoAlbumArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            convertView = vi.inflate(R.layout.list_item_gallery_album, null);
            holder.albumName = (TextView) convertView
                    .findViewById(R.id.list_gallery_album_tv_albumname);
            holder.albumCount = (TextView) convertView
                    .findViewById(R.id.list_gallery_album_tv_count);

            holder.list_gallery_album_ib_right = (ImageView) convertView.findViewById(R.id.list_gallery_album_ib_right);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GalleryPhotoAlbum galleryPhotoAlbum = galleryPhotoAlbumArrayList.get(position);
        holder.albumName.setText(galleryPhotoAlbum.getBucketName());
        holder.albumCount.setText(String.valueOf(galleryPhotoAlbum.getTotalCount()));
        if (galleryPhotoAlbum.getFirstImage() != null) {
            holder.list_gallery_album_ib_right.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(galleryPhotoAlbum.getFirstImage()).placeholder(R.mipmap.ic_launcher).into(holder.list_gallery_album_ib_right);
        } else {
            Glide.with(mContext).load("HTTP").placeholder(R.mipmap.ic_launcher).into(holder.list_gallery_album_ib_right);
        }


        convertView.setTag(holder);

        return convertView;
    }

}