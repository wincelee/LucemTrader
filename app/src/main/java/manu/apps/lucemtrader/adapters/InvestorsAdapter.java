package manu.apps.lucemtrader.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.Investor;


public class InvestorsAdapter extends RecyclerView.Adapter<InvestorsAdapter.ViewHolder> {

    Context context;
    List<Investor> investorList;
    List<Investor> investorListFull;
    OnClick onClick;


    public interface OnClick {
        void onEvent(Investor investor, int pos, String clickType);
    }

    public InvestorsAdapter(Context context, List<Investor> investorList, OnClick onClick) {

        this.context = context;
        this.investorList = investorList;

        this.onClick = onClick;

        investorListFull = new ArrayList<>(investorList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.investors_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Investor currentInvestor = investorList.get(position);

        int investorsPosition = position + 1;

        if (investorsPosition < 10){

            holder.tvInvestorNo.setText(String.format("%s%s", "#0", investorsPosition));

        }else{

            holder.tvInvestorNo.setText(String.format("%s%s", "#", investorsPosition));

        }

        holder.tvInvestorUsername.setText(currentInvestor.getUsername());

        holder.tvCreatedAt.setText(String.format("%s %s", context.getString(R.string.since),
                new Config().dateFormatter(currentInvestor.getDateJoined(), "investorsFormat")));

//        holder.civInvestorAvatar.setImageResource(currentInvestor.getInvestorAvatar());

//        Glide.with(context)
//                .load(currentInvestor.getAvatarUrl())
//                .centerCrop()
//                .error(R.drawable.ic_image_not_found)
//                .transition(DrawableTransitionOptions.withCrossFade(1000))
//                .into(holder.civInvestorAvatar);

        Config.setImageWithGlide(context, holder.civInvestorAvatar, currentInvestor.getAvatarUrl());

        holder.btnEdit.setOnClickListener(v -> onClick.onEvent(currentInvestor, position, "editClick"));

        holder.mcvInvestor.setOnClickListener(v -> onClick.onEvent(currentInvestor, position, "normalClick"));

    }

    @Override
    public int getItemCount() {
        return investorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvInvestorNo, tvInvestorUsername, tvCreatedAt;
        MaterialCardView mcvInvestor;
        CircleImageView civInvestorAvatar;
        MaterialButton btnEdit;


        public ViewHolder(View itemView) {
            super(itemView);

            tvInvestorNo = itemView.findViewById(R.id.tv_investor_no);
            tvInvestorUsername = itemView.findViewById(R.id.tv_investor_username);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            mcvInvestor = itemView.findViewById(R.id.mcv_investor);
            civInvestorAvatar = itemView.findViewById(R.id.civ_investor_avatar);

        }
    }

    public Filter getInvestorFilter() {
        return investorFilter;
    }

    private final Filter investorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Investor> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(investorListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Investor investor : investorListFull) {

                    if (investor.getUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(investor);
                    }

                    if (investor.getDateJoined().toLowerCase().contains(filterPattern)) {
                        filteredList.add(investor);
                    }

                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }


        @Override
        @SuppressWarnings("unchecked")
        @SuppressLint("NotifyDataSetChanged")
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            investorList.clear();
            investorList.addAll((List<Investor>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
