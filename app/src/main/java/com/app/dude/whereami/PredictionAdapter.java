package com.app.dude.whereami;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.dude.whereami.remote.PredictionResults;

import java.util.List;

/**
 * Created by liron.moshkovich on 11/28/17.
 */

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.ViewHolder> {

    private List<PredictionResults.Result> predictions;
    private OnSearchedPlaceChanged callback;

    public PredictionAdapter(OnSearchedPlaceChanged callback, List<PredictionResults.Result> predictions ){
        this.callback = callback;
        this.predictions = predictions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prediction, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PredictionResults.Result prediction = predictions.get(position);
        SpannableStringBuilder builder = new SpannableStringBuilder(prediction.description);
        for(PredictionResults.Result.MatchedSubstring matchedSubstring : prediction.getMatchedSubstrings()){
            builder.setSpan(new ForegroundColorSpan(holder.prediction.getContext().getResources().getColor(R.color.colorPrimary)),
                    matchedSubstring.getOffset(), matchedSubstring.getOffset() + matchedSubstring.getLength(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.prediction.setText(builder);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onSearchedPlaceChanged(prediction.description);
            }
        });
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout container;
        public TextView prediction;

        public ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.container);
            prediction = view.findViewById(R.id.prediction_tv);
        }
    }

    public void setPredictions(List<PredictionResults.Result> predictions) {
        this.predictions = predictions;
        notifyDataSetChanged();
    }
}
