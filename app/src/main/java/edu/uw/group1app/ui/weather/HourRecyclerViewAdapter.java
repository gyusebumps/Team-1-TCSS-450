package edu.uw.group1app.ui.weather;

import android.graphics.drawable.Icon;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import edu.uw.group1app.R;
import edu.uw.group1app.databinding.FragmentHourPostBinding;

public class HourRecyclerViewAdapter extends RecyclerView.Adapter<HourRecyclerViewAdapter.HourViewHolder> {

    private final List<HourPost> mHours;

    public HourRecyclerViewAdapter(List<HourPost> items){
        this.mHours = items;
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HourViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_hour_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        holder.setHour(mHours.get(position));
    }

    @Override
    public int getItemCount() {
        return mHours.size();
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentHourPostBinding binding;
        private HourPost mHour;

        public HourViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentHourPostBinding.bind(view);

        }

        void setHour(final HourPost hour){
            mHour = hour;
            binding.textViewHour.setText(hour.getHour());
            binding.textViewTemp.setText(hour.getTemp());
            binding.textViewCondition.setText(hour.getCondition());

        }
    }

}
