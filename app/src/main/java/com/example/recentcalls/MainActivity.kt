package com.example.recentcalls

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.recentcalls.JsonContants.PersonInfo_1
import com.example.recentcalls.JsonContants.RecentCall_1
import com.example.recentcalls.JsonContants.RecentCall_2
import com.example.recentcalls.JsonContants.RecentCall_3
import com.example.recentcalls.utils.ParseData.parsePersonInfo
import com.example.recentcalls.utils.ParseData.parseRecentCall
import com.example.recentcalls.utils.Utils.timeStamp2DateStr
import com.example.recentcalls.data.CallRecord
import com.example.recentcalls.data.CallRecordCacheManager
import com.example.recentcalls.data.PersonInfo
import com.example.recentcalls.data.PostInfo
import com.example.recentcalls.data.RecentCall
import com.example.recentcalls.databinding.ActivityMainBinding
import com.example.recentcalls.utils.Utils.generateRandomCallRecords
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var sortedCallRecordList: List<CallRecord>
    private lateinit var cachedCallRecordList: List<CallRecord>

    private lateinit var recentCallList : List<RecentCall>
    lateinit var personInfoList : List<PersonInfo>
    private var callRecordList : List<CallRecord> = emptyList()
    private lateinit var binding: ActivityMainBinding
    private var alreadyGetRecentCallList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewManager = LinearLayoutManager(this)
        recyclerView = binding.recyclerView.apply { setHasFixedSize(true)
            layoutManager = viewManager }

        // 下拉刷新，在监听器中发送请求并更新 RecyclerView
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            getNewRecords(RecentCall_2)
        }

        // 模拟新增通话记录，向服务器发送新数据，附带当前设备标识，通知多设备同步
        binding.button.setOnClickListener {
            var newCall = generateRandomCallRecords(1)[0]
            callRecordList = callRecordList.plus(newCall)

            callRecordList = callRecordList.sortedByDescending { it.last_call_time }

            viewAdapter = CallRecordAdapter(callRecordList)
            recyclerView.adapter = viewAdapter

            // IO 协程，发送新增的通话记录到服务器
            // HttpClient.post 需要加上用户设备标识，以便服务器识别
        }

        //启动一个协程
        lifecycleScope.launch(Dispatchers.IO) {
            // 从本地读取数据
            cachedCallRecordList = CallRecordCacheManager.getCallRecordList(this@MainActivity)

            launch(Dispatchers.Main) {
                viewAdapter = CallRecordAdapter(cachedCallRecordList)
                recyclerView.adapter = viewAdapter
            }
        }

        //打开App自动同步服务器 “最近通话” 列表
        getNewRecords(RecentCall_1)
        alreadyGetRecentCallList = true
    }

    override fun onResume() {
        super.onResume()

        if (!alreadyGetRecentCallList) {
            getNewRecords(RecentCall_1)
            alreadyGetRecentCallList = false
        }

    }

    override fun onPause() {
        super.onPause()

        // 保存数据到本地
        CallRecordCacheManager.saveCallRecordList(this, callRecordList)
    }

    // 获取最新通话记录，更新列表
    private fun getNewRecords(analogJsonStr: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // 模拟 delay 1000ms
            delay(1000L)

            // 获取最近通话列表
            //HttpClient.get("http://127.0.0.1:8080/contacts/conversation")

            recentCallList = parseRecentCall(analogJsonStr)

            val gson = Gson()
            var json: String

            //如果当前的callRecordList不为空且不为NULL，则进行匹配
            //匹配项直接更新时间
            //不匹配项表示本地没有该人员信息，需要加上update_at字段生成列表并转换Json向服务器请求个人信息
            if (!callRecordList.isNullOrEmpty()) {
                // 匹配项直接更新时间
                for (recentCall in recentCallList) {
                    for (callRecord in callRecordList) {
                        if (recentCall.id == callRecord.id) {
                            callRecord.last_call_time = recentCall.last_call_time
                            callRecord.callTime = timeStamp2DateStr(recentCall.last_call_time)
                        }
                    }
                }

                // 根据 recentCallList 中的 id 字段在 callRecordList 中查找不匹配的部分，并生成新的列表(反过来匹配不好)
                val unmatchedList1 = recentCallList.filter { recentCall ->
                    callRecordList.none { callRecord ->
                        callRecord.id == recentCall.id
                    }
                }.map { unmatchedCall ->
                    RecentCall(unmatchedCall.id , unmatchedCall.last_call_time)
                }

                // 没有匹配项，生成Json对应列表加上update_at字段
                val unmatchedList = recentCallList.filter { recentCall ->
                    callRecordList.none { callRecord ->
                        callRecord.id == recentCall.id
                    }
                }.map { unmatchedCall ->
                    PostInfo(unmatchedCall.id , 0)
                }

                if (unmatchedList.isEmpty()) {
                    //显示前排序
                    callRecordList = callRecordList.sortedByDescending { it.last_call_time }

                    withContext(Dispatchers.Main) {
                        viewAdapter = CallRecordAdapter(callRecordList)
                        recyclerView.adapter = viewAdapter
                        swipeRefreshLayout.isRefreshing = false
                    }
                } else {//不匹配部分非空，（即本地没有这些人员信息）
                    json = gson.toJson(unmatchedList)

                    // 获取联系人信息,解析到 personInfoList
                    // HttpClient.post("http://127.0.0.1:8080/contacts/users",json )
                    personInfoList = parsePersonInfo(PersonInfo_1)

                    //获取到新增的个人信息，和unmatchedList合并，然后更新callRecordList
                    val personInfoMap = personInfoList.associateBy { it.id }
                    val mergedList = unmatchedList1.mapNotNull { recentCall ->
                        personInfoMap[recentCall.id]?.let { personInfo ->
                            Pair(recentCall, personInfo)
                        }
                    }

                    var updateCallRecordList = mergedList.map { (recentCall, personInfo) ->
                        CallRecord(
                            id = recentCall.id,
                            avatar = R.drawable.ic_launcher_background, // You need to provide a way to get the avatar
                            name = personInfo.name,
                            phoneNumber = personInfo.phone_number,
                            callTime = timeStamp2DateStr(recentCall.last_call_time),
                            last_call_time = recentCall.last_call_time
                        )
                    }

                    //获取 updateCallRecordList item在callRecordList 中的位置生成 updateMap<Int, CallRecord>,然后局部更新
                    var tempList = callRecordList
                    for (temp in updateCallRecordList) {
                        tempList = tempList.plus(temp)
                    }
                    val sortedTempList = tempList.sortedByDescending { it.last_call_time }
                    var updateMap = HashMap<Int, CallRecord>()
                    for ((index, item) in updateCallRecordList.withIndex()) {
                        for (sorted in sortedTempList) {
                            if (item.id == sorted.id) {
                                updateMap += (index to item)
                            }
                        }
                    }

                    //局部更新
                    for (mutableEntry in updateMap) {
                        insertDataAtPosition(mutableEntry.value, mutableEntry.key, viewAdapter as CallRecordAdapter)
                    }

                }

            } else {    //没有本地数据，直接向服务器请求个人信息，recentCallList+update_at
                json = gson.toJson(recentCallList)

                // 向服务器请求个人信息,解析到 list
                // HttpClient.post("http://127.0.0.1:8080/contacts/users",json )
                personInfoList = parsePersonInfo(PersonInfo_1)

                //合并list
                val personInfoMap = personInfoList.associateBy { it.id }
                val mergedList = recentCallList.mapNotNull { recentCall ->
                    personInfoMap[recentCall.id]?.let { personInfo ->
                        Pair(recentCall, personInfo)
                    }
                }

                callRecordList = mergedList.map { (recentCall, personInfo) ->
                    CallRecord(
                        id = recentCall.id,
                        avatar = R.drawable.ic_launcher_background,
                        name = personInfo.name,
                        phoneNumber = personInfo.phone_number,
                        callTime = timeStamp2DateStr(recentCall.last_call_time),
                        last_call_time = recentCall.last_call_time
                    )
                }

                callRecordList = callRecordList.sortedByDescending { it.last_call_time }

                withContext(Dispatchers.Main) {
                    viewAdapter = CallRecordAdapter(callRecordList)
                    recyclerView.adapter = viewAdapter
                    swipeRefreshLayout.isRefreshing = false
                }
            }

        }

    }


    // 在指定位置插入新数据
    private fun insertDataAtPosition(newData: CallRecord, position: Int, adapter: CallRecordAdapter) {
        adapter.getDataList().add(position, newData)

        runOnUiThread {
            adapter.notifyItemInserted(position)
        }

    }

}


fun main() {

    var recentCallList = parseRecentCall(RecentCall_2)
    val sortedRecentCallList = recentCallList.sortedByDescending { it.last_call_time }


    var personInfoList = parsePersonInfo(PersonInfo_1)
    val personInfoMap = personInfoList.associateBy { it.id}
    val mergedList = sortedRecentCallList.mapNotNull { recentCall ->
        personInfoMap[recentCall.id]?.let { personInfo ->
            Pair(recentCall, personInfo)
        }
    }


    var callRecordList = mergedList.map { (recentCall, personInfo) ->
        CallRecord(
            id = recentCall.id,
            avatar = 0, // You need to provide a way to get the avatar
            name = personInfo.name,
            phoneNumber = personInfo.phone_number,
            callTime = timeStamp2DateStr(recentCall.last_call_time),
            last_call_time = recentCall.last_call_time
        )
    }

    println("mergedList:")
    for (pair in mergedList) {
        println(pair)
    }

    println("callRecordList:")
    for (pair in callRecordList) {
        println(pair)
    }
}