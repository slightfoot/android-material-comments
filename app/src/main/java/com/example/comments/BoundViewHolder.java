package com.example.comments;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;


/**
 * Bindable ViewHolder
 * <p>
 * Created by Simon on 30/05/2016.
 */
class BoundViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder
{
	public final B binding;

	public BoundViewHolder(B binding)
	{
		super(binding.getRoot());
		this.binding = binding;
	}
}
