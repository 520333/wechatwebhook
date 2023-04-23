# wechatwebhook
## 基于prometheus+alertmanager告警通知 通过webhook接收转发告警数据到企业微信机器人
> ### 使用方法
``` shell
java -jar --url=https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxxxxx --server.port=xxx
nohup java -jar --url=https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxxxxx --server.port=xxx &
```
> ### alertmanager配置
localhost:port/webhook
```yml
webhook_configs:
- url: 'http://localhost:9000/webhook'
