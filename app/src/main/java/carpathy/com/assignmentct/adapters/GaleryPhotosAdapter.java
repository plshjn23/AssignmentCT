package carpathy.com.assignmentct.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import carpathy.com.assignmentct.models.GenericImageModel;
import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.utils.SharedPreferenceUtils;
import carpathy.com.assignmentct.controllers.AppController;
import carpathy.com.assignmentct.utils.Constants;
import de.greenrobot.event.EventBus;

public class GaleryPhotosAdapter extends RecyclerView.Adapter<GaleryPhotosAdapter.RecyclerViewHolder> {
    Context context;
    ArrayList<GenericImageModel> galeryGenericImageModelsAL = new ArrayList<>();
    UpdateByGalery updateByGalery;


    public GaleryPhotosAdapter(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
        updateByGalery = (UpdateByGalery) context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (context == null) {
            context = parent.getContext();
        }
        View LayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image_social, null);
        RecyclerViewHolder rcv = new RecyclerViewHolder(LayoutView);
        return rcv;
    }

    public void addDataToAdapter(ArrayList<GenericImageModel> genericImageModelsAL) {
        this.galeryGenericImageModelsAL.addAll(genericImageModelsAL);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        String imagePath = galeryGenericImageModelsAL.get(position).getSource_path();
        if (imagePath == null) {
        } else {
            Glide.with(context).load(imagePath).placeholder(R.mipmap.ic_launcher).into(holder.imageView);
        }

        if (galeryGenericImageModelsAL.get(position).getImageSelected() == false) {
            holder.checkPic.setChecked(false);
            holder.singleSocialImageCountTV.setVisibility(View.GONE);

        } else if (galeryGenericImageModelsAL.get(position).getImageSelected() == true) {
            holder.checkPic.setChecked(true);
            holder.singleSocialImageCountTV.setText(String.valueOf(galeryGenericImageModelsAL.get(position).getSelectedAtWhatNumber()));
            holder.singleSocialImageCountTV.setVisibility(View.GONE);
        }

        for (int i = 0; i < AppController.selectedGenericImageModelsAL.size(); i++) {
            if (imagePath.equalsIgnoreCase(AppController.selectedGenericImageModelsAL.get(i).getSource_path())) {
                holder.checkPic.setChecked(true);
                galeryGenericImageModelsAL.get(position).setImageSelected(true);

                break;
            } else {
                holder.checkPic.setChecked(false);


            }

        }

        holder.checkPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (galeryGenericImageModelsAL.get(position).getImageSelected() == false) {

                    SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
                    sharedPreferenceUtils.initializeSharedPreference(context, Constants.SHARED_PREFERENCE_NAME);
                    long order_id = sharedPreferenceUtils.getSharedPreferenceLongName(Constants.CURRENT_ORDER_ID);
                    galeryGenericImageModelsAL.get(position).setOrder_id(order_id);
                    galeryGenericImageModelsAL.get(position).setSelectedAtWhatNumber(AppController.getTotalCountImages());
                    galeryGenericImageModelsAL.get(position).setImageSelected(true);
                    galeryGenericImageModelsAL.get(position).setCount(1);
                    galeryGenericImageModelsAL.get(position).setIsUploaded(false);
                    long timeStamp = System.currentTimeMillis();
                    galeryGenericImageModelsAL.get(position).setSource_id(String.valueOf(timeStamp));
                    galeryGenericImageModelsAL.get(position).setSource_type(Constants.SOURCE_TYPE_FOR_GALERY_IN_OUR_SERVER);
                    AppController.getInstance().selectedGenericImageModelsAL.add(galeryGenericImageModelsAL.get(position));
                    holder.singleSocialImageCountTV.setText(String.valueOf(galeryGenericImageModelsAL.get(position).getSelectedAtWhatNumber()));
                    holder.singleSocialImageCountTV.setVisibility(View.GONE);
                    holder.checkPic.setChecked(true);
                    holder.singleSocialImageCountTV.setVisibility(View.GONE);
                } else {
                    galeryGenericImageModelsAL.get(position).setImageSelected(false);
                    galeryGenericImageModelsAL.get(position).setSelectedAtWhatNumber(0);
                    galeryGenericImageModelsAL.get(position).setCount(0);
                    String imagepathde = galeryGenericImageModelsAL.get(position).getSource_path();
                    for (int i = 0; i < AppController.getInstance().selectedGenericImageModelsAL.size(); i++) {
                        if (imagepathde.equalsIgnoreCase(AppController.selectedGenericImageModelsAL.get(i).getSource_path())) {
                            AppController.getInstance().selectedGenericImageModelsAL.remove(i);
                        }
                    }
                    holder.singleSocialImageCountTV.setVisibility(View.GONE);
                    AppController.setTotalCountImages(AppController.getTotalCountImages() - 1);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return galeryGenericImageModelsAL.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        private CheckBox checkPic;
        public TextView singleSocialImageCountTV;
        RelativeLayout singleSocialImageSocialLL;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            checkPic = (CheckBox) itemView.findViewById(R.id.socialSingleCheckCB);
            imageView = (ImageView) itemView.findViewById(R.id.socialSinglePicIV);
            singleSocialImageCountTV = (TextView) itemView.findViewById(R.id.singleSocialImageCountTV);
            singleSocialImageSocialLL = (RelativeLayout) itemView.findViewById(R.id.singleSocialImageSocialLL);
        }
    }

    public void onEvent(int sent_by) {
        if (sent_by != Constants.SOURCE_TYPE_FOR_GALERY_IN_OUR_SERVER) {
            notifyDataSetChanged();
        }
    }


    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();

    }

    public interface UpdateByGalery {
        public void updateOtherAdapter(int sent_by);

    }
}
