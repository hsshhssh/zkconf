package org.hssh.common.zkconf;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by hssh on 2017/2/15.
 */
public class ValueBeanPostProcessor implements BeanPostProcessor {

    private static Logger logger = LoggerFactory.getLogger(ValueBeanPostProcessor.class);

    @Resource
    private ZkConfConfigProperties zkConfConfigProperties;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {

        Field[] fields = o.getClass().getDeclaredFields();
        for(Field f : fields) {
            Value annotation = f.getAnnotation(Value.class);
            if(null != annotation) {
                initValue(annotation, o, f);
            }
        }

        Method[] methods = o.getClass().getDeclaredMethods();
        for (Method m : methods)
        {
            ValueWithMethod annotation = m.getAnnotation(ValueWithMethod.class);
            if(null != annotation)
            {
                initValueWithMethod(annotation, o, m);
            }
        }

        return o;
    }

    private void initValueWithMethod(ValueWithMethod annotation, Object o , Method m)
    {

        byte[] data = ValueChangeHandler.zkClient.readData(getPath(annotation.path()));
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        try
        {
            propertiesConfiguration.load(new ByteArrayInputStream(data), "utf-8");
            m.invoke(o, propertiesConfiguration);

            ValueChangeHandler.mapMethod.put(getPath(annotation.path()), new ObjectMethod(o, m));
            ValueChangeHandler.zkClient.subscribeDataChanges(getPath(annotation.path()), ValueChangeHandler.iZkDataListener);

        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        } catch (ConfigurationException e)
        {
            e.printStackTrace();
        }

        logger.info("初始化zk监听事件 method:{}", m.getName());

    }

    private void initValue(Value annotation, Object o, Field f)
    {
        byte[] data = ValueChangeHandler.zkClient.readData(getPath(annotation.path()));
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        try {
            propertiesConfiguration.load(new ByteArrayInputStream(data), "utf-8");
            f.setAccessible(true);
            f.set(o, propertiesConfiguration.getProperty(annotation.key()));
            logger.info("初始化zk变量成功:" + annotation.key() + ": " + propertiesConfiguration.getProperty(annotation.key()));

            ValueChangeHandler.add(getPath(annotation.path()), annotation.key(), o, f);
            ValueChangeHandler.zkClient.subscribeDataChanges(getPath(annotation.path()), ValueChangeHandler.iZkDataListener);

        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

    private String getPath(String annotationPath)
    {
        if(StringUtils.isNotBlank(zkConfConfigProperties.getProjectName()) && StringUtils.isNotBlank(zkConfConfigProperties.getBizName())) {
            return "/config/" + zkConfConfigProperties.getProjectName().trim() + "/" + zkConfConfigProperties.getBizName().trim() + annotationPath;
        } else if(StringUtils.isNotBlank(zkConfConfigProperties.getProjectName())) {
            return "/config/" + zkConfConfigProperties.getProjectName().trim() + annotationPath;
        }
        return annotationPath;
    }
}
