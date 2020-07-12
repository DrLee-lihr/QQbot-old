/*
*  Copyright(C) DrLee_lihr 2020
*          Apache v2.0
* github.com/lihuoran-oier/QQbot
**/

package net.mamoe.mirai.simpleloader

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.lookup
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.join
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.content
import okhttp3.OkHttpClient
import okhttp3.Request

suspend fun loadSuccessfullyInfo(i:Group,version:String){
    i.sendMessage("Drleebot已加载 版本：$version")
    i.sendMessage("此机器人以Apache v2.0协议开源于github.com/lihuoran-oier/QQbot")
}
suspend fun onlyForTestVersionInfo(i:Group){
    i.sendMessage("这是一个仅供测试的版本，一般在修bug的过程中产生。\n放心用，我已经把鼠标放到停止键上了")
}
suspend fun versionLogOutput(i:Group,log:String){
    i.sendMessage(log)
}

suspend fun commandCheck(event:MessageEvent) {
    val info=event.message.content+"                            "
    if(info.substring(startIndex = 1,endIndex = 4)=="打拳 ")daquan(event)
    if(info.substring(startIndex = 1,endIndex = 5)=="fdj ")fdj(event)
    if(info.substring(startIndex = 1,endIndex = 5)=="yxh ")yxh(event)
    if(info.substring(startIndex = 1,endIndex = 6)=="wiki ")wiki(event)
    if(info.substring(startIndex = 1,endIndex = 6)=="help ")help(event)
    if(info.substring(startIndex = 1,endIndex = 7)=="mcbbs ")mcbbs(event)
}

suspend fun daquan(event:MessageEvent){
    if(event.message.content.substring(startIndex = 4)=="-h"){
        event.subject.sendMessage("\\打拳 [文本] - 输出一条打拳信息。\n" +
                "\\打拳 -h - 获取这条帮助信息。")
    }
    else
        event.subject.sendMessage(
            "炎热的夏天我气得浑身发抖手脚冰凉泪流满面，" +
            "我们${event.message.content.substring(startIndex = 4)}怎么样才能让你们满意，" +
            "我们${event.message.content.substring(startIndex = 4)}什么时候才能站起来，" +
            "什么时候我们${event.message.content.substring(startIndex = 4)}才能好起来，" +
            "偌大的国度我只看到深深的压迫和无边的黑暗，" +
            "我只想要逃离")
}

suspend fun fdj(event:MessageEvent){

    val temp0=event.message.content.split(" ")
    val len=temp0.count()
    if(temp0.last()=="-h"){
        event.subject.sendMessage("""
            \fdj [文本] -f [次数] - 重复发送[文本]共[次数]次。
            \fdj [文本] [次数] - 将[文本]重复[次数]遍一起发送。
            \fdj -h - 获取这条帮助信息。
        """.trimIndent())
        return
    }
    val temp1=event.message.content.substring(5,
            if(temp0[len-2]=="-f")(event.message.content.length-(temp0.last().length+4))
                else(event.message.content.length-(temp0.last().length+1)))
    if(temp0[len-2]=="-f"){
        if(temp0[len-1].toInt()>=5)event.subject.sendMessage("这人要刷屏，管理员快来禁言")
        else{
            for (i in 1..temp0[len-1].toInt() step 1) {
                event.subject.sendMessage(temp1)
            }
        }
        return
    }
    else{
        var temp2:String=""
        for (i in temp0[len-1].toInt() downTo 1 step 1){
            temp2+=temp1
        }
        event.subject.sendMessage(temp2)
        return
    }
    return
}

suspend fun wiki(event: MessageEvent){
    val pageName=event.message.content.substring(6)
    if(pageName=="-h"){
        event.subject.sendMessage("\\wiki [标题] - 查询中文Minecraft Wiki上的页面[标题]。")
        return
    }
    val host = "https://minecraft-zh.gamepedia.com/api.php?action=query&format=json" +
            "&prop=info&inprop=url&redirects&titles=${pageName}"
    val client=OkHttpClient()
    val request= Request.Builder()
        .url(host)
        .get()
        .build()
    val call=client.newCall(request)
    val response = call.execute()
    if(response.isSuccessful){
        val body=response.body()
        val string=body?.string()
        val parser: Parser = Parser.default()
        println(string)
        var stringParser=parser.parse(StringBuilder(string)) as JsonObject
        var query=stringParser.lookup<JsonObject>("query")
        var redirects=query.lookup<String>("redirects")
        var pages=query.lookup<JsonObject>("pages")
        var pageNumber=-1
        var useRedirects:Boolean=false
        for(i in 0..(string?.length!!.minus(10))){
            if(string.substring(i,i+5)=="pages") {
                println(i)
                for(j in (i+9)..(i+20)){
                    print(j)
                    print(","+string.substring(i+9,j))
                    print(",")
                    println(string.substring(i+9,j).endsWith("\":{"))
                    if(string.substring(i+9,j).endsWith("\":{")){
                        pageNumber=string.substring(i+9,j-3).toInt()
                    }
                }
            }
            if(!useRedirects){
                if (string.substring(i,i+9)=="redirects"){
                    useRedirects=true
                }
            }
        }
        if(pageNumber==-1){
            event.subject.sendMessage("未找到$pageName")
            return
        }
        println(pageNumber)
        var pageThis=pages.lookup<JsonObject>(pageNumber.toString())
        println(pageThis.toJsonString())
        event.subject.sendMessage("您要的"+pageThis["title"].value+":\n"+
                (if(useRedirects)("("+redirects["from"].value+"->"+redirects["to"].value+")\n")else(""))+
                pageThis["fullurl"].value)
    }
    else{
        event.subject.sendMessage("发生错误：土豆熟了。")
    }
}

suspend fun yxh(event:MessageEvent){
    val contentList : List<String> = event.message.content.substring(5).split(" ")
    val listLength : Int = contentList.size
    if(contentList[0]=="-h"){
        event.subject.sendMessage("\\yxh [信息1] - 输出一个营销号信息。\n\\yxh [信息1] [信息2] - 输出一个营销号信息。")
        return
    }
    when {
        listLength>=3 -> {
            event.subject.sendMessage("爬.jpg")
            return
        }
        listLength<=0 -> {
            event.subject.sendMessage("爬.jpg")
            return
        }
        listLength==1 -> {
            val a=contentList[0]
            event.subject.sendMessage("${a}是我们生活中所经常用到的东西，但很多人还不知道${a}是什么" +
                    "，${a}的意思，${a}怎么用，${a}的来源出处，${a}的典故，${a}的方法，${a}的方式，为什么大家都在用${a}，" +
                    "那么小编就来给大家详细介绍一下${a}是什么，${a}的意思，${a}怎么用，${a}的来源，${a}，${a}的方法，${a}的方式，" +
                    "为什么大家都在用${a}，相信不少同学都很想了解，那么就快来跟小编一起来看看吧。希望大家看完这篇由小编精心整理的内容后，" +
                    "能对相关知识有所了解，解决你的困惑。 好了，以上就是有关${a}的全部内容，" +
                    "希望您在阅读完小编精心整理的这篇文章后能够有所收获。")
            return
        }
        listLength==2 -> {
            val b=contentList[0]
            val c=contentList[1]
            event.subject.sendMessage("""
                ${c}是怎么回事呢？${b}相信大家都很熟悉，但是${c}是怎么回事呢？下面就让小编带大家了解一下吧。
                其实${b}就是${c}了，大家可能会感到很惊讶，${b}为什么会沦落到如此地步了呢？但事实就是这样，小编也感到非常惊讶。
                那么这就是关于${c}的事情了，大家有什么想法呢？欢迎在评论区告诉小编一起讨论哦。
            """.trimIndent())
            return
        }
    }
}

suspend fun mcbbs(event:MessageEvent){
    val info=event.message.content.substring(7)
    if(info=="-h"){
        event.subject.sendMessage("\\mcbbs [内容] - 输出一个带site:mcbbs.net的百度搜索链接。")
    }
    else {
        event.subject.sendMessage("https://www.baidu.com/s?ie=UTF-8&wd=site%3Amcbbs.net%20$info")
    }
}

suspend fun help(event:MessageEvent){
    event.subject.sendMessage("""
        命令帮助索引：
        \fdj -h
        \打拳 -h
        \wiki -h
        \yxh -h
        \mcbbs -h
    """.trimIndent())
}

suspend fun main() {
    val qqId = 465993851L//Bot的QQ号，需为Long类型，在结尾处添加大写L
    val password = "hr812403"//Bot的密码
    val DrleeBot = Bot(qqId, password){ fileBasedDeviceInfo() }.alsoLogin()//新建Bot并登录
    val mcwzhTeaHouseGroup = DrleeBot.getGroup(657876815)
    val robotDebugGroup=DrleeBot.getGroup(738829671)
    val chinoDayDreamCafe=DrleeBot.getGroup(249787728)
    val abuseBotGroup=DrleeBot.getGroup(1044813316)
    val isTest=true
    loadSuccessfullyInfo(abuseBotGroup,"Beta v0.0.8")


    //mcwzhTeaHouseGroup.sendMessage("""
    //    Beta v0.0.2:
    //    ~fdj功能已上线，正在测试中。使用~fdj -h来获取更多信息。
    //""".trimIndent())

    DrleeBot.subscribeAlways<MessageEvent> { event->
        if(isTest)if(event.subject==abuseBotGroup)if(event.message.content[0]=='\\')commandCheck(event)
        if(event.subject==chinoDayDreamCafe){
            if(event.message.content=="fbk!"){
                event.subject.sendMessage("@"+event.senderName+"bksn又在调戏机器人")
            }
        }
    }
    /*
    DrleeBot.subscribeAlways<MemberJoinRequestEvent> { event->
        if (event.group==mcwzhTeaHouseGroup) {
            if (event.message=="被虫蚀的石砖"){
                event.accept()
            }
            else{
                mcwzhTeaHouseGroup.sendMessage(event.fromNick+"提交了入群申请："+event.message)
                mcwzhTeaHouseGroup.sendMessage("太草了哈哈哈哈（罐头笑声")
            }
        }
    }
    */
    DrleeBot.subscribeAlways<MemberJoinEvent> {event->
        if(event.group==mcwzhTeaHouseGroup)mcwzhTeaHouseGroup.sendMessage("新人看群规看群规改名片改名片")
    }
    DrleeBot.join() // 等待 Bot 离线, 避免主线程退出
}

