package com.example.gifcollector;

import java.util.List;

public class GiphySearch {

    public List<Giphy> data;
    public Meta meta;
    public Pagination pagination;

    class Meta {
        public int status;
        public String msg;
        public String response_id;
    }

    class Pagination {
        public int total_count;
        public int count;
        public int offset;
    }
}