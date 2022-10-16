package com.clankalliance.backbeta.utils.StatusManipulateUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 为适配微信做了小部分修改
 *
 * 基于链表实现用户登陆状态的管理
 * 能够在一个程序周期内维持登陆状态
 * 更新及自动回收用户状态，并自动生成身份验证用的验证码与token
 * 身份过期时间设置整合进了application.yml 便于后期维护
 * 减少对数据库的查询
 */
@Component
public class ManipulateUtil {


    @Value("${clankToken.statusExpireTime}")
    private static int STATUS_EXPIRE_TIME;

    //空节点,有效数据节点从headNode.next开始存储
    public static StatusNode headNode;

    //endNode == headNode代表链表长度为0
    public static StatusNode endNode;

    /**
     * 初始化方法
     */
    @PostConstruct
    private void init(){
        headNode = new StatusNode();
        endNode = headNode;
    }


//废弃
    /**
     * 删除输入节点后一个节点
     * @param nodeBefore 被删除节点之前的节点
     */
    public static void deleteNextStatus(StatusNode nodeBefore){
        System.out.println(headNode);
        System.out.println(endNode);
        System.out.println(nodeBefore);
        nodeBefore.setNext(nodeBefore.getNext().getNext());
    }

    /**
     * 清理过期状态，可选择传入token或userId,会自动进行比较判断是否属于已过期状态
     * 若查到该节点发现过期则返回true,查不到则返回false
     * @param id 可以是token或userId
     */
    public static boolean deleteExpiredStatus(String id){
        long currentTime = System.currentTimeMillis();
        boolean result = false;
        while(headNode.getNext() != null && headNode.getNext().getNext() != null && currentTime - headNode.getNext().getUpdateTime() >= STATUS_EXPIRE_TIME){
            if(headNode.getNext().getToken().equals(id) || ("" + headNode.getNext().getUserId()).equals(id)){
                result = true;
            }

            headNode.setNext(headNode.getNext().getNext());
        }
        return result;
    }
    /**
     * 清理过期状态，无参方法，无返回值
     */
    public static void deleteExpiredStatus(){
        long currentTime = System.currentTimeMillis();
        while(headNode.getNext() != null && headNode.getNext().getNext() != null && currentTime - headNode.getNext().getUpdateTime() >= STATUS_EXPIRE_TIME){
            if(headNode.getNext().getNext().getToken() == null){
                headNode.setNext(null);
                endNode = headNode;
            }else{
                headNode.setNext(headNode.getNext().getNext());
            }
        }
    }

    /**
     * 新增状态，根据用户id自动生成登录状态并添加到链表末尾
     * @param userId 用户id
     */
    public static void appendStatus(String userId){

            endNode.setNext(new StatusNode(userId));
            endNode = endNode.getNext();

    }

    /**
     * 根据token查询登录状态
     * 若存在节点且没过期，则返回状态,并删除原节点。若不存在或过期，返回空节点
     * @param token 查询目标的token
     * @return
     */
    public static StatusNode findStatusByToken(String token){
        if(deleteExpiredStatus(token)){
            //token已过期并删除
            return new StatusNode();
        }else{
            //进一步判定
            StatusNode lastNode = headNode;
            StatusNode result = new StatusNode();
            StatusNode node = headNode.getNext();
            boolean find = false;
            while(node != null && !find){
                if(node.getToken().equals(token)){
                    result = node;
                    deleteNextStatus(lastNode);
                    find = true;
                }
                node = node.getNext();
            }
            return result;
        }
    }
//废弃
//    /**
//     * 【内部方法】正常使用不应调用该函数。
//     * 根据用户id查询登陆状态
//     * 若存在节点且没过期，则返回状态。若不存在或过期，返回空节点
//     * @param userId 用户id
//     * @return
//     */
//    private static StatusNode findStatusByUserId(long userId){
//        if(deleteExpiredStatus()){
//            //登录已过期并删除
//            return new StatusNode();
//        }else{
//            //进一步判定
//            StatusNode node = headNode;
//            StatusNode result = new StatusNode();
//            boolean find = false;
//            while(node.getNext() != null && !find){
//                if(node.getUserId() == userId){
//                    result = node;
//                    find = true;
//                }
//                node = node.getNext();
//            }
//            return result;
//        }
//    }

    /**
     * 输入用户id,延续该用户登录有效时间.
     * 登录时调用代表登录成功，保存状态
     * @param userId 状态更新对象id
     * @return
     */
    public static void updateStatus(String userId){
        //清理过期状态
        deleteExpiredStatus();
        //新增状态
        appendStatus(userId);

    }


}
