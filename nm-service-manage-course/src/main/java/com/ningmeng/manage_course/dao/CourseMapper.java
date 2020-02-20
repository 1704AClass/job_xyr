package com.ningmeng.manage_course.dao;

import com.github.pagehelper.Page;
import com.ningmeng.framework.domain.course.CourseBase;
import com.ningmeng.framework.domain.course.ext.CourseInfo;
import org.apache.ibatis.annotations.Mapper;


/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {
   CourseBase findCourseBaseById(String id);

   /**
    * 后台用户登录进来 根据公司不同区分课程区别
    * @param companyId 公司Id
    * @return
    */
   Page<CourseInfo> findCourseListPage(String companyId);
}
