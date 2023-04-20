package com.clankalliance.backbeta.utils.StatusManipulateUtilsWithRedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 为适配微信做了小部分修改
 *
 * 基于redis实现用户登陆状态的管理
 * 能够在一个程序周期内维持登陆状态
 * 更新及自动回收用户状态，并自动生成身份验证用的验证码与token
 * 身份过期时间设置整合进了application.yml 便于后期维护
 * 减少对数据库的查询
 */
@Slf4j
@Component
public class ManipulateUtilRedis {

    private static int STATUS_EXPIRE_TIME;

    private final TimeUnit EXPIRE_TIME_TYPE = TimeUnit.MILLISECONDS;

    /**
     * key: token
     * value: status
     */
    @Resource
    private StringRedisTemplate statusRedisTemplate;

    /**
     * key: id
     * value: token
     */
    @Resource
    private StringRedisTemplate idRedisTemplate;

    @Value("${clankToken.statusExpireTime}")
    public void setStatusExpireTime(int time){
        STATUS_EXPIRE_TIME = time;
    }

    /**
     * 数据缓存至redis
     *
     * @param key
     * @param value
     * @return
     */
    public <V> void add(String key, V value, StringRedisTemplate targetMap) {
        try {
            if(value != null)
                targetMap.opsForValue().set(key, JSON.toJSONString(value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("数据缓存至redis失败");
        }
    }
    /**
     * 从redis中获取缓存数据，转成对象
     *
     * @param key   must not be {@literal null}.
     * @param clazz 对象类型
     * @return
     */
    public <V> V getObject(String key, StringRedisTemplate targetMap, Class<V> clazz) {
        String value = get(key, targetMap);
        V result = null;
        if (value != null && !value.equals("")) {
            result = JSONObject.parseObject(value, clazz);
        }
        return result;
    }

    /**
     * 从redis中获取缓存数据，转成list
     *
     * @param key   must not be {@literal null}.
     * @param clazz 对象类型
     * @return
     */
    public <V> List<V> getList(String key, StringRedisTemplate targetMap, Class<V> clazz) {
        String value = this.get(key, targetMap);
        List<V> result = Collections.emptyList();
        if (value != null && !value.equals("")) {
            result = JSONArray.parseArray(value, clazz);
        }
        return result;
    }
    /**
     * 功能描述：Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return java.lang.String
     * @date 2021/9/19
     **/
    public String get(String key, StringRedisTemplate targetMap) {
        String value;
        try {
            value = targetMap.opsForValue().get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("从redis缓存中获取缓存数据失败");
        }
        return value;
    }
    /**
     * 删除key
     */
    public void delete(String key, StringRedisTemplate targetMap) {
        targetMap.delete(key);
    }

    /**
     * 批量删除key
     */
    public void delete(Collection<String> keys, StringRedisTemplate targetMap) {
        targetMap.delete(keys);
    }
    /**
     * 是否存在key
     */
    public Boolean hasKey(String key, StringRedisTemplate targetMap) {
        return targetMap.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, StringRedisTemplate targetMap) {
        return targetMap.expire(key, STATUS_EXPIRE_TIME, EXPIRE_TIME_TYPE);
    }

    /**
     * 根据token查询登录状态
     * 若存在节点且没过期，则返回状态,并删除原节点。若不存在或过期，返回空节点
     * @param token 查询目标的token
     * @return
     */
    public StatusNodeWithRedis findStatusByToken(String token){
        StatusNodeWithRedis status = null;
        if(hasKey(token, statusRedisTemplate)){
            status = getObject(token, statusRedisTemplate, StatusNodeWithRedis.class);
        }
        return status;
    }

    /**
     * 根据用户id查找状态
     * @param id 用户id
     * @return
     */
    public StatusNodeWithRedis findStatusById(String id){
        StatusNodeWithRedis status = null;
        if(hasKey(id, idRedisTemplate)){
            status = getObject(getObject(id, idRedisTemplate, String.class), statusRedisTemplate, StatusNodeWithRedis.class);
        }
        return status;
    }


    /**
     * 新增状态，根据用户id自动更新登录状态
     * @param id 用户id
     */
    public String updateStatus(String id){
        int dot = -1;
        String oldToken = null;
        long updateTime = System.currentTimeMillis();
        String newToken = DigestUtils.sha1Hex(id + updateTime);
        boolean temp = hasKey(id, idRedisTemplate);
        if(hasKey(id, idRedisTemplate)){
            oldToken = getObject(id, idRedisTemplate, String.class);
            dot = (getObject(oldToken, statusRedisTemplate, StatusNodeWithRedis.class)).getCurrentDot();
            delete(oldToken, statusRedisTemplate);
        }
        add(id, newToken, idRedisTemplate);
        StatusNodeWithRedis status = new StatusNodeWithRedis(id, dot);
        add(newToken, status, statusRedisTemplate);
        expire(newToken, statusRedisTemplate);
        expire(id, idRedisTemplate);
        return newToken;
    }

    /**
     * 新增状态，根据旧状态只更新点数据，不延长登陆状态
     * @param id 用户Id
     * @param dot 新的点数据
     */
    public void updateStatus(String id,int dot){
        if(!hasKey(id, idRedisTemplate))
            return;
        String token = getObject(id, idRedisTemplate, String.class);
        StatusNodeWithRedis status = getObject(token, statusRedisTemplate, StatusNodeWithRedis.class);
        status.setCurrentDot(dot);
        add(token, status, statusRedisTemplate);
    }

}
