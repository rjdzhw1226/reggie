package com.itheima.reggie.utils;


import java.io.File;

/**
 * 图片工具类
 */
public class ImageUtils {

    
    /**
     * 用于删除图片
     */
    public static void deleteImg(String basePath,String name) {
        System.out.println(basePath + name);
        //delFile(basePath + name);
        File file = new File(basePath + name);
        file.delete();
    }

    /**
     * 删除文件
     * @param filePathAndName 指定得路径
     */
    public static void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();
        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        String base = "D:\\DOWNLOAD\\项目\\REGGIE\\img\\";
//        //String name = "5e8ef808-3780-4496-9771-9711d757fd7d.png";
//        String name = "a124b4c9-0d5d-4abf-b522-4530075630da.png";
//        deleteImg(base,name);
//    }
}
