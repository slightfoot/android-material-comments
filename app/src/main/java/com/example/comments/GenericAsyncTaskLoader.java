package com.example.comments;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;


/**
 * Generic AsyncTaskLoader
 * <p>
 * Created by Simon on 30/05/2016.
 */
public abstract class GenericAsyncTaskLoader<D> extends AsyncTaskLoader<D>
{
	private D mData;


	public GenericAsyncTaskLoader(Context context)
	{
		super(context);
	}

	public D getData()
	{
		return mData;
	}

	@Override
	protected void onStartLoading()
	{
		if(mData != null){
			deliverResult(mData);
		}
		if(mData == null || takeContentChanged()){
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading()
	{
		cancelLoad();
	}

	@Override
	protected void onReset()
	{
		super.onReset();
		onStopLoading();
		if(mData != null){
			mData = null;
		}
	}

	@Override
	public void onCanceled(D data)
	{
		mData = null;
	}

	@Override
	public void deliverResult(D data)
	{
		mData = data;
		if(isStarted() && !isReset()){
			super.deliverResult(data);
		}
	}
}
