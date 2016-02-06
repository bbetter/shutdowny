package com.owlsoft.shutdowny.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.owlsoft.shutdowny.BR;
import com.owlsoft.shutdowny.R;
import com.owlsoft.shutdowny.databinding.CheckpointRowBinding;
import com.owlsoft.shutdowny.models.Checkpoint;
import com.owlsoft.shutdowny.viewmodels.CheckpointViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 06.02.16.
 */
public class CheckpointAdapter extends RecyclerView.Adapter<CheckpointAdapter.CheckpointHolder> {

    public void setDbOperationsListener(DbOperationsListener dbOperationsListener) {
        this.dbOperationsListener = dbOperationsListener;
    }

    public interface DbOperationsListener {
        boolean onItemRemoved(Checkpoint checkpoint);
        boolean onItemAdded(Checkpoint checkpoint);
    }

    private Context context;
    private List<Checkpoint> data;
    private DbOperationsListener dbOperationsListener;

    public CheckpointAdapter(Context context, List<Checkpoint> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public CheckpointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckpointRowBinding rowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.checkpoint_row, parent, false);
        return new CheckpointHolder(rowBinding);
    }

    @Override
    public void onBindViewHolder(CheckpointHolder holder, int position) {
        final CheckpointViewModel checkPointVM = new CheckpointViewModel(data.get(position));
        holder.bindTo(checkPointVM);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(Checkpoint checkpoint) {
        if (data == null) {
            data = new ArrayList<>();
        }
        if(dbOperationsListener.onItemAdded(checkpoint)) {
            data.add(checkpoint);
            notifyItemInserted(data.size());
        }
    }

    public class CheckpointHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckpointRowBinding binding;

        public CheckpointHolder(CheckpointRowBinding rowBinding) {
            super(rowBinding.rootLayout);
            binding = rowBinding;
        }

        public void bindTo(CheckpointViewModel checkpoint) {
            checkpoint.setOnRemoveHandler(this);
            binding.setVariable(BR.checkpoint, checkpoint);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if(dbOperationsListener.onItemRemoved(data.get(adapterPosition))) {
                data.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
        }
    }
}
