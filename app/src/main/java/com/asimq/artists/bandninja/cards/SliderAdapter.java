package com.asimq.artists.bandninja.cards;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimq.artists.bandninja.MainNavigationActivity;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.asynctasks.BaseSaveArtistTask;
import com.asimq.artists.bandninja.asynctasks.SaveArtistTask;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

import javax.inject.Inject;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

	private final Context context;
	private final View.OnClickListener listener;
	private final List<Artist> artists;
	@Inject
	ArtistDataDao artistDataDao;

	public SliderAdapter(ApplicationComponent applicationComponent, List<Artist> artists,
						 View.OnClickListener listener) {
		this.context = applicationComponent.context();
		this.artists = artists;
		this.listener = listener;
		final MyApplication application = (MyApplication) context;
		application.getApplicationComponent().inject(this);
	}

	public void clear() {
		final int size = artists.size();
		artists.clear();
		notifyItemRangeRemoved(0, size);
	}

	@Override
	public int getItemCount() {
		return artists.size();
	}

	@Override
	public void onBindViewHolder(SliderCard holder, int position) {
		Artist artist = artists.get(position);
		holder.setContent(context, artist);
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
		Intent localIntent = new Intent("ARTIST_BOUND");
		localIntent.putExtra("ARTIST_NAME", artist.getName());
		localIntent.putExtra("ARTIST_MBID", artist.getMbid());
		localBroadcastManager.sendBroadcast(localIntent);
	}

	private void populateTags(SliderCard holder, Artist artistDetailedInfo) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		final List<Tag> allTags = artistDetailedInfo.getTagWrapper().getTags();
		for (Tag tag : allTags) {
			sb.append(tag.getName()).append(count++ < (allTags.size() - 1) ? ", " : "");
		}
		ArtistData artistData = new ArtistData(artistDetailedInfo);
		new BaseSaveArtistTask(artistDataDao).execute(artistData);
	}

	@Override
	public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.layout_slider_card, parent, false);

		if (listener != null) {
			view.setOnClickListener(sliderLayoutView -> listener.onClick(sliderLayoutView));
		}

		return new SliderCard(view);
	}

	@Override
	public void onViewRecycled(SliderCard holder) {
		holder.clearContent();
	}

}
