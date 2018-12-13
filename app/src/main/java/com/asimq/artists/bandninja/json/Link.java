package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class Link {
    @SerializedName("#text")
    private String text;
    @SerializedName("rel")
    private String rel;
    @SerializedName("href")
    private String href;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "Link{" +
                "text='" + text + '\'' +
                ", rel='" + rel + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
