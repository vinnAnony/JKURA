package com.android.jkura.extras;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AspirantSelectionAdapter extends RecyclerView.Adapter<AspirantSelectionAdapter.RecyclerViewHolder> {
    @NonNull
    @Override
    public AspirantSelectionAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AspirantSelectionAdapter.RecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
