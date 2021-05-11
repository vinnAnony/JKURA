package com.android.jkura.extras;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jkura.R;
import com.android.jkura.VoteSubmissionActivity;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class AspirantSelectionAdapter extends RecyclerView.Adapter<AspirantSelectionAdapter.RecyclerViewHolder> {
    private Context mContext;
    private List<AspirantModel> aspirants;
    private String aspirantPosition,aspirantDepartment,aspirantSchool;

    public AspirantSelectionAdapter(Context mContext, List<AspirantModel> aspirants, String aspirantPosition, String aspirantDepartment, String aspirantSchool) {
        this.mContext = mContext;
        this.aspirants = aspirants;
        this.aspirantPosition = aspirantPosition;
        this.aspirantDepartment = aspirantDepartment;
        this.aspirantSchool = aspirantSchool;
    }

    @NonNull
    @Override
    public AspirantSelectionAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.aspirant_card, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AspirantSelectionAdapter.RecyclerViewHolder holder, int position) {
        final AspirantModel currentAspirant = aspirants.get(position);
        holder.aspirantName.setText(currentAspirant.getAspirantName());
        holder.aspirantCourse.setText(currentAspirant.getAspirantCourse());
        Picasso.with(mContext)
                .load(currentAspirant.getAspirantImageURL())
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(holder.aspirantImage);

        holder.aspirantCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] aspData = {currentAspirant.getAspirantName(),currentAspirant.getAspirantCourse(), currentAspirant.getAspirantImageURL(),
                        currentAspirant.getAspirantRegNo(),aspirantPosition,aspirantDepartment,aspirantSchool};
                openDetailActivity(aspData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aspirants.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView aspirantName,aspirantCourse;
        public ImageView aspirantImage;
        MaterialCardView aspirantCard;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            aspirantName =itemView.findViewById ( R.id.aspirantCardName );
            aspirantCourse = itemView.findViewById(R.id.aspirantCardCourse);
            aspirantImage = itemView.findViewById(R.id.aspirantCardImage);
            aspirantCard = itemView.findViewById(R.id.aspirantCard);

        }
    }

    private void openDetailActivity(String[] data){
        Intent intent = new Intent(mContext, VoteSubmissionActivity.class);
        intent.putExtra("NAME_KEY",data[0]);
        intent.putExtra("COURSE_KEY",data[1]);
        intent.putExtra("IMAGE_KEY",data[2]);
        intent.putExtra("REG_NO_KEY",data[3]);
        intent.putExtra("POSITION_KEY",data[4]);
        intent.putExtra("DEPARTMENT_KEY",data[5]);
        intent.putExtra("SCHOOL_KEY",data[6]);
        mContext.startActivity(intent);
    }

}
