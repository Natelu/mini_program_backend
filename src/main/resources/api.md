# api references

## host  
* http://39.100.150.192:8090

##  文章类

    1.文章列表
        - /article/list/{theme}
        - url 变量：theme，主题
        - 可选参数：?pageNumber=xx, 默认为1，没页默认10个文章
        - 返回数据：
 ```
    {
      "data": {
        "totalPage": 2,
        "currentPage": 1,
        "articles": [
          {
            "contentPreview": "和供应能力的充足息息相关，其他行业同样，我们是一个全球消费一体化的时代，同时也是一个全球供应一体化的时代。既",
            "id": "c63394781837457f99d5844f6c9df4a4",
            "readCont": 0,
            "tag": "流量",
            "title": "“互联网思维”大起底"
          },
          {
            "contentPreview": "和供应能力的充足息息相关，其他行业同样，我们是一个全球消费一体化的时代，同时也是一个全球供应一体化的时代。既",
            "id": "ea85b6dc402e46758e72fa0291c953f5",
            "readCont": 0,
            "tag": "流量",
            "title": "“互联网思维”大起底"
          }
        ]
      },
      "message": "get article list successfully!",
      "status": 200
    }
    
    2. 文章详情
        - /article/{article_id}/info?openId=user_open_id
        - 参数：
            - article_id 文章id
            - user_open_id ,测试用例test0001(普通用户）, test0002(vip)；普通用户仅显示contentPreview内容。
        - 返回数据：
```
    {
      "data": {
        "author": "luzx",
        "content": "和供应能力的充足息息相关，其他行业同样，我们是一个全球消费一体化的时代，同时也是一个全球供应一体化的时代。既然基础服务难以产生很高的溢价，那么定价策略就必须要向增值服务转移。QQ解决生存问题的收入是来自于移动QQ的流量收费；宜家销售的也不是家具，而是简洁、美观以及高性价比的解决方案，所以在宜家不同商品的利润差距非常明显；麦当劳在中国从售卖合家欢的聚餐场所，再到销售快餐中的“快”，卖的都不仅仅是汉堡；迪斯尼通过电影和乐园所提供的卡通形象，一年可以在全球卖掉300亿美元以上零售额的授权商品；这些都是在互联网时代供应非常充盈时，定价策略的成功案例",
        "contentPreview": "和供应能力的充足息息相关，其他行业同样，我们是一个全球消费一体化的时代，同时也是一个全球供应一体化的时代。既",
        "id": "c63394781837457f99d5844f6c9df4a4",
        "publishTime": 1588287424000,
        "readCont": 0,
        "tag": "流量",
        "theme": "生活",
        "title": "“互联网思维”大起底"
      },
      "message": "successfully get the article detail.",
      "isVip": true,
      "status": 200
    }
    
