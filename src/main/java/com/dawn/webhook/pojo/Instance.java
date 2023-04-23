package com.dawn.webhook.pojo;

public class Instance {
    private String status; //告警状态
    private String alertName; //告警名称
    private String groupRegion; // 集团归属
    private String instance; // 主机IP(域名)
    private String port; // 主机端口号
    private String instanceRegion; //主机节点
    private String job; //检测类型
    private String name; // 集团名称
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

    public String getGroupRegion() {
        return groupRegion;
    }

    public void setGroupRegion(String groupRegion) {
        this.groupRegion = groupRegion;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getInstanceRegion() {
        return instanceRegion;
    }

    public void setInstanceRegion(String instanceRegion) {
        this.instanceRegion = instanceRegion;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Instance{" +
                "status='" + status + '\'' +
                ", alertName='" + alertName + '\'' +
                ", groupRegion='" + groupRegion + '\'' +
                ", instance='" + instance + '\'' +
                ", port='" + port + '\'' +
                ", instanceRegion='" + instanceRegion + '\'' +
                ", job='" + job + '\'' +
                ", name='" + name + '\'' +
                ", severity='" + severity + '\'' +
                ", description='" + description + '\'' +
                ", summary='" + summary + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", resolvedDateTime='" + resolvedDateTime + '\'' +
                '}';
    }
}
