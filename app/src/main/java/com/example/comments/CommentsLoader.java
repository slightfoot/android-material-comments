package com.example.comments;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Comments Loader
 * <p>
 * Created by Simon on 30/05/2016.
 */
public class CommentsLoader extends GenericAsyncTaskLoader<ArrayList<CommentModel>>
{
	private static final String TAG = CommentsLoader.class.getSimpleName();

	public CommentsLoader(Context context)
	{
		super(context);
	}

	@Override
	public ArrayList<CommentModel> loadInBackground()
	{
		Gson gson = new GsonBuilder()
			.registerTypeAdapter(CommentModel.class,
				new CommentModel.CommentDeserializer(getContext()))
			.create();

		try{
			InputStream in = null;
			try{
				in = getContext().getAssets().open("comments.json");
				return gson.fromJson(new InputStreamReader(in), new TypeToken<List<CommentModel>>()
				{
				}.getType());
			}
			finally{
				if(in != null){
					in.close();
				}
			}
		}
		catch(IOException e){
			Log.e(TAG, "doInBackground", e);
			return null;
		}
	}
}
