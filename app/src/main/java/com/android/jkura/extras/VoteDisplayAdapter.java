package com.android.jkura.extras;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.jkura.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteDisplayAdapter extends RecyclerView.Adapter<VoteDisplayAdapter.ViewHolder> {

    private static final String TAG = "VoteAdapter";
    private List<HashMap<String, Integer>> voteTallyList = new ArrayList<>();
    private final Context mContext;
    private final RegNameManager regNameManager;

    public VoteDisplayAdapter(Context context){
        mContext = context;
        regNameManager = new RegNameManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pie_chart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        initializeTallyAnalysis(voteTallyList.get(position), holder);

    }

    @Override
    public int getItemCount() {
        return voteTallyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AnimatedPieView animatedPieView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           animatedPieView  = itemView.findViewById(R.id.pie_chart);
        }
    }


    private void initializeTallyAnalysis(HashMap<String, Integer> voteTallies, ViewHolder holder) {

        Log.d(TAG, "initializeTallyAnalysis: VoteTally " + voteTallies);

        if (voteTallies.size() > 2){
            List<String> regNoByPosition = new ArrayList<>();
            int othersTotal = 0;
            HashMap<String, Integer> tempList = new HashMap<>(voteTallies);
            String positionOne = getHighest(tempList);

            if (positionOne != null){
                regNoByPosition.set(0, positionOne);
                tempList.remove(positionOne);
                String positionTwo =  getHighest(voteTallies);


                if (positionTwo != null){
                    regNoByPosition.set(1, positionTwo);
                    tempList.remove(positionTwo);
                }

                for (String reg: regNoByPosition){
                    if (regNameManager.getRegName(reg) == null){
                        fetchRegNameDetails(reg);
                    }
                }

                othersTotal = getTotalVotes(tempList);

                String positionOneReg = ((regNameManager.getRegName(regNoByPosition.get(0)) == null) ? regNoByPosition.get(0) : regNameManager.getRegName(regNoByPosition.get(0)));
                String positionTwoReg = ((regNameManager.getRegName(regNoByPosition.get(1)) == null) ? regNoByPosition.get(1) : regNameManager.getRegName(regNoByPosition.get(1)));

                AnimatedPieViewConfig config = new AnimatedPieViewConfig();
                config.startAngle(-90)// Starting angle offset
                        .addData(new SimplePieInfo(voteTallies.get(positionOneReg), mContext.getResources().getColor(R.color.pieBlue), positionOneReg))//Data (bean that implements the IPieInfo interface)
                        .addData(new SimplePieInfo(voteTallies.get(positionTwoReg), mContext.getResources().getColor(R.color.pieYellow), positionTwoReg))
                        .addData(new SimplePieInfo(othersTotal, mContext.getResources().getColor(R.color.pieOthers), "Others"))
                        .duration(2000)
                        .drawText(true)
                        .textSize(30);// draw pie animation duration
                holder.animatedPieView.applyConfig(config);
                holder.animatedPieView.start();

            }
        } else if (voteTallies.size() == 1){

            String positionOneReg = "User";
            for (Map.Entry tallyElement : voteTallies.entrySet()) {
                positionOneReg = tallyElement.getKey().toString();
            }
            AnimatedPieViewConfig config = new AnimatedPieViewConfig();
            config.startAngle(-90)// Starting angle offset
                    .addData(new SimplePieInfo(voteTallies.get(positionOneReg), mContext.getResources().getColor(R.color.pieBlue), positionOneReg))//Data (bean that implements the IPieInfo interface)
                    .duration(2000)
                    .drawText(true)
                    .textSize(30);// draw pie animation duration
            holder.animatedPieView.applyConfig(config);
            holder.animatedPieView.start();

        } else {

            HashMap<String, Integer> tempList = new HashMap<>(voteTallies);
            List<String> regNoByPosition = new ArrayList<>();
            regNoByPosition.add("One");
            regNoByPosition.add("Two");
            String positionOne = getHighest(tempList);

            if (positionOne != null) {
                regNoByPosition.set(0, positionOne);
                tempList.remove(positionOne);
                String positionTwo = getHighest(tempList);
                regNoByPosition.set(1, positionTwo);
            }

            Log.d(TAG, "initializeTallyAnalysis: Array "+regNoByPosition);

            String positionOneReg = ((regNameManager.getRegName(regNoByPosition.get(0)) == null) ? regNoByPosition.get(0) : regNameManager.getRegName(regNoByPosition.get(0)));
            String positionTwoReg = ((regNameManager.getRegName(regNoByPosition.get(1)) == null) ? regNoByPosition.get(1) : regNameManager.getRegName(regNoByPosition.get(1)));

            for (String reg: regNoByPosition){
                if (regNameManager.getRegName(reg) == null){
                    fetchRegNameDetails(reg);
                }
            }


            AnimatedPieViewConfig config = new AnimatedPieViewConfig();
            config.startAngle(-90)// Starting angle offset
                    .addData(new SimplePieInfo(voteTallies.get(positionOneReg), mContext.getResources().getColor(R.color.pieBlue), positionOneReg))//Data (bean that implements the IPieInfo interface)
                    .addData(new SimplePieInfo(voteTallies.get(positionTwoReg), mContext.getResources().getColor(R.color.pieYellow), positionTwoReg))//Data (bean that implements the IPieInfo interface)
                    .duration(2000)
                    .drawText(true)
                    .textSize(30);// draw pie animation duration
            holder.animatedPieView.applyConfig(config);
            holder.animatedPieView.start();
        }


    }

    private void fetchRegNameDetails(final String reg) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("Students").child(reg).child("studentName");

        Log.d(TAG, "fetchRegNameDetails: reg "+ reg);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null){
                    Log.d(TAG, "onDataChange: Name "+ snapshot);
                    String value = snapshot.getValue().toString();
                    regNameManager.setRegName(reg, value);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int getTotalVotes(HashMap<String, Integer> voteTallies) {
        int total = 0;
        for (Map.Entry tallyElement : voteTallies.entrySet()) {
            int current = Integer.parseInt(tallyElement.getValue().toString());
            total = total+current;
        }

        return total;
    }

    private String getHighest(HashMap<String, Integer> voteTallies) {
        int highest = 0;
        String highestReg = null;
        for (Map.Entry tallyElement : voteTallies.entrySet()) {
            String studentReg = tallyElement.getKey().toString();
            int aspirantVotes = Integer.parseInt(tallyElement.getValue().toString());

            if (aspirantVotes > highest){
                highest = aspirantVotes;
                highestReg = studentReg;
            }
        }
        return highestReg;
    }

    public void setData(List<HashMap<String, Integer>> voteTallyList ){
        this.voteTallyList = voteTallyList;
        notifyDataSetChanged();
    }

}
