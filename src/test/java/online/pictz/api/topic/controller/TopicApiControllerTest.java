package online.pictz.api.topic.controller;


import online.pictz.api.choice.service.ChoiceService;
import online.pictz.api.topic.service.TopicService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicApiControllerTest {


    @Mock
    private TopicService topicService;

    @Mock
    private ChoiceService choiceService;

    @InjectMocks
    private TopicApiController topicApiController;


}