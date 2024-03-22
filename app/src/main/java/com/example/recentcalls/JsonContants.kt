package com.example.recentcalls

/**
 * 真机调试时，连接PC本地服务器很难搞
 * 通过PC先获取返回的Json字符串，然后在这里定义成常量，方便调试
 *
 */
object JsonContants {
    const val RecentCall_1 = """ [{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p5","last_call_time":1681789126},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6","last_call_time":1681875670},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p7","last_call_time":1681717405}] """
    const val RecentCall_2 = """ [{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p5","last_call_time":1710944520},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6","last_call_time":1681875670},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p7","last_call_time":1710944520}] """
    const val RecentCall_3 = """ [{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o111","last_call_time":1710944520},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o222","last_call_time":1681875670},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p7","last_call_time":1710944520}] """
    const val PersonInfo_1 = """ [{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p5","name":"王二","gender":0,"phone_number":"13812345674","updated_at":1681803145},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6","name":"张三","gender":0,"phone_number":"13812345678","updated_at":1681803145},{"id":"a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p7","name":"李四","gender":1,"phone_number":"13987654321","updated_at":1681746828}] """

}