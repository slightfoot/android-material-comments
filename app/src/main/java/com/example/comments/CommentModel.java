package com.example.comments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Random;


/**
 * Comment Model
 * http://jsonplaceholder.typicode.com/
 * <p>
 * Created by Simon on 30/05/2016.
 */
public class CommentModel
{
	public final int postId;
	public final int id;
	public final String name;
	public final String email;
	public final String body;

	public final transient Drawable image;


	private CommentModel(int postId, int id, String name, String email, String body, Drawable image)
	{
		this.postId = postId;
		this.id = id;
		this.name = name;
		this.email = email;
		this.body = body;
		this.image = image;
	}

	public static class CommentDeserializer implements JsonDeserializer<CommentModel>
	{
		private static final int[] sImages = {
			R.drawable.person1, R.drawable.person2, R.drawable.person3, R.drawable.person4,
		};

		private final Context mContext;
		private final Random mRandom;


		public CommentDeserializer(Context context)
		{
			mContext = context;
			mRandom = new Random();
		}

		@Override
		public CommentModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
		{
			Drawable image = ContextCompat.getDrawable(mContext,
				sImages[mRandom.nextInt(sImages.length)]);
			JsonObject o = json.getAsJsonObject();
			return new CommentModel(
				o.get("postId").getAsInt(),
				o.get("id").getAsInt(),
				o.get("name").getAsString(),
				o.get("email").getAsString(),
				o.get("body").getAsString(),
				image);
		}
	}
}
