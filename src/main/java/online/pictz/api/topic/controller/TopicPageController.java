package online.pictz.api.topic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TopicPageController {

    @GetMapping("/topics/{slug}")
    public String getTopicPage(@PathVariable("slug") String slug) {
        return "choice";
    }

}
