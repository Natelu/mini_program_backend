package com.info.share.mini.utils;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

@Component
public class Tools {
    public static String getRandomId(){
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");
        return id;
    }


}
