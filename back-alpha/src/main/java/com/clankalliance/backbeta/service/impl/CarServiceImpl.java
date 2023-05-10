package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.Car;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.repository.CarRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.cart.CartSettingBody;
import com.clankalliance.backbeta.service.CarService;
import com.clankalliance.backbeta.service.TrainingService;
import com.clankalliance.backbeta.utils.ErrorHandle;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;

@Service
public class CarServiceImpl implements CarService {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserRepository userRepository;

    @Resource
    private CarRepository carRepository;

    @Resource
    private TrainingService trainingService;


    /**
     * 小车上传实时数据
     * 通过轮查的方式，传输最新的专注力数值并获取最新的小车设置
     * @param id 小车id
     * @param dot 实时数据
     * @return
     */
    public CommonResponse updateDot(String id, int dot){
        Optional<Car> cop = carRepository.findById(id);
        CommonResponse response = new CommonResponse<>();
        if(cop.isEmpty()){
            response.setContent(new CartSettingBody(-1, 0));
            response.setMessage("小车不存在");
            response.setSuccess(false);
            return response;
        }
        Car car = cop.get();
        if(car.getUserId() != null){
            User user = userRepository.findUserByOpenId(car.getUserId()).get();
            response = tokenUtil.updateDot(user.getWxOpenId(),dot);
            response.setContent(new CartSettingBody(user.getThreshold(), user.getMap()));
            response.setMessage("已更新后端数据");
            response.setSuccess(true);
            if(dot == -1 && (user.getMap() == null || user.getMap() != 0)){
                user.setMap(0);
                userRepository.save(user);
            }
        }else{
            response.setContent(new CartSettingBody(-1, 0));
            response.setMessage("小车未绑定");
            response.setSuccess(false);
        }
        return response;
    }

    /**
     * 小车上传数据
     * @param id 小车id
     * @param graph 图像点序列
     * @param mark 训练标号
     * @param gold 金币数
     * @param time 训练时长
     * @return
     */
    public CommonResponse upload(String id,String graph,int mark,int gold,int time){
        CommonResponse response = new CommonResponse<>();
        Optional<Car> cop = carRepository.findById(id);
        if(cop.isEmpty()){
            response.setContent(new CartSettingBody(-1, 0));
            response.setMessage("小车不存在");
            response.setSuccess(false);
            return response;
        }
        Car car = cop.get();
        Optional<User> uop = userRepository.findUserByOpenId(car.getUserId());
        if(uop.isEmpty()){
            response.setSuccess(false);
            response.setMessage("用户不存在");
            return response;
        }
        int average = 0;
        String[] graphArray = graph.split(",");
        for(String s : graphArray)
            average += Integer.parseInt(s);
        average /= graphArray.length;

        try{
            trainingService.handleSaveGraph(car.getUserId(), mark, gold, graph + ',', average, time);
        }catch (Exception e){
            return ErrorHandle.handleSaveException(e,response);
        }

        response.setMessage("上传成功");
        response.setSuccess(true);
        return response;
    }

    public CommonResponse getDot(String token){
        CommonResponse response = tokenUtil.getDot(token);
        if(response.getSuccess())
            response.setMessage("查找成功");
        return response;
    }

    public CommonResponse getDotWithT(String token,int threshold){
        CommonResponse response = tokenUtil.getDot(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        user.setThreshold(threshold);
        try{
            userRepository.save(user);
        }catch (Exception e){
            return ErrorHandle.handleSaveException(e,response);
        }
        return response;
    }

    public CommonResponse getThreshold(String token){
        CommonResponse response = tokenUtil.getDot(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        response.setContent(user.getThreshold());
        return response;
    }

    public CommonResponse updateMap(String token,Integer map){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()){
            response.setMessage("登录失效");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        user.setMap(map);
        try{
            userRepository.save(user);
        }catch (Exception e){
            return ErrorHandle.handleSaveException(e,response);
        }
        response.setMessage("保存成功");
        return response;
    }

    public CommonResponse addCar(String id, String pass){
        CommonResponse response = new CommonResponse<>();
        if(!pass.equals("114514")){
            response.setSuccess(false);
            response.setMessage("权限错误");
            return response;
        }
        Car car = new Car();
        car.setId(id);
        try{
            carRepository.save(car);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("创建失败");
            response.setContent(e);
            return response;
        }
        response.setSuccess(true);
        response.setMessage("创建成功");
        return response;
    }

    public CommonResponse deleteCar(String id, String pass){
        CommonResponse response = new CommonResponse<>();
        if(!pass.equals("114514")){
            response.setSuccess(false);
            response.setMessage("权限错误");
            return response;
        }
        Optional<Car> car = carRepository.findById(id);
        if(car.isEmpty()){
            response.setSuccess(false);
            response.setMessage("小车不存在");
            return response;
        }
        try{
            carRepository.delete(car.get());
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("删除失败");
            response.setContent(e);
            return response;
        }
        response.setSuccess(true);
        response.setMessage("删除成功");
        return response;
    }

    public CommonResponse bindCar(String token, String id){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return  response;
        Optional<Car> cop = carRepository.findById(id);
        if(cop.isEmpty()){
            response.setSuccess(false);
            response.setMessage("小车不存在");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        Car car = cop.get();
        if(car.getUserId() != null){
            //unbind
            User oldOwner = userRepository.findUserByOpenId(car.getUserId()).get();
            Set<Car> oldOwnerCarSet = oldOwner.getCarSet();
            oldOwnerCarSet.remove(car);
            oldOwner.setCarSet(oldOwnerCarSet);
            try{
                userRepository.save(oldOwner);
            }catch (Exception e){
                response.setSuccess(false);
                response.setMessage("与小车前主人解绑失败");
                response.setContent(e);
                return response;
            }
        }
        Set<Car> carSet = user.getCarSet();
        carSet.add(car);
        user.setCarSet(carSet);
        car.setUserId(user.getWxOpenId());
        try{
            userRepository.save(user);
            carRepository.save(car);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存失败");
            response.setContent(e);
            return response;
        }
        response.setSuccess(true);
        response.setMessage("绑定成功");
        return response;
    }

}
