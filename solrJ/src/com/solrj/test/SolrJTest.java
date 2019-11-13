package com.solrj.test;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrJTest {

    //向索引库中添加索引
    //实现步骤:1.
    // Solr 服务器建立连接。HttpSolrServer 对象建立连接
    //2.创建一个 SolrInputDocument 对象，然后添加域
    //3.将 SolrInputDocument 添加到索引库
    //4.提交
    @Test
    public void addDocument() throws IOException, SolrServerException {
        // Solr 服务器建立连接。HttpSolrServer 对象建立连接
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //创建一个 SolrInputDocument 对象，然后添加域
        SolrInputDocument document = new SolrInputDocument();
        //向文档中添加域
        //第一个参数：域的名称，域的名称必须是在 schema.xml中定义的 solr的schema.xml在solrcore中
        //第二个参数：域的值
        document.addField("id", "c0001");
        document.addField("title_ik", "使用solrj添加的文档");
        document.addField("content_ik", "文档内容");
        document.addField("product_name", "商品名称");
        //把document对象添加到索引库中
        solrServer.add(document);
        //提交
        solrServer.commit();
    }

    //根据id删除文档
    @Test
    public void deleteDocumentById() throws IOException, SolrServerException {
        //创建连接
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        solrServer.deleteById("c0001");
        solrServer.commit();
    }

    //根据查询删除
    @Test
    public void testDeleteByQuery() throws IOException, SolrServerException {
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8080/solr");
        httpSolrServer.deleteByQuery("*:*");
        httpSolrServer.commit();
    }

    //基本文档查询
    @Test
    public void testQueryIndex() throws SolrServerException {
        //创建连接
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //创建一个query对象
        SolrQuery solrQuery = new SolrQuery();
        //设置查询条件
        solrQuery.setQuery("*:*");
        //执行查询
        QueryResponse queryResponse = solrServer.query(solrQuery);
        //获取查询结果
        SolrDocumentList responseResults = queryResponse.getResults();
        //获取查询到商品的数量
        long count = responseResults.getNumFound();
        System.out.println("匹配条数:" + count);
        //遍历查询结果
        for (SolrDocument solrDocument : responseResults
        ) {
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("title_ik"));
            System.out.println(solrDocument.get("content_ik"));
            System.out.println(solrDocument.get("product_name"));
        }
    }

    //solr复杂查询
    @Test
    public void multiQueryIndex() throws SolrServerException {
        //创建连接
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
        //创建一个query对象
        SolrQuery solrQuery = new SolrQuery();
        //设置默认查询域
        solrQuery.set("df","product_keywords");
        //设置查询条件
        solrQuery.setQuery("钻石");
        //设置过滤条件
        solrQuery.setFilterQueries("product_catalog_name:幽默杂货");
        //设置排序
        solrQuery.setSort("product_price", SolrQuery.ORDER.asc);
        //分页处理
        solrQuery.setStart(0);
        solrQuery.setRows(10);
        //设置结果域列表
        solrQuery.setFields("id", "product_name", "product_price", "product_catalog_name", "product_picture");
        //设置高亮
        solrQuery.setHighlight(true);
        //设置高亮域
        solrQuery.addHighlightField("product_name");
        //设置高亮前缀
        solrQuery.setHighlightSimplePre("<em>");
        //设置高亮后缀
        solrQuery.setHighlightSimplePost("</em>");
        //执行查询
        QueryResponse queryResponse = solrServer.query(solrQuery);
        //获取查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        //获取共查询到的商品条数
        System.out.println("共"+solrDocumentList.getNumFound());
        //遍历结果
        for (SolrDocument document:solrDocumentList
             ) {
            System.out.println(document.get("id"));
            String product_name="";
            //取出高亮部分
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            List<String> list = highlighting.get(document.get("id")).get("product_name");
            if (list!=null){
                product_name=list.get(0);
            }else{
                product_name= (String) document.get("product_name");
            }
            System.out.println(product_name);
            System.out.println(document.get("product_price"));
            System.out.println(document.get("product_catalog_name"));
            System.out.println(document.get("product_picture"));
        }

    }
}