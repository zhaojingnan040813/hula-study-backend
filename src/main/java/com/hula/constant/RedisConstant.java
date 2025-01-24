package com.hula.constant;

/**
 * @Author: 赵景南
 * @Date: 2025/1/22 15:27
 * @Version: v1.0.0
 * @Description: 常量用接口或者枚举都可以
 * 如果这个项目多个地方用到Redis,建议把所有的key都定义在这个常量里
 * 这样别人想了解你的项目哪些地方用到了Redis，就可以直接来这个地方看
 * **/
public interface RedisConstant {

    /**
     * 用户签到记录的 Redis Key 前缀
     */
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins";

    /**
     * 获取用户签到记录的 Redis Key
     * @param year 年份
     * @param userId 用户 id
     * @return 拼接好的 Redis Key
     * 接口里面的方法必须是default或者 static 默认方法通过接口实例调用，而静态方法通过接口名直接调用
     */
    static String getUserSignInRedisKey(int year, long userId) { //
        return String.format("%s:%s:%s", USER_SIGN_IN_REDIS_KEY_PREFIX, year, userId);//这是模版字符串的语法
    }

}

