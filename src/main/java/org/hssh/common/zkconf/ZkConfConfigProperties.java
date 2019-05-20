package org.hssh.common.zkconf;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by hssh on 2017/2/18.
 */
@ConfigurationProperties(prefix = ZkConfConfigProperties.ZKDB_PREFIX)
public class ZkConfConfigProperties
{

    public static final String ZKDB_PREFIX = "zkconf";

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 业务名称
     */
    private String bizName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }
}
