package org.hssh.common.zkconf;

import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hssh on 2017/2/15.
 */
public class ValueChangeHandler {

    public static Logger logger = LoggerFactory.getLogger(ValueChangeHandler.class);

    public static SetMultimap<String, ObjectField> map = HashMultimap.create();

    public static SetMultimap<String, ObjectMethod> mapMethod = HashMultimap.create();

    public static ZkClient zkClient = new ZkClient(System.getenv("ZK_HOST"));

    public static IZkDataListener iZkDataListener = new IZkDataListener() {
        @Override
        public void handleDataChange(String dataPath, byte[] data) throws Exception {
            handlerValue(dataPath, data);

            handlerVauleWithMethod(dataPath, data);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {

        }
    };


    private static void handlerVauleWithMethod(String dataPath, byte[] data) throws ConfigurationException, InvocationTargetException, IllegalAccessException
    {
        Set<ObjectMethod> objectMethods = mapMethod.get(dataPath);
        for (ObjectMethod objectMethod : objectMethods)
        {
            objectMethod.getMethod().invoke(objectMethod.getObject(), new ByteArrayInputStream(data));
            logger.info("调用zkconf注册方法 method:{}", objectMethod.getMethod().getName());
        }
    }

    /**
     * 处理key值的配置
     */
    private static void handlerValue(String dataPath, byte[] data) throws ConfigurationException, IllegalAccessException
    {
        PropertiesConfiguration properties = new PropertiesConfiguration();
        properties.load(new ByteArrayInputStream(data), "utf-8");
        Iterator<String> is = properties.getKeys();
        while (is.hasNext()) {
            String key = dataPath + "/" + is.next();
            Set<ObjectField> set = map.get(key);
            for(ObjectField ob : set) {
                ob.getField().setAccessible(true);
                Value annotation = ob.getField().getAnnotation(Value.class);
                ob.getField().set(ob.getObject(), properties.getProperty(annotation.key()));
                logger.info("修改变量成功 key:{} value:{}", annotation.key(), properties.getProperty(annotation.key()));
            }

        }
    }

    public static void add(String path, String key, Object object, Field field)
    {
        StringBuffer pathKey = new StringBuffer();
        pathKey.append(path);
        pathKey.append("/");
        pathKey.append(key);

        map.put(pathKey.toString(), new ObjectField(object, field));
    }


}
