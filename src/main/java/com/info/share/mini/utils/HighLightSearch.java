package com.info.share.mini.utils;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;

//@Service(value = "elasticSearch")
//@Component
public class HighLightSearch {

    @Resource(name = "highLevelClient")
    private RestHighLevelClient esClient;

    public static String preTag = "<font color=\"#dd4b39\">";
    public static String postTag = "</font>"; // google的高亮色值

    private int page;

    private int pageSize;

    private String[] fields;

    private String type;

    private String index;

    public HighLightSearch(String index, String type, String[] fields, int page, int pageSize){
        EsConf esConf = new EsConf();
        this.esClient = esConf.elasticSearchClient();
        this.index = index;
        this.type = type;
        this.fields = fields;
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getFrom(){
        int from = (this.page - 1)*this.pageSize;
        if (from < 0) {from = 0;}
        return from;
    }

    public SearchHits search(String keyworkd){
//        System.out.println(this.toString());
        MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery(keyworkd, this.fields);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String field : fields){
            highlightBuilder.field(field).preTags(HighLightSearch.preTag).postTags(HighLightSearch.postTag);
        }
        //设置索引 index
        SearchRequest searchRequest = new SearchRequest(this.index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(builder);
        sourceBuilder.from(this.getFrom());
        sourceBuilder.size(this.pageSize);
        sourceBuilder.highlighter(highlightBuilder);
        sourceBuilder.timeout(new TimeValue(1000));
        searchRequest.source(sourceBuilder);
        searchRequest.types(this.type);

        SearchResponse response = null;
        try {
            response = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHits searchHits = response.getHits();
        return searchHits;
    }

    @Override
    public String toString() {
        return "HighLightSearch{" +
                "esClient=" + esClient +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", fields=" + Arrays.toString(fields) +
                ", type='" + type + '\'' +
                ", index='" + index + '\'' +
                '}';
    }
}
