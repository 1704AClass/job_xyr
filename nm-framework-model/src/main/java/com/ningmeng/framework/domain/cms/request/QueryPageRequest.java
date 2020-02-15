package com.ningmeng.framework.domain.cms.request;

import com.ningmeng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryPageRequest extends RequestData {
    /**
     * 使用页面查询时候自己封装的类
     * 条件查询只需要传递一个查询对象就行了
     */
    //站点id
    @ApiModelProperty("站点Id")
    private String siteId;
    //页面ID
    @ApiModelProperty("页面Id")
    private String pageId;
    //页面名称
    @ApiModelProperty("页面名称")
    private String pageName;
    //别名
    @ApiModelProperty("页面别名")
    private String pageAliase;
    //模版id
    @ApiModelProperty("模板Id")
    private String templateId;
}
