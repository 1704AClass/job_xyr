package com.ningmeng.manage_media.service;

import com.ningmeng.framework.domain.media.MediaFile;
import com.ningmeng.framework.domain.media.response.CheckChunkResult;
import com.ningmeng.framework.domain.media.response.MediaCode;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_media.config.MediaUploadController;
import com.ningmeng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

@Service
public class MediaUploadService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MediaUploadController.class);

    @Autowired
    MediaFileRepository mediaFileRepository;

    //nm-service-manage-media.upload-location
    //上传文件根目录
    @Value("${nm‐service‐manage‐media.upload‐location}")
    private String uploadPath;


    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5,String fileExt){
        String filePath = uploadPath+fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) +
                "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return filePath;
    }

    //得到文件目录相对路径，路径中去掉根目录
    private String getFileFolderRelativePath(String fileMd5,String fileExt){
        String filePath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" +
                fileMd5 + "/";
        return filePath;
    }

    //得到文件所在目录
    private String getFileFolderPath(String fileMd5){
        String fileFolderPath = uploadPath+ fileMd5.substring(0, 1) + "/" + fileMd5.substring(1,
                2) + "/" + fileMd5 + "/" ;
        return fileFolderPath;
    }

    //创建文件目录
    private boolean createFileFold(String fileMd5){
        //创建上传文件目录
        String fileFolderPath = getFileFolderPath(fileMd5);
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            //创建文件夹
            boolean mkdirs = fileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }

    //得到块文件所在目录
    private String getChunkFileFolderPath(String fileMd5){
        String fileChunkFolderPath = getFileFolderPath(fileMd5) + "chunks" + "/";
        return fileChunkFolderPath;
    }

    //创建块文件目录
    private boolean createChunkFileFolder(String fileMd5){
        //创建上传文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            //创建文件夹
            boolean mkdirs = chunkFileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }

    //文件上传注册
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String
            mimetype, String fileExt) {
        //1、检查文件是否上传
        //得到文件的路径
        String fileFolderPath = this.getFileFolderPath(fileMd5);
        File file = new File(fileFolderPath);
        //2、查询数据库文件是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        //文件存在直接返回
        if(file.exists() && optional.isPresent()){
            CustomExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //根据MD5创建文件目录
        boolean fileFold = createFileFold(fileMd5);
        if(!fileFold){
            CustomExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }



    //检查分块是否存在
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        //得到块文件所在路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkPath = chunkFileFolderPath + "chunks/";
        //因为分块群文件用的就是数字名称（分块下白鸥就是文件名称）没有后缀 0 1 2
        File chunkFile = new File(chunkPath + chunk);
        if(chunkFile.exists()){
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,true);
        }else{
            return new CheckChunkResult(null,false);
        }
    }



    //块文件上传
    public ResponseResult uploadchunk(MultipartFile file, Integer chunk,String fileMd5) {
        if(file == null){
            CustomExceptionCast.cast(MediaCode.UPLOAD_CHUNKFILE_IS_NULL);
        }
        //创建块文件目录
        boolean fileFold = createChunkFileFolder(fileMd5);

        //块文件
        File chunkfile = new File(getChunkFileFolderPath(fileMd5) + chunk);
        //上传的块文件
        InputStream inputStream= null;
        FileOutputStream outputStream = null;

        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkfile);
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            return new ResponseResult(CommonCode.FAIL);
        }finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(outputStream!=null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取所有块文件
    private File[] getChunkFiles(File chunkfileFolder){
        //获取路径下的所有块文件
        File[] files = chunkfileFolder.listFiles();
        //排序
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        return files;
    }


    //合并块文件
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String
            mimetype, String fileExt) {
        //获取块文件的路径
        String chunkfileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkfileFolder = new File(chunkfileFolderPath);
        if(!chunkfileFolder.exists()){
            chunkfileFolder.mkdirs();
        }
        //合并文件路径
        File mergeFile = new File(getFilePath(fileMd5,fileExt));
        //创建合并文件
        //合并文件存在先删除再创建
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        boolean newFile = false;
        try {
            newFile = mergeFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!newFile){
            CustomExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //获取块文件，此列表是已经排好序的列表
        File[] chunkFiles = this.getChunkFiles(chunkfileFolder);
        //合并文件
        File newFiles = this.mergeFile(mergeFile, chunkFiles);

        //校验文件(md5值)
        boolean bool = this.checkFileMd5(mergeFile, fileMd5);
        if(!bool){
            CustomExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5,fileExt));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);//状态为上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //校验文件的md5值
    private boolean checkFileMd5(File mergeFile,String md5){
        InputStream inputStream = null;
        //进行md5校验
        FileInputStream mergeFileInputstream = null;
        try {
            mergeFileInputstream = new FileInputStream(mergeFile);
            //得到文件的md5
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileInputstream);
            //比较md5
            if(md5.equalsIgnoreCase(mergeFileMd5)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(inputStream!=null){
                    inputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 合并文件
     * @param mergeFile 合并文件空文件
     * @param chunkFiles 分块文件列表
     * @return
     */
    private File mergeFile(File mergeFile,File[] chunkFiles){
        try {
            //创建写文件对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
            //遍历分块文件开始合并
            //读取文件缓冲区
            byte[] b = new byte[1024];
            for(File chunkFile:chunkFiles){
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
                int len = -1;
                //读取分块文件
                while((len = raf_read.read(b))!=-1){
                    //向合并文件中写数据
                    raf_write.write(b,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mergeFile;
    }
}

