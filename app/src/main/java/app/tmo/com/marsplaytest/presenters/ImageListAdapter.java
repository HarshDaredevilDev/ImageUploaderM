package app.tmo.com.marsplaytest.presenters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import app.tmo.com.marsplaytest.R;
import app.tmo.com.marsplaytest.models.ImageListModel;
import app.tmo.com.marsplaytest.views.MainActivity;


public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList<ImageListModel> imageList;
    Context context;
    String str;
    Bundle bundle = null;
    String from;
    SetZoomableView setZoomableView;

    int finalHeight, finalWidth;


    public ImageListAdapter(ArrayList<ImageListModel> imageList, Context context, String from) {

        this.imageList = imageList;
        this.context = context;
        this.from=from;
        this.setZoomableView=(MainActivity)context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_view_card, parent, false);
            context = parent.getContext();
            return new ImageViewHolder(view);

        }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position)
    {

        setZoomableView.setUpZoomView(((ImageViewHolder)holder).image);

        Glide.with(context).load(AppConfig.BASE_URL+ imageList.get(position).getImageUrl()).into(((ImageViewHolder)holder).image);


    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    class ImageViewHolder extends RecyclerView.ViewHolder  {

        public CardView cardView;
        public ImageView image;




        public ImageViewHolder(View view) {
            super(view);

            cardView=(CardView)view.findViewById(R.id.card_view);
            image=(ImageView)view.findViewById(R.id.image_view);

        }

    }


  public interface SetZoomableView{

        public void setUpZoomView(ImageView cardView);

  }


}

