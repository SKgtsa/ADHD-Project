package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.DateData;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.repository.DateDataRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.GeneralUploadService;
import com.clankalliance.backbeta.service.TrainingService;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Calendar;
import java.util.List;

@Service
public class GeneralUploadServiceImpl implements GeneralUploadService {

    // 设置上传文件的最大值
    public static final int FILE_MAX_SIZE = 30*1024*1024;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private DateDataRepository dateDataRepository;

    @Resource
    private UserRepository userRepository;


    @Override
    public CommonResponse handleSave(MultipartFile file, String token, String nickName){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        // 文件是否为空
        if (file.isEmpty()) {
            System.out.println("文件为空");
        }
        if (file.getSize() > FILE_MAX_SIZE) {
            System.out.println("文件大小超出限制");
        }
        //暂时不做类型检查
//        // 文件类型是否符合
//        String contentType = file.getContentType();
//        // 如果集合包含某元素则返回ture
//        if (!AVATAR_TYPE.contains(contentType)) {
//            System.out.println("文件类型不支持");
//        }
        // 上传的文件.../upload/file/文件
        String parent =
                System.getProperty("user.dir") + "/static/photo/" + response.getMessage();
        // File对象指向这个路径，file是否存在
        File dir = new File(parent);
        if (!dir.exists()) { // 检测目录是否存在
            dir.mkdirs(); // 创建当前目录
        }
        // 获取到这个文件名称 uuid工具来将生成一个新的字符串作为文件名
        // 例如：avatar01.png
        String originalFilename = file.getOriginalFilename();
        System.out.println("OriginalFilename" + originalFilename);
        // 截取文件后缀
        String suffix = "";
        int index = originalFilename.lastIndexOf(".");
        suffix = originalFilename.substring(index);
        Calendar calendar = Calendar.getInstance();
        // 随机生成前缀名并拼接
        String filename = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + nickName + suffix;

        File dest = new File(dir, filename); // 是一个空文件
        // 参数file中数据写入到这个空文件中
        try {
            file.transferTo(dest); //将file文件中的数据写入到dest文件中
        }
        catch (Exception e) {
            System.out.println("文件状态异常或文件读写异常");
        }

        // 返回文件的路径/upload/test.png
        String filePath =  "/static/photo/" + response.getMessage();

        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<DateData> dateDataList = user.getDateDataList();
        if(dateDataList.size() > 0){
            DateData dateData = dateDataList.get(dateDataList.size() - 1);
            if(
                    dateData.getYear().equals(calendar.get(Calendar.YEAR))
                            && dateData.getMonth().equals(calendar.get(Calendar.MONTH))
                            && dateData.getDay().equals(calendar.get(Calendar.DAY_OF_MONTH))
            ){
                dateData.setImageName(filePath + "/" + filename);
                dateDataRepository.save(dateData);
                dateDataList.set(dateDataList.size() - 1,dateData);
            }
        }
        user.setDateDataList(dateDataList);
        userRepository.save(user);
        response.setMessage("上传成功");
        response.setContent(filePath);
        return response;
    }

    public CommonResponse handleAvatarSave(MultipartFile file,String token){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        // 文件是否为空
        if (file.isEmpty()) {
            System.out.println("文件为空");
        }
        if (file.getSize() > FILE_MAX_SIZE) {
            System.out.println("文件大小超出限制");
        }
        String parent =
                System.getProperty("user.dir") + "/static/avatar";
        // File对象指向这个路径，file是否存在
        File dir = new File(parent);
        if (!dir.exists()) { // 检测目录是否存在
            dir.mkdirs(); // 创建当前目录
        }
        // 获取到这个文件名称 uuid工具来将生成一个新的字符串作为文件名
        // 例如：avatar01.png
        String originalFilename = file.getOriginalFilename();
        System.out.println("OriginalFilename" + originalFilename);
        // 截取文件后缀
        String suffix = "";
        int index = originalFilename.lastIndexOf(".");
        suffix = originalFilename.substring(index);
        String filename = response.getMessage() + suffix;

        File dest = new File(dir, filename); // 是一个空文件
        if(dest.exists()){
            dest.delete();
        }
        // 参数file中数据写入到这个空文件中
        try {
            file.transferTo(dest); //将file文件中的数据写入到dest文件中
        }
        catch (Exception e) {
            System.out.println("文件状态异常或文件读写异常");
        }
        // 返回文件的路径/upload/test.png
        String filePath =  "/static/avatar/" + filename;
        response.setMessage("上传成功");
        response.setContent(filePath);
        return response;
    }

    public boolean handleAudioFileSave(MultipartFile audioFile, String wxOpenId, String nickName){
        String parent =
                System.getProperty("user.dir") + "/static/audio";
        // File对象指向这个路径，file是否存在
        File dir = new File(parent);
        if (!dir.exists()) { // 检测目录是否存在
            dir.mkdirs(); // 创建当前目录
        }
        // 获取到这个文件名称 uuid工具来将生成一个新的字符串作为文件名
        // 例如：avatar01.png
        String originalFilename = audioFile.getOriginalFilename();
        System.out.println("OriginalFilename" + originalFilename);
        // 截取文件后缀
        String suffix = "";
        int index = originalFilename.lastIndexOf(".");
        suffix = originalFilename.substring(index);
        Calendar calendar = Calendar.getInstance();
        String filename = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + nickName + suffix;

        File dest = new File(dir, filename); // 是一个空文件
        if(dest.exists()){
            dest.delete();
        }
        // 参数file中数据写入到这个空文件中
        try {
            audioFile.transferTo(dest); //将file文件中的数据写入到dest文件中
        }
        catch (Exception e) {
            System.out.println("文件状态异常或文件读写异常");
            return false;
        }
        return true;
    }

}
