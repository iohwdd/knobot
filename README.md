# 开发中

开发QQ群：970315394

## 环境配置
1. 大模型api-key获取：阿里百炼平台 https://bailian.console.aliyun.com/?spm=5176.29597918.J_SEsSjsNv72yRuRFS2VknO.2.635b7ca0Mz7cuE&tab=model#/api-key

2. 搜索引擎api-key获取：Searchapi（免费额度100次） https://www.searchapi.io/

3. 向量数据库pgvector安装： `docker pull ankane/pgvector` 创建库名为`vecdb`，向量表在成功启动时自动创建。以上三点内容的配置信息统一在`application-ai.yml`中配置。

4. mysql初始化：脚本位于`knobot-service/src/main/resources/init.sql`

5. oss对象存储：创建好Bucket与密钥对 https://ram.console.aliyun.com/profile/access-keys?spm=5176.7933691.nav-v2-dropdown-my-aliyun.5.29852c47zR5EjH ,配置好`application-oss.yml`

6. 前端启动：代码在 https://github.com/iohwdd/knobot_frontend ，依次执行`npm install` `npm run dev` 即可

# 页面效果展示
整个项目还在开发过程中，功能尚未完善，存在许多问题，期待大家指正，更期待参与fork贡献😊
![image](https://github.com/user-attachments/assets/938dd639-7588-4e07-9e2b-d8192c80d28a)

![image](https://github.com/user-attachments/assets/f2889211-19d1-4e1d-af5e-ec557f96033d)

![image](https://github.com/user-attachments/assets/bfab6b1c-f6a3-4124-832d-c9f1420438ee)

![image](https://github.com/user-attachments/assets/c0c508ba-2f0d-441f-a863-818bf9606d1b)

