package com.ningmeng.manage_cms_client.service;


import org.springframework.stereotype.Service;

@Service
public class CmsPageService {

    public void TestRabbit(String id){
        System.out.println("我被执行到了");
    }

    //保存html页面到服务器物理路径
    public void  savePageToServerPath(String pageId){
        //更具pageId查询cmsPage
        //得到html的文件id，从cmsPage中获取htmlFileId内容
        //从gridFs中查询html文件
        //得到站点Id
        //得到站点的信息
        //得到站点的物理路径
        //得到页面的物理路径（页面物理路径=站点物理路径+页面物理路径+页面名称。）
        //将html文件保存到服务器物理路径上（把文件放贷nginx能接管的位置）
    }
}
