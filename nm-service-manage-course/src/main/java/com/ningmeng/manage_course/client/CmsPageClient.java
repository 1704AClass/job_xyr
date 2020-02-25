package com.ningmeng.manage_course.client;

import com.ningmeng.framework.client.NmServiceList;
import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = NmServiceList.NM_SERVICE_MANAGE_CMS)
public interface CmsPageClient {

    //feignClient接口 有参数在参数必须加@PathVariable("XXX")和@RequestParam("XXX")
    //feignClient返回值为复杂对象时其类型必须有无参构造函数。
    @GetMapping("/cms/findOne/{id}")
    public CmsPage findOne(@PathVariable("id") String id);

    @PostMapping("/cms/add")
    public ResponseResult add(@RequestBody CmsPage cmsPage);

}
