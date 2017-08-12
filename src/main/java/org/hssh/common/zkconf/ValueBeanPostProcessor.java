package org.hssh.common.zkconf;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

/**
 * Created by hssh on 2017/2/15.
 */
public class ValueBeanPostProcessor implements BeanPostProcessor {

    private static Logger logger = LoggerFactory.getLogger(ValueBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {

        Field[] fields = o.getClass().getDeclaredFields();
        for(Field f : fields) {
            Value annotation = f.getAnnotation(Value.class);
            if(null != annotation) {
                byte[] data = ValueChangeHandler.zkClient.readData(annotation.path());
                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
                try {
                    propertiesConfiguration.load(new ByteArrayInputStream(data), "utf-8");
                    f.setAccessible(true);
                    f.set(o, propertiesConfiguration.getProperty(annotation.key()));
                    logger.info("初始化zk变量成功:" + annotation.key() + ": " + propertiesConfiguration.getProperty(annotation.key()));

                    ValueChangeHandler.add(annotation.path(), annotation.key(), o, f);
                    ValueChangeHandler.zkClient.subscribeDataChanges(annotation.path(), ValueChangeHandler.iZkDataListener);

                } catch (ConfigurationException e) {
                    e.printStackTrace();
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }



            }
        }
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}
