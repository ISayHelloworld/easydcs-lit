# Language
- en [English](README_en.md)
- zh_CN [简体中文](README_zh.md)

# EasyDcs Lit

#### Introduction
EasyDcs Lit is a lightweight aviation departure management system developed based on javaFx. It does not need to intervene in a specific aviation network. It can complete flight management, passenger check-in, boarding, baggage management and other complete departure processes based on PNL input data.

#### 功能介绍
1、PNL data identification, support udp data integrity verification, segment identification, automatic entry into the database
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603160747.png)
2、Automatically generate the seat map of the flight model according to the standardized instructions
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603160834.png)
3、Flight process management, control flight start, suspend, end boarding | check-in process
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603160747.png)
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603161112.png)
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603162848.png)
4、User information management, including passport basic information, api information, special service items, user luggage registration, seat allocation, support batch operation registration
![输入图片说明](src/resources/img/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20230603163033.png)
5、Data security: Different airlines configure databases separately to separate airline data; private data is encrypted and stored, and passenger data is automatically deleted 3 days after the flight ends.
6、Basic settings: Chinese and English language switching, shortcut key customization, page theme switching

#### Installation instructions
It is recommended to use java1.8 version for compilation
Run Main.java to start

#### Special Note
The current version is an open source trial version Demo developed by Aeroscien Aviation Technology Co., Ltd. For details about the official commercial version, please contact the relevant personnel of the company  
Company website: http://aeroscien.com/  
E-mail: seansheng@aeroscien.com  
WeChat (same phone): 13262272231  
