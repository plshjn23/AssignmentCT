package carpathy.com.assignmentct.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.models.AlbumPictureModel;
import carpathy.com.assignmentct.models.FacebookAlbumIdMsg;
import de.greenrobot.event.EventBus;

public class FacebookAlbumsAdaptersRV extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    public static ArrayList<AlbumPictureModel> albumPictureModelAL = new ArrayList<>();
    MyFbLogoutInterface myFbLogoutInterface;
    String url;
    RelativeLayout loginRl, fbFragContentRL;
    public static int VIEW_TYPE_FOOTER = 1;
    public static int VIEW_TYPE_CELL = 2;


    public FacebookAlbumsAdaptersRV(Context context, ArrayList<AlbumPictureModel> albumPictureModelAL, RelativeLayout loginRL, RelativeLayout fbFragContentRL) {
        this.context = context;
        this.albumPictureModelAL = albumPictureModelAL;
        this.loginRl = loginRL;
        this.fbFragContentRL = fbFragContentRL;
        myFbLogoutInterface = (MyFbLogoutInterface) context;
        FacebookSdk.sdkInitialize(context);
    }

    public void addToAdapter(ArrayList<AlbumPictureModel> albumPictureModels) {
        albumPictureModelAL.addAll(albumPictureModels);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == albumPictureModelAL.size() - 1) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_album_or_folder, parent, false);
            RecyclerViewHolder rcv = new RecyclerViewHolder(view);
            return rcv;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fb_login_logout_view, parent, false);
            FooterViewHolder fvh = new FooterViewHolder(view);
            return fvh;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            url = albumPictureModelAL.get(position).getUrl();
            Glide.with(context).load(url).placeholder(R.mipmap.ic_launcher).into(recyclerViewHolder.AlbumCoverPicIV);
            recyclerViewHolder.albumNameTV.setText(albumPictureModelAL.get(position).getName());
            recyclerViewHolder.grid_item_layout_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().postSticky(new FacebookAlbumIdMsg(albumPictureModelAL.get(position).getId()));
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return albumPictureModelAL.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView AlbumCoverPicIV;
        public TextView albumNameTV;
        public RelativeLayout grid_item_layout_facebook;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            AlbumCoverPicIV = (ImageView) itemView.findViewById(R.id.imageIV);
            albumNameTV = (TextView) itemView.findViewById(R.id.folderOrAlbumName);
            grid_item_layout_facebook = (RelativeLayout) itemView.findViewById(R.id.grid_item_layout_facebook);

        }


    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface MyFbLogoutInterface {
        public void clearfbAdapters();
    }

    public void clearAllAlbumData() {
        if (albumPictureModelAL != null) {
            albumPictureModelAL.clear();
        }

    }

}