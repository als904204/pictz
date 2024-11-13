package online.pictz.api.topic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopicSuggestPageController {

    @GetMapping("/suggest")
    public String topicSuggestCreate() {
        return "suggest";
    }

    @GetMapping("/topic-suggests/{id}")
    public String getTopicSuggestDetail() {
        return "suggest-detail";
    }

}
