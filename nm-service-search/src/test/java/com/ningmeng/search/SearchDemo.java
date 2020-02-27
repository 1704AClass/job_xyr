package com.ningmeng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchDemo {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 增改（简单） ES使用logstah 插件完成自动同步，删需要手动同步
     * 查（重要）
     */

    //DSL查询方式: 分页、高亮、不同域方式

    /**
     * 查询全部
     * @throws Exception
     */
    @Test
    public void getDSL() throws Exception{
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("nm_course");
        //指定搜索类型
        searchRequest.types("doc");
        //搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建搜索条件 QueryBuilders.matchAllQuery() 查询全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //定义过滤条件
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        //搜索绑定条件
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");
            System.out.println("name:"+a+"++++++++++++++studymodel:"+b);
        }

    }
    //添加文档document
    @Test
    public void testAddDoc() throws IOException {
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        //jsonMap.put("id","1");
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);


        //第一个 索引库名称 第二个类型（表名）
        //索引请求
        IndexRequest indexRequest = new IndexRequest("nm_course","doc");
        //指定索引文档内容
        indexRequest = indexRequest.source(jsonMap);
        //索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);
        //获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println("-----------------------"+result);
    }


    //查询 根据Id String index,String type,String id
    //查询文档
    @Test
    public void getDoc() throws IOException {

        GetRequest getRequest = new GetRequest("nm_course", "doc", "3");GetResponse getResponse = client.get(getRequest);
        boolean exists = getResponse.isExists();
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println("================="+sourceAsMap);
    }

    //看看有没有Id 如果有Id 根据Id删除数据，然后再添加 没有添加
    //更新文档
    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("nm_course", "doc",
                "1");
        Map<String, String> jsonmap = new HashMap<>();
        jsonmap.put("name", "spring cloud实战123");
        updateRequest = updateRequest.doc(jsonmap);
        UpdateResponse update = client.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }

    //根据id删除文档
    @Test
    public void testDelDoc() throws IOException {
        //删除文档id
        String id = "1";
        //删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest("nm_course","doc",id);
        //响应对象
        DeleteResponse deleteResponse = client.delete(deleteRequest);
        //获取响应结果
        DocWriteResponse.Result result = deleteResponse.getResult();
        System.out.println(":::::::::::::::::::::"+result);
    }
}
