package org.hssh.common.zkconf;

import java.lang.annotation.*;

/**
 * Created by hssh on 2017/10/2.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueWithMethod
{
    String path();
}
