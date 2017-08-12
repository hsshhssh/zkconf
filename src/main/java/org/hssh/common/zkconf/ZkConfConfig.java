package org.hssh.common.zkconf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hssh on 2017/8/8.
 */
@Configuration
public class ZkConfConfig
{
    @Bean
    public ValueBeanPostProcessor valueBeanPostProcessor()
    {
        return new ValueBeanPostProcessor();
    }

}
