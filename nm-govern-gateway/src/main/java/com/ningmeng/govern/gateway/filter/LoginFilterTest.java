package com.ningmeng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginFilterTest extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginFilterTest.class);

    @Override
    public String filterType() {
        //filterType：返回字符串代表过滤器的类型，如下 pre：请求在被路由之前执行
        //routing：在路由请求时调用 post：在routing和errror过滤器之后调用 error：处理请求时发生错误调用
        return "pre";
    }

    @Override
    public int filterOrder() {
        //int值来定义过滤器的执行顺序，数值越小优先级越高
        return 2;
    }

    @Override
    public boolean shouldFilter() {// 该过滤器需要执行
        //是否启用当前过滤器
        return true;
    }


    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        HttpServletRequest request = requestContext.getRequest();
        //取出头部信息Authorization
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            requestContext.setSendZuulResponse(false);// 拒绝访问
            requestContext.setResponseStatusCode(200);// 设置响应状态码
            ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
            String jsonString = JSON.toJSONString(unauthenticated);
            requestContext.setResponseBody(jsonString);
            response.setContentType("application/json;charset=UTF‐8");
            return null;
        }
        return null;
    }
}