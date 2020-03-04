package com.ningmeng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class TestMerge {

    //测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        //先得到分块数据List
        //然后惊醒排序 1-n
        //一次写入一个核名文件中
        //块文件目录
        //分块文件地址

        //块文件目录
        File chunkFolder = new File("D:/video/chunk/");
        //合并文件
        File mergeFile = new File("D:/video/haicaowu1.mp4");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        //如果不存在，开始业务
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //先得到分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            //自定义排序规格 采用升序
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                }
                return 1;
            }
        });



        //合并文件
        for(File chunkFile:fileList){
            //读取分块文件 然后写到haicaowu.mp4
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"rw");
            int len = -1;
            while((len=raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
