package com.ningmeng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.ningmeng.filesystem.dao.FileSystemRepository;
import com.ningmeng.framework.domain.filesystem.FileSystem;
import com.ningmeng.framework.domain.filesystem.response.FileSystemCode;
import com.ningmeng.framework.domain.filesystem.response.UploadFileResult;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Service
public class FileSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemService.class);
    @Value("${ningmeng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${ningmeng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${ningmeng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${ningmeng.fastdfs.charset}")
    String charset;
    @Autowired
    FileSystemRepository fileSystemRepository;

    /**
     * 初始化 加载fastDFS配置信息
     */
    private void initFdfsConfig(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            //初始化文件系统出错
        }
    }

    /**
     * 上传文件
     * @param file
     * @param filetag
     * @param businesskey
     * @param metadata
     * @return
     */
    @Transactional
    public UploadFileResult upload(MultipartFile file,
                                   String filetag,
                                   String businesskey,
                                   String metadata){
        //1.上传文件到fastDFS 成功之后返回路径（file_id）
        String file_id = fdfs_upload(file);

        if(file == null){
            CustomExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //2.把上传成功的信息保存到文件系统数据库中
        FileSystem fileSystem = new FileSystem();
        //文件id
        fileSystem.setFileId(file_id);
        //文件在文件系统中的路径
        fileSystem.setFilePath(file_id);
        //业务标识
        fileSystem.setBusinesskey(businesskey);
        //标签
        fileSystem.setFiletag(filetag);
        //名称
        fileSystem.setFileName(file.getOriginalFilename());
        //文件类型
        fileSystem.setFileType(file.getContentType());

        //元数据
        try {
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        } catch (Exception e) {
            CustomExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_METAERROR);
        }

        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    /**
     * 上传文件到fdfs，返回文件id
     * @param file
     * @return
     */
    public String fdfs_upload(MultipartFile file) {
        try {
            //加载fdfs的配置
            initFdfsConfig();
            //创建tracker client
            TrackerClient trackerClient = new TrackerClient();
            //获取trackerServer服务器地址（找到上传下载的服务器）
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storageServer服务器（上传下载）
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //获得上传的客户端storage
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //上传文件
            //文件字节
            byte[] bytes = file.getBytes();
            //文件原始名称
            String originalFilename = file.getOriginalFilename();
            //文件扩展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //文件id
            String file_id = storageClient1.upload_file1(bytes, extName, null);
            return file_id;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
