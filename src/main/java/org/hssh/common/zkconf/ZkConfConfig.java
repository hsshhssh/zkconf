package org.hssh.common.zkconf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hssh on 2017/8/8.
 */
@Configuration
@EnableConfigurationProperties(ZkConfConfigProperties.class)
public class ZkConfConfig
{
    @Bean
    public ValueBeanPostProcessor valueBeanPostProcessor()
    {
        return new ValueBeanPostProcessor();
    }

}
