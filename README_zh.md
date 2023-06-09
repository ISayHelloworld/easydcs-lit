# Language
- en [English](README_en.md)
- zh_CN [简体中文](README_zh.md)

# EasyDcs Lit

#### 介绍
EasyDcs Lit是一款基于javaFx开发的轻量级航空离岗管理系统，无需介入特定航空网络，基于PNL录入数据即可完整航班管理，乘客值机，登机，行李管理等完整离岗流程。

#### 功能介绍
1、PNL数据识别，支持udp数据完整性校验，分段识别，自动录入数据库
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603160747.png)
2、根据规范化指令自动生成航班机型座位图
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603160834.png)
3、航班流程管理，控制航班启动，暂停，结束 登机|值机 流程
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603160747.png)
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603161112.png)
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603162848.png)
4、用户信息管理，包含护照基本信息，api信息，特殊服务项，用户行李登记，座位分配，支持批量操作登记
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603163033.png)
5、数据安全：不同航司单独配置数据库，实现航司数据分隔；隐私数据加密存储，航班结束后3天后自动删除乘客数据。
6、基本设置：中英语言切换，快捷键自定义，页面主题切换

#### 安装使用说明
建议使用java1.8版本进行编译  
运行Main.java即可启动

#### 特别说明
当前版本为 Aeroscien航技嘉立有限公司研发开源试用版Demo，正式商业版详情请与公司相关人员进行联系  
公司网站：http://aeroscien.com/  
邮箱: seansheng@aeroscien.com  
微信(同电话）：13262272231  
