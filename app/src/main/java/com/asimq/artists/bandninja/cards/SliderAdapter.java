package com.asimq.artists.bandninja.cards;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.BaseMusicItem;
import com.asimq.artists.bandninja.json.MusicItem;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

import javax.inject.Inject;

public class SliderAdapter<T extends BaseMusicItem> extends RecyclerView.Adapter<SliderCard> {

	private final Context context;
	private final View.OnClickListener listener;
	private final List<T> musicItems;

	public SliderAdapter(ApplicationComponent applicationComponent, List<T> musicItems,
						 View.OnClickListener listener) {
		this.context = applicationComponent.context();
		this.musicItems = musicItems;
		this.listener = listener;
	}

	public void clear() {
		final int size = musicItems.size();
		musicItems.clear();
		notifyItemRangeRemoved(0, size);
	}

	@Override
	public int getItemCount() {
		return musicItems.size();
	}

	@Override
	public void onBindViewHolder(SliderCard holder, int position) {
		MusicItem musicItem = musicItems.get(position);
		holder.setContent(context, musicItem);
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
