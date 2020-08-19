package com.info.share.mini.utils;

import com.info.share.mini.entity.User;
import com.info.share.mini.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

@Component
public class Tools {

    @Resource(name = "userService")
    private UserService userService;

    public static String getRandomId(){
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");
        return id;
    }

}
