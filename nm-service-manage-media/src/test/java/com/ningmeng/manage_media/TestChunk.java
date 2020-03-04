package com.ningmeng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TestChunk {

    //测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        //源文件地址
        File sourceFile = new File("D:/video/haicaowu.mp4");
        // File sourceFile = new File("d:/logo.png");
        String chunkPath = "D:/video/chunk/";
        File chunkFolder = new File(chunkPath);
        if(!chunkFolder.exists()){
            //不存在创建目录
            chunkFolder.mkdirs();
        }
        //设置分块大小 以kb为单位
        long chunkSize = 1024*1024*1;
        //得到块文件个数 应该采用向上转型
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize );
        if(chunkNum<=0){
            chunkNum = 1;
        }
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //分块
        for(int i=0;i<chunkNum;i++){
            //raf_read读出来 之后 写入文件
            //指定一个文件名
            //创建分块文件
            File file = new File(chunkPath+i);
            boolean newFile = file.createNewFile();
            if(newFile){
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                //read 读到-1 读完了
                int len = -1;
                while((len = raf_read.read(b))!=-1){
                    raf_write.write(b,0,len);
                    if(file.length()>chunkSize){
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }



}
