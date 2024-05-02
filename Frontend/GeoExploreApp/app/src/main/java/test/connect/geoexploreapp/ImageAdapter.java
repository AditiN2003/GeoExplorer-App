package test.connect.geoexploreapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import test.connect.geoexploreapp.R;
import test.connect.geoexploreapp.model.Image;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Image> images;

    public ImageAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = images.get(position);
        if (image != null) {
            holder.imageID.setText("File Path: " + image.getId());

            holder.textViewImageFile.setText("File Path: " + image.getFilePath());
            if (image.getObservation() != null) {
                holder.textViewObservationTitle.setText("Image for Observation Title: " + image.getObservation().getTitle());
            } else {
                holder.textViewObservationTitle.setText("Profile Image");
            }
        }
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView imageID, textViewImageFile, textViewObservationTitle;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageID = itemView.findViewById(R.id.imageID);
            textViewImageFile = itemView.findViewById(R.id.textViewImageFile);
            textViewObservationTitle = itemView.findViewById(R.id.textViewObservationTitle);

        }
    }
}