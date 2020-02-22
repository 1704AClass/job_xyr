package com.ningmeng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ningmeng.framework.domain.course.CourseBase;
import com.ningmeng.framework.domain.course.CourseMarket;
import com.ningmeng.framework.domain.course.CoursePic;
import com.ningmeng.framework.domain.course.Teachplan;
import com.ningmeng.framework.domain.course.ext.CourseInfo;
import com.ningmeng.framework.domain.course.ext.TeachplanNode;
import com.ningmeng.framework.domain.course.response.CourseCode;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.QueryResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_course.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    @Autowired
    private TeachplanRepository teachplanRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseMarketRepository courseMarketRepository;
    @Autowired
    private CoursePicRepository coursePicRepository;

    //查询课程计划
    public TeachplanNode findTeachplanList(String id){
        return teachplanMapper.findTeachplanList(id);
    }

    /**
     * 根据公司Id查询课程列表
     * 想使用spring事务怎么办？springBoot自动加入spring事务管理
     */
    @Transactional
    public QueryResponseResult findCourseList(int page,int pagesize,String companyId){

        //做异常处理
        if(companyId == null || "".equals(companyId)){
            //通用的异常处理 都是父子级关系
            CustomExceptionCast.cast(CommonCode.FAIL);
        }

        PageHelper.startPage(page,pagesize);
        //这中间不能有sql语句
        Page<CourseInfo> pageAll = courseMapper.findCourseListPage(companyId);

        QueryResult queryResult = new QueryResult();
        queryResult.setList(pageAll.getResult());
        queryResult.setTotal(pageAll.getTotal());

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }


    //获取课程根结点，如果没有则添加根结点
    public String getTeachplanRoot(String courseId){
        //校验课程id
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        CourseBase courseBase = optional.get();
        //取出课程计划根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if(teachplanList == null || teachplanList.size()==0){
            //新增一个根节点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");//1级
            teachplanRoot.setStatus("0");//未发布
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = teachplanList.get(0);
        return teachplan.getId();
    }

    //添加课程计划
    public ResponseResult addTeachplan(Teachplan teachplan){
        //校验课程id和课程计划名称
        if(teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())){
            CustomExceptionCast.cast(CommonCode.INCALID_PARAM);
        }
        //取出课程id
        String courseid = teachplan.getCourseid();
        //取出父节点id
        String parentid = teachplan.getParentid();
        if(StringUtils.isEmpty(parentid)){
            //如果父节点为空则获取根节点
            parentid = getTeachplanRoot(courseid);
        }
        //取出父节点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if(!teachplanOptional.isPresent()){
            CustomExceptionCast.cast(CommonCode.INCALID_PARAM);
        }
        //父节点
        Teachplan teachplanParent = teachplanOptional.get();
        //父节点级别
        String parentGrade = teachplanParent.getGrade();
        //设置父节点
        teachplan.setParentid(parentid);
        teachplan.setStatus("0");//未发布
        //子结点的级别，根据父结点来判断
        if(parentGrade.equals("1")){
            teachplan.setGrade("2");
        }else if(parentGrade.equals("2")){
            teachplan.setGrade("3");
        }
        //设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //新增课程
    public ResponseResult addCourseBase(CourseBase courseBase){
        if(courseBase == null){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程基础信息
    public CourseBase getCourseBaseById(String courseId) {
        if(courseId == null || "".equals(courseId)){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        return courseBaseRepository.findById(courseId).get();
    }
    //更新课程基础信息
    public ResponseResult updateCourseBase(CourseBase courseBase) {
        if(courseBase == null){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程营销信息
    public CourseMarket getCourseMarketById(String courseId) {
        if(courseId == null || "".equals(courseId)){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        return courseMarketRepository.findById(courseId).get();
    }

    //更新课程营销信息
    public ResponseResult updateCourseMarket(CourseMarket courseMarket) {
        if(courseMarket == null){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        courseMarketRepository.save(courseMarket);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //添加课程图片
    @Transactional
    public ResponseResult saveCoursePic(String courseId,String pic){
        //自己抛出异常
        if(pic == null || "".equals(pic)){
            //pic图片路径有问题
            CustomExceptionCast.cast(CourseCode.COURSE_PUBLISH_VIEWERROR);
        }
        CoursePic coursePic = new CoursePic();
        //先查询课程Id有没有对应的图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if(picOptional.isPresent()){
            coursePic = picOptional.get();
        }

        //没有课程图片就新增
        coursePic.setPic(pic);
        coursePic.setCourseid(courseId);

        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursepic(String courseId) {

        //先判断 是否为空 然后再.get
        return coursePicRepository.findById(courseId).get();
    }

    //删除课程图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {

        //执行删除，返回1表示删除成功，返回0表示删除失败
        long result = coursePicRepository.deleteByCourseid(courseId);
        if(result==0){
            //失败
            return new ResponseResult(CommonCode.FAIL);
        }
        //成功
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
