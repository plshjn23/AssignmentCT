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


import carpathy.com.assignmentct.R;
import carpathy.com.assignmentct.utils.SharedPreferenceUtils;
import carpathy.com.assignmentct.controllers.AppController;
import carpathy.com.assignmentct.models.GenericImageModel;
import carpathy.com.assignmentct.utils.Constants;

public class InstagramPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<GenericImageModel> genericInstagramModels;
    Update update;
    MyInstagramLogoutInterface myInstagramLogoutInterface;
    public static int VIEW_TYPE_CELL = 2;


    public InstagramPhotosAdapter(Context context, ArrayList<GenericImageModel> GenericInstagramModels) {
        this.context = context;
        this.genericInstagramModels = GenericInstagramModels;
        myInstagramLogoutInterface = (MyInstagramLogoutInterface) context;
        update = (Update) context;
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int getItemViewType(int position) {
        int size = genericInstagramModels.size();
        return VIEW_TYPE_CELL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.single_image_social, parent, false);
        RecyclerViewHolder rcv = new RecyclerViewHolder(view);
        return rcv;


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder myHolder = (RecyclerViewHolder) holder;
            Glide.with(context).load(genericInstagramModels.get(position).getSource_path()).placeholder(R.mipmap.ic_launcher).dontAnimate().into(myHolder.singleSocialPic);
            int size = AppController.getInstance().selectedGenericImageModelsAL.size();
            String image_url = genericInstagramModels.get(position).getSource_path();
            if (genericInstagramModels.get(position).getImageSelected()) {
                myHolder.checkPic.setChecked(true);
                myHolder.singleSocialImageCountTV.setVisibility(View.GONE);
                myHolder.singleSocialImageCountTV.setText(String.valueOf(genericInstagramModels.get(position).getSelectedAtWhatNumber()));
            } else {
                myHolder.checkPic.setChecked(false);
                myHolder.singleSocialImageCountTV.setVisibility(View.GONE);
            }


            for (int i = 0; i < AppController.selectedGenericImageModelsAL.size(); i++) {
                if (image_url.equalsIgnoreCase(AppController.selectedGenericImageModelsAL.get(i).getSource_path())) {
                    myHolder.checkPic.setChecked(true);
                    genericInstagramModels.get(position).setImageSelected(true);
                    break;
                } else {
                    myHolder.checkPic.setChecked(false);

                }

            }


            myHolder.checkPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (genericInstagramModels.get(position).getImageSelected() == false) {

                        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils();
                        sharedPreferenceUtils.initializeSharedPreference(context, Constants.SHARED_PREFERENCE_NAME);
                        long order_id = sharedPreferenceUtils.getSharedPreferenceLongName(Constants.CURRENT_ORDER_ID);
                        genericInstagramModels.get(position).setOrder_id(order_id);
                        genericInstagramModels.get(position).setSelectedAtWhatNumber(AppController.getInstance().getTotalCountImages());
                        genericInstagramModels.get(position).setImageSelected(true);
                        genericInstagramModels.get(position).setCount(1);
                        genericInstagramModels.get(position).setUpdateHapperned(false);
                        long timeStamp = System.currentTimeMillis();
                        genericInstagramModels.get(position).setSource_id(String.valueOf(timeStamp));
                        genericInstagramModels.get(position).setWhereIsSaved(Constants.SAVED_LOCALLY);
                        AppController.getInstance().selectedGenericImageModelsAL.add(genericInstagramModels.get(position));
                        myHolder.singleSocialImageCountTV.setText(String.valueOf(genericInstagramModels.get(position).getSelectedAtWhatNumber()));
                        myHolder.singleSocialImageCountTV.setVisibility(View.GONE);


                    } else {
                        genericInstagramModels.get(position).setImageSelected(false);
                        genericInstagramModels.get(position).setSelectedAtWhatNumber(0);
                        String imagepathde = genericInstagramModels.get(position).getSource_path();

                        for (int i = 0; i < AppController.getInstance().selectedGenericImageModelsAL.size(); i++) {
                            if (imagepathde.equalsIgnoreCase(AppController.selectedGenericImageModelsAL.get(i).getSource_path())) {

                                AppController.getInstance().selectedGenericImageModelsAL.remove(i);
                            }


                        }
                        myHolder.singleSocialImageCountTV.setVisibility(View.GONE);
                        genericInstagramModels.get(position).setImageSelected(false);
                        AppController.getInstance().setTotalCountImages(AppController.getInstance().getTotalCountImages() - 1);
                    }
                }
            });

        }


    }

    public interface MyInstagramLogoutInterface {
        void clearInstaImages();
    }


    @Override
    public int getItemCount() {
        int size = genericInstagramModels.size();
        return genericInstagramModels.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkPic;
        public ImageView singleSocialPic;


        public RelativeLayout mainContentLL;
        public TextView singleSocialImageCountTV;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            checkPic = (CheckBox) itemView.findViewById(R.id.socialSingleCheckCB);
            singleSocialPic = (ImageView) itemView.findViewById(R.id.socialSinglePicIV);
            singleSocialImageCountTV = (TextView) itemView.findViewById(R.id.singleSocialImageCountTV);
            mainContentLL = (RelativeLayout) itemView.findViewById(R.id.singleImageSocialParent);
        }
    }


    public interface Update {
        public void fn(int sent_by);
    }


}