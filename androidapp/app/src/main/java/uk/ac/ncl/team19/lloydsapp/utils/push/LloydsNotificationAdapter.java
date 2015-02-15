package uk.ac.ncl.team19.lloydsapp.utils.push;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * Created by dale on 10/11/14.
 */
public class LloydsNotificationAdapter extends RecyclerView.Adapter<LloydsNotificationAdapter.ViewHolder> {

    private static List<LloydsNotification> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
        }
    }

    public LloydsNotificationAdapter(List<LloydsNotification> dataset) {
        mDataset = dataset;
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
