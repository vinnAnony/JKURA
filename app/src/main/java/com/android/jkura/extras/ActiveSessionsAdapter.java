package com.android.jkura.extras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.jkura.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ActiveSessionsAdapter extends RecyclerView.Adapter<ActiveSessionsAdapter.viewHolder> {

    List<ActiveSession> mActiveSessions;

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_session_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        final ActiveSession session = mActiveSessions.get(position);
        holder.sessionName.setText(session.getPosition());
        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.clicked();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActiveSessions.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView sessionName;
        private final MaterialButton actionButton;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            sessionName = itemView.findViewById(R.id.sessionName);
            actionButton= itemView.findViewById(R.id.sessionActionBtn);
            
        }

        public void clicked() {

        }
    }

    public void setData(List<ActiveSession> sessions){
        mActiveSessions = sessions;
        notifyDataSetChanged();
    }
}