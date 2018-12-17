package com.asimq.artists.bandninja.cards;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimq.artists.bandninja.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final int[] content;
    private final View.OnClickListener listener;
    private final String[] urls;
    private final Context context;

    public SliderAdapter(Context context, String[] urls, int[] content, View.OnClickListener listener) {
        this.context = context;
        this.urls = urls;
        this.content = content;
        this.listener = listener;
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

        if (listener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);
                }
            });
        }

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(SliderCard holder, int position) {
        holder.setContent(context, content[position % content.length], urls, position);
    }

    @Override
    public void onViewRecycled(SliderCard holder) {
        holder.clearContent();
    }

    @Override
    public int getItemCount() {
        return urls.length;
    }

}
