# wechatwebhook
## 基于prometheus+alertmanager发送到企业微信WebHook 接收转发alertManager告警数据
### 使用方法
``` shell
java -jar --url=https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxxxxx --server.port=xxx
nohup java -jar --url=https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxxxxxxx --server.port=xxx &
```
监听在localhost:port/webhook

