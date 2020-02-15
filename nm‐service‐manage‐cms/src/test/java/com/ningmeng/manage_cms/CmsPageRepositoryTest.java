package com.ningmeng.manage_cms;

import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.framework.domain.cms.CmsPageParam;
import com.ningmeng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Test
    public void findAll(){
        //查询的是cmsPage表
        List<CmsPage> list = cmsPageRepository.findAll();
        for (CmsPage cmsPage:list) {
            System.out.println(cmsPage.getPageName());
        }
    }

    //分页查询
    @Test
    public void testFindPage() {
        int page = 0;//从0开始
        int size = 10;//每页记录数
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    //添加
    @Test
    public void testInsert(){
        //定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("1");
        cmsPage.setTemplateId("1");
        cmsPage.setPageId("1");
        //如果这个地方写null 代表让mongo帮自动生成Id
        cmsPage.setPageId(null);
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    //删除
    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("5e435570b4d3a1446c62c116");
    }

    //修改
    @Test
    public void testUpdate() {
        //mongoDB中 只有查询和修改（增删改）
        //Optional java1.8
        Optional<CmsPage> optional = cmsPageRepository.findById("5e435900b4d3a14218a79d4e");
        //Optional的优点是：
        //1、提醒你非空判断。
        //2、将对象非空检测标准化
        //3、使代码更加安全
        if(optional.isPresent()){
            //修改 和 增加save（）
            //当你的_id 如果没有就添加 有就覆盖
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("测试修改方法");
            cmsPageRepository.save(cmsPage);
        }else{
            System.out.println("修改失败");
        }
    }

    @Test
    public void testFindByName(){
        //CmsPage cmsPage = cmsPageRepository.findByPageName("测试修改方法");
        CmsPage cmsPage = cmsPageRepository.findByPageNameAndPageType("测试修改方法","1");
        //分页查询
        PageRequest pageRequest = PageRequest.of(0, 10);


        //System.out.println(cmsPage);
    }

    //自定义条件查询测试
    @Test
    public void testFindAll() {
        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
        exampleMatcher = exampleMatcher.withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains());
        //ExampleMatcher.GenericPropertyMatchers.contains() 包含
        //ExampleMatcher.GenericPropertyMatchers.startsWith()//开头匹配

        exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值
        CmsPage cmsPage = new CmsPage();
        //站点ID
        //cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        //模板ID
        //cmsPage.setTemplateId("5a962c16b00ffc514038fafd");

        cmsPage.setPageAliase("课程");
        cmsPage.setPageName("4");
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        PageRequest pageRequest = new PageRequest(0, 10);

        Page<CmsPage> pageAll = cmsPageRepository.findAll(example, pageRequest);

        System.out.println(pageAll);
    }
}
