package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeachplanMapper {
    //查询课程计划
    public TeachplanNode findTeachplanList(String id);
}
