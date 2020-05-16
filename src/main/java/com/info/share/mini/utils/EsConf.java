package com.info.share.mini.utils;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class EsConf {

    @Bean(name = "highLevelClient")
    RestHighLevelClient elasticSearchClient(){
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo("39.100.150.192:9200")
                .build();
        RestHighLevelClient client = RestClients.create(configuration).rest();
        System.out.println(client.toString());
        return client;
    }
}
