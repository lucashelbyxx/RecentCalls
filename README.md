# RencentCalls App

## 功能实现
1. 每次打开App时自动同步服务器“最近通话”列表，并显示在界面上；
2. 下拉刷新：向服务器请求最近通话列表，更新界面
3. 模拟拨号，新增通话记录更新界面，向服务器发送新数据，附带当前设备标识，用于服务器通知多设备同步
4. 保存数据到本地，启动时候读取，显示到界面
5. 时间规则显示

## 关键方法
获取最新通话记录，更新列表
1. 向服务器请求最近通话列表，recentCallList
2. 如果当前 callRecordList 无效（清除数据打开App的场景），向服务器请求人员信息得到 personInfoList，与recentCallList合并，生成 callRecordList ，更新列表
3. 如果当前 callRecordList 有效（存在且非空），进行匹配
  1. recentCallList匹配项，直接在callRecordList 更新通话时间
  2. 如果不匹配部分为空（即人员信息在本地都存在），callRecordList 直接排序，更新列表
  3. 如果不匹配部分非空，（即本地没有这些人员信息），则将该部分加上udpate_at 0 向服务器请求这些人员信息。获取到新增的个人信息 personInfoList，和 unmatchedList 合并，生成 updateCallRecordList（存放用于更新的Item），获取 updateCallRecordList item在callRecordList 中的位置，然后生成 updateMap<Int, CallRecord>,最后recyclerview进行局部更新
