package manu.apps.lucemtrader.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import manu.apps.lucemtrader.R;
import manu.apps.lucemtrader.classes.Config;
import manu.apps.lucemtrader.classes.Crypto;


public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {

    Context context;
    List<Crypto> cryptoList;
    List<Crypto> cryptoListFull;
    String displayDateText;
    boolean profitLossFragment;
    OnClick onClick;


    public interface OnClick {
        void onEvent(Crypto crypto, int pos);
    }

    public CryptoAdapter(Context context, List<Crypto> cryptoList, String displayDateText,
                         boolean profitLossFragment, OnClick onClick) {

        this.context = context;
        this.cryptoList = cryptoList;
        this.displayDateText = displayDateText;
        this.profitLossFragment = profitLossFragment;

        this.onClick = onClick;

        cryptoListFull = new ArrayList<>(cryptoList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.crypto_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Crypto currentCrypto = cryptoList.get(position);


        holder.tvCryptoTitle.setText(currentCrypto.getTitle());

        double cryptoValue = currentCrypto.getValue();
        boolean isProfit = currentCrypto.isProfit();

        if (cryptoValue > 0) {

            if (profitLossFragment) {

                if (isProfit){

                    holder.tvCryptoValue.setText(Html.fromHtml("<font color=\"#52cc56\">" + "<b>" + "+" +
                            context.getString(R.string.space) + new Config().numberFormatter(currentCrypto.getProfitLossPercentage()) + "%" +
                            "</b>" + "</font>" + context.getString(R.string.space) + "<font color=\"#173051\">"
                             + currentCrypto.getValue() + "</font>"), TextView.BufferType.SPANNABLE);

                }else {

                    holder.tvCryptoValue.setText(Html.fromHtml("<font color=\"#EB6060\">" + "<b>" + "-" +
                            context.getString(R.string.space) + new Config().numberFormatter(currentCrypto.getProfitLossPercentage()) + "%" +
                            "</b>" + "</font>" + context.getString(R.string.space) + "<font color=\"#173051\">"
                            + currentCrypto.getValue() + "</font>"), TextView.BufferType.SPANNABLE);

                }

            } else {

                holder.tvCryptoValue.setText(String.format("%s", currentCrypto.getValue()));

            }

            holder.tvCryptoAmount.setText(String.format("%s%s", context.getString(R.string.dollar),
                    currentCrypto.getAmount()));

        } else if (cryptoValue == 0) {

            if (isProfit) {

                holder.tvCryptoValue.setTextColor(ContextCompat.getColor(context,
                        R.color.secondaryLightColor));

                holder.tvCryptoAmount.setTextColor(ContextCompat.getColor(context,
                        R.color.secondaryLightColor));

                holder.tvCryptoValue.setText(String.format("%s %s", "+", currentCrypto.getProfitLossPercentage()));

                holder.tvCryptoAmount.setText(String.format("%s %s%s", "+", context.getString(R.string.dollar),
                        currentCrypto.getAmount()));

            } else {

                holder.tvCryptoValue.setTextColor(ContextCompat.getColor(context,
                        R.color.appRedColor));

                holder.tvCryptoAmount.setTextColor(ContextCompat.getColor(context,
                        R.color.appRedColor));

                holder.tvCryptoValue.setText(String.format("%s %s", "-", currentCrypto.getProfitLossPercentage()));

                holder.tvCryptoAmount.setText(String.format("%s %s%s", "-", context.getString(R.string.dollar),
                        currentCrypto.getAmount()));

            }

        }

        holder.tvCryptoName.setText(currentCrypto.getName());

        holder.tvDisplayLastUpdate.setText(displayDateText);

        holder.tvLastUpdate.setText(new Config().dateFormatter(currentCrypto.getDate(), "cryptoFormat"));

        holder.civCrypto.setImageResource(currentCrypto.getCryptoImage());

        /*
        *Glide.with(context)
                .load("https://images.pexels.com/photos/2536965/pexels-photo-2536965.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500")
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.ic_error)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(holder.imvProduct);*/

        holder.mcvCrypto.setOnClickListener(v -> onClick.onEvent(currentCrypto, position));

    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCryptoTitle, tvCryptoValue, tvCryptoName,
                tvCryptoAmount, tvDisplayLastUpdate, tvLastUpdate;
        MaterialCardView mcvCrypto;
        CircleImageView civCrypto;


        public ViewHolder(View itemView) {
            super(itemView);

            tvCryptoTitle = itemView.findViewById(R.id.tv_crypto_title);
            tvCryptoValue = itemView.findViewById(R.id.tv_crypto_value);
            tvCryptoName = itemView.findViewById(R.id.tv_crypto_name);
            tvCryptoAmount = itemView.findViewById(R.id.tv_crypto_amount);
            tvDisplayLastUpdate = itemView.findViewById(R.id.tv_display_last_update);
            tvLastUpdate = itemView.findViewById(R.id.tv_last_update);
            mcvCrypto = itemView.findViewById(R.id.mcv_crypto);
            civCrypto = itemView.findViewById(R.id.civ_crypto);

        }
    }

    public Filter getCryptoFilter() {
        return cryptoFilter;
    }

    private final Filter cryptoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Crypto> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(cryptoListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Crypto crypto : cryptoListFull) {

                    if (crypto.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(crypto);
                    }

                    if (crypto.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(crypto);
                    }

                    if (crypto.getDate().toLowerCase().contains(filterPattern)) {
                        filteredList.add(crypto);
                    }


                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            cryptoList.clear();
            cryptoList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
