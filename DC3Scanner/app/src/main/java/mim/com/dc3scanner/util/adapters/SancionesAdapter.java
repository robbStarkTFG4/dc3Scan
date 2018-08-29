package mim.com.dc3scanner.util.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mim.com.dc3scanner.R;
import mim.com.dc3scanner.util.interfaces.ListenerClick;
import mim.com.dc3scanner.util.models.Sanciones;

public class SancionesAdapter extends RecyclerView.Adapter<SancionesAdapter.ViewHolder> {
    private final List<Sanciones> mDataset;
    private final ListenerClick listener;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout layout;
        TextView fechaSancion;
        TextView motivoSancion;


        public ViewHolder(View v) {
            super(v);
            layout = (LinearLayout) v;
            fechaSancion = layout.findViewById(R.id.fecha_sancion);
            motivoSancion = layout.findViewById(R.id.motivo_sancion);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SancionesAdapter(List<Sanciones> myDataset, ListenerClick listener) {
        this.mDataset = myDataset;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SancionesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sanciones_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.mTextView.setText(mDataset.);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.click(position);
            }
        });
        Sanciones empresa = mDataset.get(position);
        if(empresa.getFechaSancion()!=null) {
            holder.fechaSancion.setText(empresa.getFechaSancion().toString());
        }
        holder.motivoSancion.setText(String.valueOf(empresa.getMotivo()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        } else {
            return 0;
        }
    }
}
