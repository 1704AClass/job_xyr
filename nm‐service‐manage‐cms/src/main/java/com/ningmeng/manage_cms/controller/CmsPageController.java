package com.ningmeng.manage_cms.controller;


import com.ningmeng.api.cmsapi.CmsPageControllerApi;
import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.framework.domain.cms.request.QueryPageRequest;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private PageService pageService;
    @Override
    //rest风格 get查询 post增加 put修改 delete删除
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size,QueryPageRequest queryPageRequest) {
        /*//测试数据，测试接口是否正常运行
        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(2);
        //静态数据列表
        List list = new ArrayList();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试页面");
        list.add(cmsPage);

       queryResult.setList(list);
       QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
       return queryResponseResult;*/
        return pageService.findList(page,size,queryPageRequest);
    }

    @Override
    //使用get还是post
    @PostMapping("/add")
    public ResponseResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    @Override
    @GetMapping("/findOne/{id}")
    public CmsPage findOne(@PathVariable("id") String id) {

        return pageService.findOne(id);
    }

    @Override
    @PutMapping("/update")
    public ResponseResult update(@RequestBody CmsPage cmsPage) {

        return pageService.update(cmsPage);
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {

        return pageService.delete(id);
    }

    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.postPage(pageId);
    }


    /**
     * String  = 页面物理路径=站点物理路径+页面物理路径+页面名称
     * @param cmsPageId
     * @return
     */
    @GetMapping("/preview/{id}")
    public String preview(@PathVariable("id") String cmsPageId){
        return pageService.preview(cmsPageId);
    }
}
