package com.example.gifcollector.giphy;

import java.util.Map;

public class Giphy {

    public String id;
    public String type;
    public Map<String, GiphyImage> images;

    public static class GiphyImage {
        public String url;
        public int width;
        public int height;
        public int size;
        public String mp4;
        public int mp4_size;
        public String webp;
        public int webp_size;
    }
}