package com.ningmeng.api.searchapi;

import com.ningmeng.framework.domain.course.CoursePub;
import com.ningmeng.framework.domain.search.CourseSearchParam;
import com.ningmeng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

@Api(value = "课程搜索",description = "课程搜索",tags={"课程搜索"})
public interface EsCourseControllerApi {

    @ApiOperation("课程搜索")
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam);

    @ApiOperation("根据id查询课程信息")
    public Map<String, CoursePub> getall(String id);

}
