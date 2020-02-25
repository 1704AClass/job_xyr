package com.ningmeng.framework.domain.course.ext;

import com.ningmeng.framework.domain.course.CourseBase;
import com.ningmeng.framework.domain.course.CourseMarket;
import com.ningmeng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 组装模板页面需要的数据接口（Map）
 * 仅供课程静态化程序调用使用
 */

@Data
@ToString
@NoArgsConstructor
public class CourseView{
    CourseBase courseBase;//基础信息
    CourseMarket courseMarket;//课程营销
    CoursePic coursePic;//课程图片
    TeachplanNode TeachplanNode;//教学计划
}
