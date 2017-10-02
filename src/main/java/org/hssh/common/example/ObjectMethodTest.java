package org.hssh.common.example;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.hssh.common.zkconf.ValueWithMethod;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * Created by hssh on 2017/10/2.
 */
@Component
public class ObjectMethodTest
{

    @ValueWithMethod(path = "/config/zkconf/ad_pp_media.conf")
    public void testValueWithMethod(PropertiesConfiguration properties)
    {
        Iterator<String> keys = properties.getKeys();
        while (keys.hasNext())
        {
            String next = keys.next();
            System.out.println(next + "---------" + properties.getString(next));
        }
    }
}
