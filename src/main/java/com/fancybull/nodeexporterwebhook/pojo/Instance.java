package com.fancybull.nodeexporterwebhook.pojo;

public class Instance {
    private String status; //告警状态
    private String alertName; //告警名称
    private String instance; // 主机IP(域名)
    private String job; //检测类型
    private String severity; // 告警等级
    private String description; //详细描述
    private String summary; //概括
    private String startDateTime; //开始日期
    private String resolvedDateTime; //恢复日期

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }


    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }


    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }


    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getResolvedDateTime() {
        return resolvedDateTime;
    }

    public void setResolvedDateTime(String resolvedDateTime) {
        this.resolvedDateTime = resolvedDateTime;
    }
}
