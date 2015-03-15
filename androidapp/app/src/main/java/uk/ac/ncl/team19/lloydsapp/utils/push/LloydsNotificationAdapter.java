package uk.ac.ncl.team19.lloydsapp.utils.push;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * Created by dale on 10/11/14. Modified by Raffaello Perrotta on 15/03/2015
 */
public class LloydsNotificationAdapter extends RecyclerView.Adapter<LloydsNotificationAdapter.ViewHolder> {

    private static List<LloydsNotification> mDataset;
    private Context ctx;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Each data item is just a string in this case
        public TextView mTextView;
        // Icon item
        public ImageView mImageView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
            mImageView = (ImageView) v.findViewById(R.id.pushIcon);
        }
    }

    public LloydsNotificationAdapter(List<LloydsNotification> dataset, Context ctx) {
        this.mDataset = dataset;
        this.ctx = ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the layout inflater
        LayoutInflater l = LayoutInflater.from(parent.getContext());

        // Create a new view from XML
        View v = l.inflate(R.layout.message_row, parent, false);

        // Return the ViewHolder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getNotificationMessage());

        if(holder.mTextView.getText().toString().startsWith(ctx.getString(R.string.ic_heartbeat))){
            holder.mImageView.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_money));
            holder.mTextView.setText(holder.mTextView.getText().toString().replace(ctx.getString(R.string.ic_heartbeat), ""));

        }else if(holder.mTextView.getText().toString().startsWith(ctx.getString(R.string.ic_offers))){
            holder.mImageView.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_shop));
            holder.mTextView.setText(holder.mTextView.getText().toString().replace(ctx.getString(R.string.ic_offers), ""));

        }else if(holder.mTextView.getText().toString().startsWith(ctx.getString(R.string.ic_info))){
            holder.mImageView.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_about));
            holder.mTextView.setText(holder.mTextView.getText().toString().replace(ctx.getString(R.string.ic_info), ""));

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
