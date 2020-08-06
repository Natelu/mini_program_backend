package com.info.share.mini.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUtil {

    private static Logger logger = LogManager.getLogger(FileUtil.class);
    private static String qrImagesPath;
    @Value("${wechat.QRImagePath}")
    private void setQrImagesPath(String path){ qrImagesPath = path;}

    private static String nginxStaticUrl;
    @Value("${wechat.nginxUrl}")
    private void setNginxStaticUrl(String url){ nginxStaticUrl = url;}

    // 上传文件（客服二维码)
    public JSONObject uploadImage(String fileName, MultipartFile file){
        String shortName = fileName;
        fileName = qrImagesPath + fileName;
        logger.info( "待保存的路径"  + fileName);
        File destFile = new File(fileName);
        // 如果父目录不存在 创建
        if (!destFile.getParentFile().exists()){
            destFile.getParentFile().mkdir();
        }
        JSONObject ressult = new JSONObject();
        boolean isSuccess = false;
        String message = "";
        String fileUrl = "";
        try {
            file.transferTo(destFile);
            message = "文件上传成功";
            isSuccess = true;
            fileUrl = nginxStaticUrl + shortName;
        } catch (IOException e) {
            e.printStackTrace();
            message = e.getLocalizedMessage();
        }
        ressult.put("status", isSuccess);
        ressult.put("msg", message);
        ressult.put("fileUrl", fileUrl);
        return ressult;
    }
}
