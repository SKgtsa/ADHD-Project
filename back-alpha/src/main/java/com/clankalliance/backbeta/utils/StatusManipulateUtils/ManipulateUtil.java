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



    private static int STATUS_EXPIRE_TIME;

    @Value("${clankToken.statusExpireTime}")
    public void setStatusExpireTime(int time){
        STATUS_EXPIRE_TIME = time;
    }

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
        if(nodeBefore.getNext().getToken().equals(endNode.getToken())){
            endNode = nodeBefore;
        }
        nodeBefore.setNext(nodeBefore.getNext().getNext());

    }

    /**
     * 清理过期状态，可选择传入token或userId,会自动进行比较判断是否属于已过期状态
     * 若查到该节点发现过期则返回true,查不到则返回false
     * @param id 可以是token或userId
     * @param deleteTarget 若目标状态即使未过期仍需要删除，输入true
     */
    public static boolean deleteExpiredStatus(String id, boolean deleteTarget){
        long currentTime = System.currentTimeMillis();
        boolean find = false;
        while(headNode.getNext() != null && headNode.getNext().getNext() != null && currentTime - headNode.getNext().getUpdateTime() >= STATUS_EXPIRE_TIME){
            if(headNode.getNext().getToken().equals(id) || headNode.getNext().getUserId().equals(id)){
                find = true;
            }

            headNode.setNext(headNode.getNext().getNext());
        }
        if(deleteTarget && !find){
            //即使未过期也需要删除目标节点并且未找到目标节点
            //进一步按照id来查找重复节点
            //进一步判定
            StatusNode lastNode = headNode;
            StatusNode node = headNode.getNext();
            while(node != null && !find){
                if(node.getToken().equals(id) || node.getUserId().equals(id)){
                    deleteNextStatus(lastNode);
                    find = true;
                }
                lastNode = node;
                node = node.getNext();
            }
        }

        return find;
    }

    //已知在登陆时，若发生在未过期时再次登录，会找不到过去的登陆状态节点

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
        if(deleteExpiredStatus(token, false)){
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
                lastNode = node;
                node = node.getNext();
            }
            return result;
        }
    }

//    /**
//     * 根据用户id查询登陆状态
//     * 若存在节点且没过期，则返回状态。若不存在或过期，返回空节点
//     * @param userId 用户id
//     * @return
//     */
//    private static StatusNode findStatusByUserId(String userId){
//        if(deleteExpiredStatus(userId)){
//            //登录已过期并删除
//            return new StatusNode();
//        }else{
//            //进一步判定
//            StatusNode lastNode = headNode;
//            StatusNode node = headNode;
//            StatusNode result = new StatusNode();
//            boolean find = false;
//            while(node.getNext() != null && !find){
//                if(node.getUserId().equals(userId)){
//                    result = node;
//                    find = true;
//                }
//                lastNode = node;
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
        if(deleteExpiredStatus(userId, true)){
            //找到了该用户过去的登陆状态并已删除

        }else {
            //该用户登录未过期或生命周期内未登陆过

        }
        //新增状态
        appendStatus(userId);

    }


}
