package com.android.jkura.extras;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.jkura.AspirantSelectionActivity;
import com.android.jkura.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ActiveSessionsAdapter extends RecyclerView.Adapter<ActiveSessionsAdapter.viewHolder> {

    List<ActiveSession> mActiveSessions;
    private Context mContext;
    View vv;
    SessionManager sessionManager;
    String CurrentStudentRegNo ;
    private DatabaseReference mVoterRef;

    public ActiveSessionsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_session_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        final ActiveSession session = mActiveSessions.get(position);
        vv = holder.itemView;
        sessionManager = new SessionManager(mContext);
        CurrentStudentRegNo = sessionManager.getRegNo();
        mVoterRef = FirebaseDatabase.getInstance().getReference("Students/"+CurrentStudentRegNo);

        holder.sessionName.setText(session.getPosition());
        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyVotedStatus(session.getPosition(),session.getDepartment(),session.getSchool());
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
    }

    public void setData(List<ActiveSession> sessions){
        mActiveSessions = sessions;
        notifyDataSetChanged();
    }

    private void verifyVotedStatus(final String aspPosition, final String aspDepartment, final String aspSchool){
        String voteType = "";
        if (aspPosition.equals("Delegate"))
            voteType = "dlgtVoted";
        else if (aspPosition.equals("School Representative"))
            voteType = "schRepVoted";

        final String finalVoteType = voteType;
        mVoterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int voteStatus = dataSnapshot.child(finalVoteType).getValue(Integer.class);
                Log.e("Vote Status", "Value is: " + voteStatus);
                if (voteStatus == 0) {
                    Intent intent = new Intent(mContext,AspirantSelectionActivity.class);
                    intent.putExtra("POSITION_KEY",aspPosition);
                    intent.putExtra("DEPARTMENT_KEY", aspDepartment);
                    intent.putExtra("SCHOOL_KEY",aspSchool);
                    mContext.startActivity(intent);
                }
                else if(voteStatus == 1){
                    populateVotedAlert();
                    //Toast.makeText(mContext, "Already voted!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void populateVotedAlert() {
        ViewGroup viewGroup = vv.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.voted_alert, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(false);
        final AlertDialog confirmLogoutDialog = builder.create();
        confirmLogoutDialog.show();

        Button okButton = dialogView.findViewById(R.id.popOkBtn);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogoutDialog.dismiss();
            }
        });
    }
}