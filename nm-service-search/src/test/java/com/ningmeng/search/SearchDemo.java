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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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


    //ESL分页查询
    @Test
    public void getESLByPage() throws Exception{

        int page = 1;
        int size = 2;

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("nm_course");
        //指定搜索类型
        searchRequest.types("doc");
        //搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //当前页数
        searchSourceBuilder.from((page-1)*size);
        //每页显示数
        searchSourceBuilder.size(size);

        //构建搜索条件 QueryBuilders.matchAllQuery() 查询全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());


        //定义过滤条件
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        //搜索绑定条件
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        long total = hits.getTotalHits();
        System.out.println(total);

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");
            System.out.println("name:"+a+"++++++++++++++studymodel:"+b);
        }
    }

    //trem查询 （精准查询）
    @Test
    public void getTermESL() throws Exception{

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("nm_course");
        //指定搜索类型
        searchRequest.types("doc");
        //搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //构建搜索条件 QueryBuilders.matchAllQuery() 查询全部
        //termQuery方法有两个参数 1.字段名 2.查询的值
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));


        //定义过滤条件
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        //搜索绑定条件
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        long total = hits.getTotalHits();
        System.out.println(total);

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");
            System.out.println("name:"+a+"++++++++++++++studymodel:"+b);
        }
    }

    //ids查询 根据id精确查询
    @Test
    public void getIdsESL() throws Exception {

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("nm_course");
        //指定搜索类型
        searchRequest.types("doc");

        String[] split = new String[]{"1", "3"};
        List<String> idList = Arrays.asList(split);

        //搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建搜索条件 QueryBuilders.termsQuery 第一个参数 查询的方式 查询id的数组
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", idList));

        //定义过滤条件
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        //搜索绑定条件
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        long total = hits.getTotalHits();
        System.out.println(total);

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");
            System.out.println("name:" + a + "++++++++++++++studymodel:" + b);
        }

    }

    //matchQuery match Query即全文检索，它的搜索方式是先将搜索字符串分词，再使用各各词条从索引中搜索。
    @Test
    public void getESLByMatchQuery() throws Exception {

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("nm_course");
        //指定搜索类型
        searchRequest.types("doc");

        //搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //构建搜索条件 QueryBuilders. matchQuery operator 查询方式
        //query：搜索的关键字，对于英文关键字如果有多个单词则中间要用半角逗号分隔，而对于中文关键字中间可以用逗号分隔也可以不用。
        //operator：or 表示 只要有一个词在文档中出现则就符合条件，and表示每个词都在文档中出现则才符合条件。
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发").operator(Operator.OR));

        //定义过滤条件
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        //搜索绑定条件
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        long total = hits.getTotalHits();
        System.out.println(total);

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");
            System.out.println("name:" + a + "++++++++++++++studymodel:" + b);
        }

    }

    //multi Query
    @Test
    public void getESLByMultiQuery() throws Exception {

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("nm_course");
        //指定搜索类型
        searchRequest.types("doc");

        //搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建搜索条件 QueryBuilders multiMatchQuery 可以查询多个字段 根据全中进行匹配的分影响显示顺序
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name",10);//提升boost

        //定义过滤条件
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel"}, new String[]{});
        //搜索绑定条件
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        long total = hits.getTotalHits();
        System.out.println(total);

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");
            System.out.println("name:" + a + "++++++++++++++studymodel:" + b);
        }

    }

    //BooleanQuery 组合方式查询
    @Test
    public void getESLByBooleanQuery() throws Exception {

        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("nm_course");
        //指定搜索类型
        searchRequest.types("doc");

        //搜索对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建搜索条件 QueryBuilders multiMatchQuery 可以查询多个字段 根据全中进行匹配的分影响显示顺序
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name", 10);//提升boost

        //TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //must == and  should == or  must_not == not
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        //设置布尔查询对象
        searchSourceBuilder.query(boolQueryBuilder);
        //设置搜索源配置
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        long total = hits.getTotalHits();
        System.out.println(total);

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");
            System.out.println("name:" + a + "++++++++++++++studymodel:" + b);
        }

    }

    //布尔查询使用过滤器 boolQueryBuilder.Filter
    //排序方法
    @Test
    public void getDSLByFilter() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","description"},
                new String[]{});
        searchRequest.source(searchSourceBuilder);
        //匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description");
        //设置匹配占比
        multiMatchQueryBuilder.minimumShouldMatch("50%");
        //提升另个字段的Boost值
        multiMatchQueryBuilder.field("name",10);
        searchSourceBuilder.query(multiMatchQueryBuilder);
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(searchSourceBuilder.query());

        //过滤拼接条件查询
        //boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "201001"));
        //gt 大于 lt小于 gte大于等于 lte小于等于
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));

        //排序 可以在字段上添加一个或多个排序，支持在keyword、date、float等类型上添加，text类型的字段上不允许添加排序。
        searchSourceBuilder.sort("studymodel",SortOrder.ASC);
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.DESC));

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    //高亮查询 Highlight
    @Test
    public void getDSLByHighlight() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","description"},
                new String[]{});
        searchRequest.source(searchSourceBuilder);
        //匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发",
                "name", "description");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(searchSourceBuilder.query());
        //过滤
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        //排序
        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));

        //高亮显示设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置前缀
        highlightBuilder.preTags("<tag>");
        //设置后缀
        highlightBuilder.postTags("</tag>");

        // 绑定高亮显示的域
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        //highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        //匹配的条数
        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String a = (String) sourceAsMap.get("name");
            String b = (String) sourceAsMap.get("studymodel");

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField nameField = highlightFields.get("name");
                if(nameField!=null){
                    Text[] texts = nameField.getFragments();
                    if(texts!=null && texts.length>0){

                        b = texts[0].toString();

                        /*for(Text str:texts){
                            b = str.toString();
                        }*/
                    }
                }
            }

            System.out.println("name:"+a+"++++++++++++++studymodel:"+b);
        }
    }

}
