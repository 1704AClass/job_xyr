package com.ningmeng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.framework.domain.cms.request.QueryPageRequest;
import com.ningmeng.framework.domain.cms.response.CmsCode;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.QueryResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_cms.config.RabbitmqConfig;
import com.ningmeng.manage_cms.dao.CmsPageRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String preview(String cmsPageId){
        //模板Id
        CmsPage cmsPage = cmsPageRepository.findById(cmsPageId).get();
        //根据cmsPage.getTemplateId();获得GridFS中保存模板文件
        //获得静态化模板数据
        //生成静态化文件内容

        return "";
    }

    /**
     * 发布页面方法
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId){
        boolean flag = creatHtml();
        if(!flag){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }

        //查询数据库
        CmsPage cmsPage = this.findOne(pageId);
        if(cmsPage == null){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }

        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("pageId",pageId);

        //消息内容
        String msg = JSON.toJSONString(msgMap);
        //获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();

        //发送json{pageId:"1"} sitId就是RoutingKey
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 创建静态页面
     * @return
     */
    private boolean creatHtml(){
        System.out.println("执行页面静态化程序，保存静态化文件完成。。。。。。");
        //成功
        return true;
    }


    /**
     * 根据Id查询对象
     * @param id
     * @return
     */
    public CmsPage findOne(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    public ResponseResult update(CmsPage cmsPage){
        //先查询对象是否存在 不存在提示失败 存在进行修改
        CmsPage cmsPage1 = this.findOne(cmsPage.getPageId());
        if(cmsPage1 != null){

            cmsPageRepository.save(cmsPage);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);

    }

    /**
     * 根据对象添加
     * @param cmsPage
     * @return
     */
    public ResponseResult add(CmsPage cmsPage){
        if(cmsPage == null){
            //向外抛异常 页面对象为空异常
            //throw  new CustomException(CommonCode.SUCCESS);
            CustomExceptionCast.cast(CommonCode.FAIL);
        }

        //添加之前 应该先查询 站点Id 和页面名称 以及 web路径
        CmsPage cmsPage1 = cmsPageRepository.findBySiteIdAndAndPageNameAndPageWebPath(cmsPage.getSiteId(), cmsPage.getPageName(), cmsPage.getPageWebPath());
        // 如果三个条件一起有相同数据就证明页面已经存在
        if(cmsPage1 != null){
            //已经存在 提示错误 异常
            CustomExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //不存在才添加
        cmsPage.setPageId(null);
        cmsPage.setPageCreateTime(new Date());
        CmsPage cmsPage2 = cmsPageRepository.save(cmsPage);

        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(10000);
        responseResult.setSuccess(true);
        responseResult.setMessage(JSON.toJSONString(cmsPage2));

        return responseResult;
    }

    /**
     * 根据Id删除页面
     */
    public ResponseResult delete(String id){
        CmsPage one = this.findOne(id);
        if(one != null){
            //try catch快捷键 ctrl+Alt+t
            cmsPageRepository.deleteById(id);

            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }



    /**
     * 根据条件查询全部数据
     * @param page 当前页数
     * @param size 页面显示个数
     * @param queryPageRequest 查询条件对象
     * @return 页面列表
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){



        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        //分页查询 初始化页码
        if (page <= 0) {
            page = 1;
        }
        //我们原来传递参数的时候 mybatis 1,10
        //为了适应mongodb的接口将页码减1
        page = page - 1;

        //分页对象
        PageRequest pageRequest = PageRequest.of(page, size);

        //构建条件构建器
        //条件值
        CmsPage cmsPage = new CmsPage();
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        if(queryPageRequest.getPageAliase() != null){
            exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if(queryPageRequest.getSiteId() != null){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if(queryPageRequest.getTemplateId() != null){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }

        //构建条件
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);

        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageRequest);

        //封装返回条件
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //封装返回对象
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
        //返回结果
        return queryResponseResult;
    }
}
