### intro
币安工具集

目前实现功能：

- bark模块：币价提示，在币价达到n秒变动m%时进行提示，通过钉钉群机器人进行发消息

### how to use
- 在运行参数指定：

-Dbi.api.key=币安apikey

-Dbi.git.bark.lock=git账户，用于拉取上传程序配置

-Dbi.git.bark.time=git密码，用于拉取上传程序配置

- 在配置文件中配置：

```
bi:
  git:
    bark:
      repo-name: 程序配置文件仓库名（例：bi-config）
      bark-host: 程序配置文件仓库url（例：https://github.com/supermc90/bi-config.git）
      bark-config-file-name: 程序配置文件名，需要提前在仓库创建空文件（例：bi-config.json）
      repo-path: 程序配置文件仓库拉取到本地的路径（例：/tmp/jgit/repo）
```

- 修改代码

com.mcsoft.bi.bark.ding.api.DingBotApi:21

将access_token进行替换

### web
- bark模块

简陋的币价提示配置页面：

http://localhost:9007/views/config/

增删改查

postman导入文件：bark目录下的bi.postman_collection.json