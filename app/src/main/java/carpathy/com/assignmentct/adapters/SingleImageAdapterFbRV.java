package carpathy.com.assignmentct.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import carpathy.com.assignmentct.controllers.AppController;
import carpathy.com.assignmentct.models.GenericImageModel;
import carpathy.com.assignmentct.utils.Constants;


public class SingleImageAdapterFbRV extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    public static int VIEW_TYPE_FOOTER = 1;
    public static int VIEW_TYPE_CELL = 2;
    public static int VIEW_TYPE_EMPTY = 3;
    int added_by = 0, temp = 0;
    MyFbLogoutInterfaceImage myFbLogoutInterface;
    UpdateByFacebookInterface updateByFacebookInterface;
    RelativeLayout loginRl, fbFragContentRl;
    public static ArrayList<GenericImageModel> genericFbImageModelAL = new ArrayList<>();

    public SingleImageAdapterFbRV(Context context, ArrayList<GenericImageModel> singleImageFbModelsAL, RelativeLayout loginRL, RelativeLayout fbFragContentRL) {
        this.context = context;
        genericFbImageModelAL = singleImageFbModelsAL;
        updateByFacebookInterface = (UpdateByFacebookInterface) context;
        myFbLogoutInterface = (MyFbLogoutInterfaceImage) context;
        this.loginRl = loginRL;
        this.fbFragContentRl = fbFragContentRL;
    }


    public void addToAdapter(ArrayList<GenericImageModel> genericFbImageModelAL) {
        this.genericFbImageModelAL.addAll(genericFbImageModelAL);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_image_social, parent, false);
            MyViewHolder rcv = new MyViewHolder(view);
            return rcv;
        } else if (viewType == VIEW_TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fb_login_logout_view, parent, false);
            FooterViewHolder fvh = new FooterViewHolder(view);
            return fvh;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
            EmptyViewHolder evh = new EmptyViewHolder(view);
            return evh;
        }

    }

    @Override
    public int getItemViewType(int position) {
        int size = genericFbImageModelAL.size();
        if (position < genericFbImageModelAL.size()) {
            return VIEW_TYPE_CELL; // this is when it is normal picture
        } else if (position == genericFbImageModelAL.size() + added_by) {
            return VIEW_TYPE_FOOTER; // this happens at the end, as we need to add logout in middle
        } else //
        {
            return VIEW_TYPE_EMPTY; // this empty view added between logout and previos images
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            String image_url = genericFbImageModelAL.get(position).getSource_path();
            if (genericFbImageModelAL.get(position).getImageSelected()) {
                myViewHolder.selectCheckBox.setChecked(true);
                myViewHolder.textView.setVisibility(View.GONE);
                myViewHolder.textView.setText(String.valueOf(genericFbImageModelAL.get(position).getSelectedAtWhatNumber()));

            } else {
                myViewHolder.selectCheckBox.setChecked(false);
                myViewHolder.textView.setVisibility(View.GONE);
            }


            for (int i = 0; i < AppController.selectedGenericImageModelsAL.size(); i++) {
                if (image_url.equalsIgnoreCase(AppController.selectedGenericImageModelsAL.get(i).getSource_path())) {
                    myViewHolder.selectCheckBox.setChecked(true);
                    genericFbImageModelAL.get(position).setImageSelected(true);

                    break;
                } else {
                    myViewHolder.selectCheckBox.setChecked(false);


                }

            }


            myViewHolder.selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (genericFbImageModelAL.get(position).getImageSelected() == false) {

                        genericFbImageModelAL.get(position).setIsUploaded(false);
                        genericFbImageModelAL.get(position).setWhereIsSaved(Constants.SAVED_LOCALLY);
                        genericFbImageModelAL.get(position).setSource_type(Constants.SOURCE_TYPE_FOR_FACEBOOK_IN_OUR_SERVER);
                        genericFbImageModelAL.get(position).setSelectedAtWhatNumber(AppController.getTotalCountImages());

                        long timeStamp = System.currentTimeMillis();

                        genericFbImageModelAL.get(position).setSource_id(String.valueOf(timeStamp));
                        genericFbImageModelAL.get(position).setImageSelected(true);
                        genericFbImageModelAL.get(position).setCount(1);

                        AppController.getInstance().selectedGenericImageModelsAL.add(genericFbImageModelAL.get(position));
                        myViewHolder.textView.setText(String.valueOf(genericFbImageModelAL.get(position).getSelectedAtWhatNumber()));

                        myViewHolder.textView.setVisibility(View.GONE);


                    } else {
                        genericFbImageModelAL.get(position).setImageSelected(false);
                        genericFbImageModelAL.get(position).setSelectedAtWhatNumber(0);
                        genericFbImageModelAL.get(position).setOrder_id(0l);
                        String imagepathde = genericFbImageModelAL.get(position).getSource_path();

                        for (int i = 0; i < AppController.getInstance().selectedGenericImageModelsAL.size(); i++) {
                            if (imagepathde.equalsIgnoreCase(AppController.selectedGenericImageModelsAL.get(i).getSource_path())) {

                                AppController.getInstance().selectedGenericImageModelsAL.remove(i);
                            }


                        }


                        AppController.getInstance().selectedGenericImageModelsAL.remove(genericFbImageModelAL.get(position));
                        AppController.getInstance().setTotalCountImages(AppController.getInstance().getTotalCountImages() - 1);
                        myViewHolder.textView.setVisibility(View.GONE);

                    }
                }
            });
            Glide.with(context).load(genericFbImageModelAL.get(position).getSource_path()).placeholder(R.mipmap.ic_launcher).into(myViewHolder.imageView);
        } else if (holder instanceof FooterViewHolder) {
            Log.e("SingleImageFbAdapter : ", "footer");
        } else {
            Log.e("SingleImageFbAdapter : ", "empty");
        }

    }

    public void clearAllData() {
        if (genericFbImageModelAL == null) {

        } else {
            genericFbImageModelAL.clear();
        }

    }

    public interface MyFbLogoutInterfaceImage {
        public void clearfbAdaptersFromImage();
    }

    public interface UpdateByFacebookInterface {
        void sent_by_facebook(int facebook);
    }


    @Override
    public int getItemCount() {
        int size = genericFbImageModelAL.size();
        int remainder = size % 3;
        if (remainder == 1) {
            added_by = 3;
        } else if (remainder == 2) {
            added_by = 2;
        } else if (remainder == 0) {
            added_by = 1;
        }
        temp = added_by;
        return genericFbImageModelAL.size() + added_by + 1;

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CheckBox selectCheckBox;
        public TextView textView;
        public RelativeLayout singleImageSocialParent;


        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.socialSinglePicIV);
            selectCheckBox = (CheckBox) itemView.findViewById(R.id.socialSingleCheckCB);
            textView = (TextView) itemView.findViewById(R.id.singleSocialImageCountTV);
            singleImageSocialParent = (RelativeLayout) itemView.findViewById(R.id.singleImageSocialParent);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}