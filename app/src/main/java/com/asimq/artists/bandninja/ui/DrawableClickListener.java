package com.asimq.artists.bandninja.ui;

public interface DrawableClickListener {

    void onClick(DrawablePosition target);

    enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}
}
