package com.example.comments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.comments.databinding.ActivityCommentsBinding;
import com.example.comments.databinding.ItemCommentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Material Comments Activity
 * <p>
 * Created by Simon on 30/05/2016.
 */
public class CommentsActivity extends AppCompatActivity implements
	LoaderManager.LoaderCallbacks<ArrayList<CommentModel>>
{
	private ActivityCommentsBinding mBinding;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comments);
		mBinding.commentsList.setItemAnimator(new CommentsItemAnimator(this));

		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<ArrayList<CommentModel>> onCreateLoader(int id, Bundle args)
	{
		return new CommentsLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<CommentModel>> loader, ArrayList<CommentModel> data)
	{
		if(data != null){
			mBinding.commentsList.setAdapter(new CommentAdapter(CommentsActivity.this, data));
		}
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<CommentModel>> loader)
	{
		mBinding.commentsList.setAdapter(null);
	}


	public interface CommentsPresenter
	{
		void onCommentClick(int position, CommentModel comment);
	}

	private static class CommentAdapter extends
		RecyclerView.Adapter<BoundViewHolder<ItemCommentBinding>> implements CommentsPresenter
	{
		public final static Object ITEM_EXPANDING  = new Object();
		public final static Object ITEM_COLLAPSING = new Object();

		private final LayoutInflater mInflater;
		private final ArrayList<CommentModel> mItems;

		private int mExpandedPosition = RecyclerView.NO_POSITION;


		public CommentAdapter(Context context, ArrayList<CommentModel> items)
		{
			mInflater = LayoutInflater.from(context);
			mItems = items;
			setHasStableIds(true);
		}

		@Override
		public BoundViewHolder<ItemCommentBinding> onCreateViewHolder(ViewGroup parent, int viewType)
		{
			ItemCommentBinding binding = ItemCommentBinding.inflate(mInflater, parent, false);
			binding.setPresenter(this);
			return new BoundViewHolder<>(binding);
		}

		@Override
		public void onBindViewHolder(BoundViewHolder<ItemCommentBinding> holder, int position)
		{
			boolean expanded = position == mExpandedPosition;
			holder.binding.setPosition(position);
			holder.binding.setComment(mItems.get(position));
			holder.binding.setExpanded(expanded);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				holder.itemView.setElevation(holder.itemView.getResources()
					.getDimension(expanded ? R.dimen.comment_expanded: R.dimen.comment_collapsed));
			}
			holder.binding.executePendingBindings();
		}

		@Override
		public void onBindViewHolder(BoundViewHolder<ItemCommentBinding> holder, int position, List<Object> payloads)
		{
			if(payloads.isEmpty()){
				onBindViewHolder(holder, position);
			}
			else if(payloads.contains(ITEM_EXPANDING)){
				holder.binding.setExpanded(true);
				holder.binding.executePendingBindings();
			}
			else if(payloads.contains(ITEM_COLLAPSING)){
				holder.binding.setExpanded(false);
				holder.binding.executePendingBindings();
			}
		}

		@Override
		public void onCommentClick(int position, CommentModel comment)
		{
			if(mExpandedPosition != RecyclerView.NO_POSITION){
				notifyItemChanged(mExpandedPosition, ITEM_COLLAPSING);
			}
			if(position == mExpandedPosition){
				mExpandedPosition = RecyclerView.NO_POSITION;
			}
			else{
				mExpandedPosition = position;
				notifyItemChanged(mExpandedPosition, ITEM_EXPANDING);
			}
		}

		@Override
		public long getItemId(int position)
		{
			return mItems.get(position).id;
		}

		@Override
		public int getItemCount()
		{
			return mItems.size();
		}
	}


	private class CommentsItemAnimator extends DefaultItemAnimator
	{
		private Map<RecyclerView.ViewHolder, AnimatorSet> changeAnimatorsMaps = new HashMap<>();
		private final float mExpandedElevation;
		private final float mCollapsedElevation;


		public CommentsItemAnimator(Context context)
		{
			long mediumAnimTime = context.getResources()
				.getInteger(android.R.integer.config_mediumAnimTime);
			setMoveDuration(mediumAnimTime);
			setChangeDuration(mediumAnimTime);
			mExpandedElevation  = context.getResources().getDimension(R.dimen.comment_expanded);
			mCollapsedElevation = context.getResources().getDimension(R.dimen.comment_collapsed);
		}

		@NonNull
		@Override
		public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state,
			@NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads)
		{
			CommentItemHolderInfo itemHolderInfo = (CommentItemHolderInfo) super.recordPreLayoutInformation(
				state, viewHolder, changeFlags, payloads);
			if((changeFlags & FLAG_CHANGED) != 0 && !payloads.isEmpty()){
				itemHolderInfo.isExpanding  = payloads.contains(CommentAdapter.ITEM_EXPANDING);
				itemHolderInfo.isCollapsing = payloads.contains(CommentAdapter.ITEM_COLLAPSING);
			}
			return itemHolderInfo;
		}

		@Override
		public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
			final @NonNull RecyclerView.ViewHolder newHolder,
			@NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo)
		{
			cancelCurrentAnimationIfExists(newHolder);

			ArrayList<Animator> animators = new ArrayList<>(5);
			animators.add(ObjectAnimator.ofInt(newHolder.itemView, "top",    preInfo.top,    postInfo.top));
			animators.add(ObjectAnimator.ofInt(newHolder.itemView, "left",   preInfo.left,   postInfo.left));
			animators.add(ObjectAnimator.ofInt(newHolder.itemView, "bottom", preInfo.bottom, postInfo.bottom));
			animators.add(ObjectAnimator.ofInt(newHolder.itemView, "right",  preInfo.right,  postInfo.right));

			CommentItemHolderInfo commentPreInfo = (CommentItemHolderInfo) preInfo;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				if(commentPreInfo.isExpanding){
					animators.add(ObjectAnimator.ofFloat(newHolder.itemView, "elevation", mExpandedElevation));
				}
				else if(commentPreInfo.isCollapsing){
					animators.add(ObjectAnimator.ofFloat(newHolder.itemView, "elevation", mCollapsedElevation));
				}
			}

			AnimatorSet changeAnimator = new AnimatorSet();
			changeAnimator.setDuration(getChangeDuration());
			changeAnimator.playTogether(animators);
			changeAnimator.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					changeAnimatorsMaps.remove(newHolder);
					dispatchAnimationFinished(newHolder);
				}
			});
			changeAnimator.start();

			changeAnimatorsMaps.put(newHolder, changeAnimator);

			return false;
		}

		private void cancelCurrentAnimationIfExists(RecyclerView.ViewHolder item)
		{
			if(changeAnimatorsMaps.containsKey(item)){
				changeAnimatorsMaps.get(item).cancel();
			}
		}

		@Override
		public void endAnimation(RecyclerView.ViewHolder item)
		{
			super.endAnimation(item);
			cancelCurrentAnimationIfExists(item);
		}

		@Override
		public void endAnimations()
		{
			super.endAnimations();
			for(AnimatorSet set : changeAnimatorsMaps.values()){
				set.cancel();
			}
		}

		@Override
		public ItemHolderInfo obtainHolderInfo()
		{
			return new CommentItemHolderInfo();
		}

		private class CommentItemHolderInfo extends ItemHolderInfo
		{
			public boolean isCollapsing;
			public boolean isExpanding;
		}
	}
}
