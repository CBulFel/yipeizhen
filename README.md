# yipeizhen  
云康微信预约陪诊  
  
Version 4.0  
1.更换服务器至BAE，解决openshift idle问题的sh脚本感觉并不好使  
2.BAE的共享免费MySQL需要用mysql-connector-java-5.1.18-bin.jar，否则会出错  
3.BAE的共享免费MySQL的表名和字段名不能相同，否则出错  
4.BAE的git push会有一个问题，解决方法在txt文本  
5.mysql  
(1)authenticated_user(name,id,wechat,code,expdate,usetimes)  
(2)appointments(seq,code,name,sex,id,tel,time,hospital,department,doctor,disease)  
(3)hospitals(hospital,address)  
(4)logs(log,time)  
(5)recipient(address)  
  
Version 3.0  
1.内测第一版  
2.改为表单提交预约信息  
3.UI美化  
4.URL传值，自动填写密钥  
5.添加javascript校验信息完整性，预约日期正确性（提前一天）  
6.添加取消订单模块，前一天晚上8点前可以取消订单  
7.添加日志功能，自动记录相关日志至数据库  
8.邮件通知新增订单、新反馈意见  
9.其他一些小更新，不赘述  
10.添加自动重启脚本解决openshift长时间闲置会挂起的问题  
11.mysql  
(1)authenticated_user(name,id,wechat,code,expdate,usetimes)  
(2)appointments(seq,code,name,sex,id,tel,time,hospital,department,doctor,disease)  
(3)hospitals(hospital,address)  
(4)log(log,time)  
(5)recipient(address)  
  
Version 2.0  
1.不再使用捆绑上报客户模式，改为密钥分享模式  
2.部署在openshift上，解决中文乱码和时区问题  
3.mysql  
(1)authenticated_user(name,id,wechat,code,expdate,usetimes)  
(2)appointments(code,id,name,hospital,time)  
  
Version 1.0  
1.用户认证  
2.上报捆绑客户信息  
3.预约  
4.mysql  
（1） authenticated_user(name,id,wechat)  
（2） available_user(hostwechat,id,name)  
（3） appointments(hostwechat,id,name,hospital,time)  
  
created by 云康医生事业部实习生陈俊熙
