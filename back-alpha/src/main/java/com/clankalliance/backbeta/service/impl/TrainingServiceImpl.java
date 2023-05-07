package com.clankalliance.backbeta.service.impl;


import com.clankalliance.backbeta.entity.DateData;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.repository.DateDataRepository;
import com.clankalliance.backbeta.repository.TrainingRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.FindGraphResponse;
import com.clankalliance.backbeta.response.LastSevenResponse;
import com.clankalliance.backbeta.service.GeneralUploadService;
import com.clankalliance.backbeta.service.TrainingService;

import com.clankalliance.backbeta.utils.DateDataIdGenerator;
import com.clankalliance.backbeta.utils.ErrorHandle;
import com.clankalliance.backbeta.utils.TokenUtil;
import com.clankalliance.backbeta.utils.TrainingIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;


@Service
public class TrainingServiceImpl implements TrainingService {

    //完整签到一周可获得的奖励
    @Value("${adhdTraining.full_week_award}")
    private Integer FULL_WEEK_AWARD;

    //图像的分辨率 代表最终传回前端的图像中点的个数的最大值
    //当图像点数超过该值的1.5倍，会对本图像平均分GRAPH_RESOLUTION端求平均值
    //来获得最终图像
    @Value("${adhdTraining.graph_resolution}")
    private Integer GRAPH_RESOLUTION;

    //签到所需的最短训练时长
    @Value("${adhdTraining.check_in_min_time}")
    private Integer CHECK_IN_MIN_TIME;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TrainingRepository trainingRepository;

    @Resource
    private DateDataRepository dateDataRepository;

    @Resource
    private GeneralUploadService generalUploadService;

    @Override
    public Integer getCHECK_IN_MIN_TIME() {
        return CHECK_IN_MIN_TIME;
    }

    @Override
    public Integer getFULL_WEEK_AWARD() {
        return FULL_WEEK_AWARD;
    }

    /**
     * 接收token与原始数据，按照同步的日期来设定时间 按照同步的编号来制定顺序
     * @param token 用户令牌
     * @param rawData 原始数据
     * @return
     */
    @Override
    public CommonResponse handleSave(String token, String rawData) {
        System.out.println("\n\n\n\n\n\n\n************************");
        System.out.println("\t\t\t接收到原始数据");
        System.out.println("\t\t\t" + rawData);
        System.out.println("\t\t\t应为<训练编号>;<金币数>;<时间>;点1,点2,...点n,");
        System.out.println("\t\t\t或<训练编号>;<金币数>;点1,点2,...点n,");
        System.out.println("************************");
        CommonResponse response = new CommonResponse<>();
        //测试后门 上线时会去掉
        if(token != null && token.equals("114514")){
            response.setSuccess(true);
            response.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            response = tokenUtil.tokenCheck(token);
        }
        if(rawData.length() < 5)
            return response;

        if(!response.getSuccess())
            return response;
        if(rawData.charAt(rawData.length() - 1) != ',')
            rawData += ",";
        //原本在此去除rawData末端的';' 现废弃，由前端去头去尾
//        rawData = rawData.substring(0, rawData.length() - 2);
        //格式:  编号;金币;时间(秒);图像 对应data[0] data[1] data[2] data[3]
        String[] data = rawData.split(";");
        String[] graph;
        if(data.length == 4){
            //有时间
            graph = data[3].split(",");
        }else{
            //没时间
            graph = data[2].split(",");
        }
        //********* 更新属性 对训练平均值和长度进行存储，提高效率***********
        long total = 0;
        Integer average;
        for(String s : graph){
            total += Long.parseLong(s);
        }
        try{
            average = Integer.parseInt("" + total/ graph.length);
        }catch (Exception e){
            return ErrorHandle.handleSaveException("上传数据为空",response);
        }
        //**********************************************************
        //为训练数据制定的更短的id生成规则
        Integer mark;
        try{
            mark = Integer.parseInt(data[0]);
        }catch (Exception e){
            mark = 0;
        }
        Integer gold;
        try{
            gold = Integer.parseInt(data[1]);
        }catch (Exception e){
            gold = 0;
        }
        Integer time;
        if(data.length == 4){
            try{
                time = Integer.parseInt(data[2]);
            }catch (Exception e){
                time = graph.length;
            }
        }else{
            time = graph.length;;
        }
        boolean hasImage ;
        try{
            hasImage = handleSaveGraph(response.getMessage(), mark, gold, data[data.length - 1], average, time);
        }catch (Exception e){
            return ErrorHandle.handleSaveException(e,response);
        }

        response.setContent(hasImage);
        response.setMessage("保存成功");
        return response;
    }

    public boolean handleSaveGraph(String userId,Integer mark, Integer gold, String graph, Integer average, Integer time ){
        Calendar calendar = Calendar.getInstance();
        Training training = new Training(TrainingIdGenerator.nextId(userId),mark,gold, graph,average, time );
        trainingRepository.save(training);
        boolean hasImage = false;
        User user = userRepository.findUserByOpenId(userId).get();
        List<DateData> dateDataList = user.getDateDataList();
        user.setGold(user.getGold() + gold);

        String dateDataId = DateDataIdGenerator.next(userId);
        Optional<DateData> dop = dateDataRepository.findDateDataById(dateDataId);
        DateData dateData;
        if(dop.isPresent()){
            dateData = dop.get();
            if(dateData.getImageName() != null){
                hasImage = true;
            }
        }else{
            dateData = new DateData(userId);
            dateDataList.add(dateData);
        }

        //由token工具检查token有效后，会一并返回动态存储的用户的id
        //此处使用id 查询到用户 因为此处token工具取得的id可信，不进行错误判断

        //由该布尔值记录是否本周满签到
        boolean fullCheckIn = true;
        //满签条件：今天是周日 且训练列表里末尾训练时间为周六 则进一步判断
        //此处训练列表以时间顺序同步，拥有顺序，也能保证查询时最省时
        if(calendar.get(Calendar.DAY_OF_WEEK) == 1 && dateDataList.size() > 0 && dateDataList.get(dateDataList.size() - 1).getDayOfTheWeek() == 7){
            if(dateData.getTime() >= getCHECK_IN_MIN_TIME()){//周日为一周的第一天，故此处减一
                int weekOfTheYear = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
                //当前签到日为周日，不需判断
                boolean weekContainer[] = {false, false, false, false, false, false, true};
                int i = dateDataList.size() - 1;
                //倒序遍历本周所有训练
                while (i >= 0 && dateDataList.get(i).getWeekOfTheYear() == weekOfTheYear && dateDataList.get(i).getTime() >= getCHECK_IN_MIN_TIME()) {
                    weekContainer[dateDataList.get(i).getDayOfTheWeek() - 2] = true;
                    i--;
                }
                //若有一天未签到，本周不满签
                for (boolean b : weekContainer) {
                    if (!b)
                        fullCheckIn = false;
                }
            }
            //完整签到一周
            if(fullCheckIn)
                user.setGold(user.getGold() + FULL_WEEK_AWARD);
        }

        dateData.addTraining(training);
        user.setThreshold(average + 5);
        dateDataList.set(dateDataList.size() - 1, dateData);
        user.setDateDataList(dateDataList);
        dateDataRepository.save(dateData);
        userRepository.save(user);
        return hasImage;
    }


    /**
     * 查询训练对应的图
     * @param token 用户令牌
     * @param id 训练id
     * @return
     */
    @Override
    public FindGraphResponse handleFindGraph(String token, String id) {
        CommonResponse responseT = new CommonResponse<>();
        if(token != null && token.equals("114514")){
            responseT.setSuccess(true);
            responseT.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            responseT = tokenUtil.tokenCheck(token);
        }
        FindGraphResponse response = new FindGraphResponse();
        if(!responseT.getSuccess()){
            response.setSuccess(false);
            response.setMessage("登录过期");
            return response;
        }
        //将新令牌赋值给response 传给前端 保存登陆状态
        response.setToken(responseT.getToken());
        //根据训练id查询训练数据
        Optional<Training> top = trainingRepository.findTrainingById(id);
        if(top.isEmpty()){
            response.setMessage("训练不存在");
            response.setSuccess(false);
        }
        //上方进行判断，训练存在，继续进行操作
        Training training = top.get();
        //半成品数据
        String halfResult[] = training.getGraph().split(",");
        int result[];

        //图像数据的时间长度 2023.2.27更改为上传时一并传时间
        //在前端计算出对应每一点的时间
        response.setSec(training.getLength());

        //图像过长，以分段取平均值的方式缩短来保证正常显示
        if(halfResult.length > 1.5 * GRAPH_RESOLUTION){
            result = new int[GRAPH_RESOLUTION];
            //每段的长度
            int passageLength = halfResult.length/GRAPH_RESOLUTION;
            //最终结果数组中的遍历器
            int resultIter = 0;
            //原始数据中的遍历器
            int halfIter = 0;
            while(resultIter < result.length){
                //本段累加和
                int num = 0;
                int cache = halfIter;
                //分段求和
                while(halfIter < halfResult.length && halfIter - cache < passageLength){
                    result[resultIter] += Integer.parseInt(halfResult[halfIter]);
                    halfIter ++;
                    num ++;
                }
                //取平均值
                if(num != 0)
                    result[resultIter] /= num;
                resultIter ++;
            }
        }else{
            //图像不长，不需要缩短
            result = new int[halfResult.length];
            for(int i = 0;i < result.length;i ++){
                result[i] = Integer.parseInt(halfResult[i]);
            }
        }

        response.setGraph(result);

        //返回的是本次训练的平均专注度
        response.setConcentration(training.getAverage());
        response.setSuccess(true);
        response.setMessage("查找成功");
        return response;
    }


    /**
     * 统计前七天数据：每天训练时间及平均专注率
     * @param token 用户token
     * @return message代表平均专注度，content代表七天图像数据
     */
    @Override
    public LastSevenResponse handleFindSeven(String token){
        CommonResponse responseT = tokenUtil.tokenCheck(token);
        LastSevenResponse response = new LastSevenResponse();
        if(!responseT.getSuccess()){
            response.setSuccess(false);
            return response;
        }else{
            response = needImageComment(responseT.getMessage(), response);
            response.setSuccess(true);
            response.setToken(responseT.getToken());
        }
        User user = userRepository.findUserByOpenId(responseT.getMessage()).get();
        List<DateData> dateDataList = user.getDateDataList();
        Double result[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-7);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dateDataListLength = dateDataList.size() - 1;
        int dateIter = 0;
        int trainingIter;
        //专注度和
        long totalC = 0;
        //专注度数据个数
        long totalNum = 0;

        Calendar dateTargetData = Calendar.getInstance();
        int daysBetween;
        while(
                dateIter <= dateDataListLength
                && dateDataList.get(dateDataListLength -  dateIter).getYear() >= year
                && (
                        dateDataList.get(dateDataListLength - dateIter).getMonth() > month
                        || (dateDataList.get(dateDataListLength - dateIter).getMonth() <= month
                        && dateDataList.get(dateDataListLength - dateIter).getDay() >= day)
                )
        ){
            //按照训练时间建立calendar 后转为Date 与当前时间做差后转换单位向下取整得到天数差
            dateTargetData.set(dateDataList.get(dateDataListLength -  dateIter).getYear(),dateDataList.get(dateDataListLength -  dateIter).getMonth(),dateDataList.get(dateDataListLength -  dateIter).getDay());
            Date timeNow = new Date();
            Date timeLastTraining = dateTargetData.getTime();
            daysBetween = Integer.parseInt( "" + (timeNow.getTime() - timeLastTraining.getTime())/(1000*60*60*24));

            DateData date = dateDataList.get(dateDataListLength - dateIter);
            List<Training> trainingList = date.getTrainingList();
            trainingIter = trainingList.size() - 1;
            while(trainingIter >= 0 && daysBetween <= 6){
                String graph[] = trainingList.get(trainingIter).getGraph().split(",");
                for(String s : graph){
                    totalC += Integer.parseInt(s);
                    totalNum ++;
                }
                result[6 - daysBetween] += trainingList.get(trainingIter).getLength();
                trainingIter --;
            }
            dateIter++;
        }
        response.setContent(result);
        response.setAverage(Integer.valueOf("" + (totalC > 0? totalC/totalNum : 0)));
        return response;
    }

    /**
     * 验证token 根据startIndex 和页长度length
     * 来返回从startIndex开始 有数据的length天的平均数据
     * 相当于返回了个字典，进行了一个预查找
     * @param token 用户令牌
     * @param startIndex 开始位置 对应用户trainingList下标
     * @param length 页长度
     * @return  返回的message为下一页的startIndex 可直接使用
     */
    @Override
    public CommonResponse handleFindDateList(String token,Integer startIndex, Integer length){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<DateData> dateDataList = user.getDateDataList();

        int dateDataListIndex;
        if(startIndex == -1){
            //初次请求，第一次加载页面时的请求
            startIndex = Math.max(dateDataList.size() - 1, 0);
        }
        dateDataListIndex = startIndex;
        int endIndex = Math.max(dateDataListIndex - length, 0);
        List<DateData> targetList = dateDataList.subList(endIndex ,dateDataList.size() > 0? dateDataListIndex + 1: 0);
        Collections.reverse(targetList);
        response.setContent(targetList);
        response.setMessage("" + (endIndex == 0? -2: endIndex));
        return response;
    }

    /**
     * 验证token,根据startIndex来返回这一天的所有训练
     * @param token 用户令牌
     * @param id 日数据id
     * @return
     */
    @Override
    public CommonResponse handleFindDateData(String token,String id){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        DateData dateData = dateDataRepository.findDateDataById(id).get();
        response.setContent(dateData.getTrainingList());
        response.setMessage("成功");
        return response;
    }

    @Override
    public LastSevenResponse needImageComment(String wxOpenId, LastSevenResponse response){
        User user = userRepository.findUserByOpenId(wxOpenId).get();
        List<DateData> dateDataList = user.getDateDataList();
        boolean needImage = false;
        boolean needComment = false;
        if(dateDataList.size() > 0){
            DateData dateData = dateDataList.get(dateDataList.size() - 1);
            Calendar calendar = Calendar.getInstance();
            if(
               dateData.getYear().equals(calendar.get(Calendar.YEAR))
               && dateData.getMonth().equals(calendar.get(Calendar.MONTH))
               && dateData.getDay().equals(calendar.get(Calendar.DAY_OF_MONTH))
            ){
                if(dateData.getImageName() == null)
                    needImage = true;
                String parent = System.getProperty("user.dir") + "/static/audio/" + wxOpenId;
                // File对象指向这个路径，file是否存在
                File dir = new File(parent);
                if(dateData.getComment() == null){
                    String[] fileList = dir.list();
                    boolean findFileToday = false;
                    for(String s : fileList){
                        if(s.contains(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日")){
                            findFileToday = true;
                            break;
                        }
                    }
                    if(!findFileToday)
                        needComment = true;
                }
            }
        }
        response.setNeedImage(needImage);
        response.setNeedComment(needComment);
        return response;
    }

    @Override
    public CommonResponse handleComment(MultipartFile audioFile, String text, String token){
        if(text == null && audioFile.isEmpty()){
            CommonResponse response = new CommonResponse<>();
            response.setMessage("音频与文本不能均为空");
            response.setSuccess(false);
            return response;
        }
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        Optional<User> uop = userRepository.findUserByOpenId(response.getMessage());
        if(uop.isEmpty()){
            response.setMessage("用户不存在");
            response.setSuccess(false);
        }
        User user = uop.get();
        List<DateData> dateDataList = user.getDateDataList();
        Calendar calendar = Calendar.getInstance();
        if(!dateDataList.isEmpty()){
            DateData dateData = dateDataList.get(dateDataList.size() - 1);
            if(
                    dateData.getYear().equals(calendar.get(Calendar.YEAR))
                    && dateData.getMonth().equals(calendar.get(Calendar.MONTH))
                    && dateData.getDay().equals(calendar.get(Calendar.DAY_OF_MONTH))){
                if(text != null && !text.equals("[object Null]") ) {
                    dateData.setComment(text);
                    dateDataRepository.save(dateData);
                }
                if(audioFile != null &&  !audioFile.isEmpty())
                    if(!generalUploadService.handleAudioFileSave(audioFile, user.getWxOpenId(), user.getNickName())){
                        response.setSuccess(false);
                        response.setMessage("音频保存出错");
                        return response;
                    }
            }else{
                response.setSuccess(false);
                response.setMessage("今日未训练");
                return response;
            }
        }else{
            response.setSuccess(false);
            response.setMessage("今日未训练");
            return response;
        }
        response.setSuccess(true);
        response.setMessage("保存成功");
        return response;
    }
}
