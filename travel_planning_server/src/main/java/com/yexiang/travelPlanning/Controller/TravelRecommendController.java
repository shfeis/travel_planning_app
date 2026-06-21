package com.yexiang.travelPlanning.Controller;

import com.yexiang.travelPlanning.Entity.Recommend;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 旅游规划和AI聊天
 */
@RestController
@RequestMapping("recommend")
public class TravelRecommendController {
    private final ChatModel chatModel;

    //ChatModel不能使用@Resource注入，只能使用构造器进行注入
    public TravelRecommendController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    //旅游规划详情
    @PostMapping({"/planning"})
    public String planning(@RequestBody Recommend recommend) {
        //告知AI的身份
        SystemMessage systemMessage = new SystemMessage("你的名字叫小智，是一个智能的景点介绍和行程规划助手；返回的内容与顺序(目的地、天数、预算、每天具体行程、预算细明、温馨提示)，预算细明内容(住宿、餐饮、交通、门票、其他、总计)");
        //发送给AI的问题
        String query = "目的地是：" + recommend.getCity() + "，预算：" + recommend.getBudget() + "元，旅游天数：" + recommend.getDays()+"天，预算细明只要金额不要进行细节说明，温馨提示必须返回";
        UserMessage userMessage = new UserMessage(query);
        //将用户问题发送给AI,并返回解答
        return chatModel.call(systemMessage, userMessage);
    }

    //AI聊天功能
    @GetMapping({"/chat"})
    public Flux<String> aiChat(String query) {
        //告知AI的身份
        SystemMessage systemMessage = new SystemMessage("你的名字叫小智，是一个智能的景点介绍和行程规划助手");
        //发送给AI的问题
        UserMessage userMessage = new UserMessage(query);
        //将用户问题发送给AI,并返回解答
        return chatModel.stream(systemMessage, userMessage);
    }
}
