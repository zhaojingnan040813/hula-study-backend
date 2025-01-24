package com.hula.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 赵景南
 * @Date: 2025/1/22 15:12
 * @Version: v1.0.0
 * @Description: 略
 **/
@Configuration
@ConfigurationProperties(prefix = "spring.redis")//要读取的配置文件的前缀
@Data
public class RedissonConfig {

    private String host;

    private Integer port;

    private Integer database;//16个库的库号

    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();//导入的是redission的包，要创建redission的包
        config.useSingleServer() //表示用单个服务器
//      redis://  它是协议，然后再叫域名，再加端口
                .setAddress("redis://" + host + ":" + port)//单击的话用这个，集群的话就是用 addNodeAddress
                .setDatabase(database)
                .setPassword(password);
        return Redisson.create(config);
    }
}
