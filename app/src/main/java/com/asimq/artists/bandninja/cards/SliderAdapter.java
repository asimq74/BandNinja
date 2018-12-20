package com.asimq.artists.bandninja.cards;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.json.Artist;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

	private final Context context;
	private final View.OnClickListener listener;
	private final List<Artist> artists;

	public SliderAdapter(Context context, List<Artist> artists, View.OnClickListener listener) {
		this.context = context;
		this.artists = artists;
		this.listener = listener;
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
		holder.setContent(context, artists.get(position));
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
