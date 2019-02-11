package com.asimq.artists.bandninja.cards;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.room.AlbumData;

public class AlbumDataSliderAdapter extends RecyclerView.Adapter<SliderCard> {

	private final Context context;
	private final View.OnClickListener listener;
	private final List<AlbumData> musicItems;

	public AlbumDataSliderAdapter(ApplicationComponent applicationComponent, List<AlbumData> musicItems,
			View.OnClickListener listener) {
		this.context = applicationComponent.context();
		this.musicItems = musicItems;
		this.listener = listener;
	}

	@Override
	public int getItemCount() {
		return musicItems.size();
	}

	@Override
	public void onBindViewHolder(SliderCard holder, int position) {
		AlbumData musicItem = musicItems.get(position);
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

}
