package org.hssh.common.zkconf;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * Created by hssh on 2017/10/2.
 */
@Data
public class ObjectMethod
{
    private Object object;

    private Method method;

    public ObjectMethod()
    {
    }

    public ObjectMethod(Object object, Method method)
    {
        this.object = object;
        this.method = method;
    }
}
