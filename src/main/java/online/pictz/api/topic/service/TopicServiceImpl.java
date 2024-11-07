package online.pictz.api.topic.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopicServiceImpl implements TopicService{

    private final TopicRepository topicRepository;
    private final TimeProvider timeProvider;


    /**
     * 모든 토픽 조회
     * TODO: 추후 페이징으로 변경
     * @return 토픽 목록
     */
    @Override
    public List<TopicResponse> findAll() {
        return topicRepository.findAll()
            .stream()
            .map(TopicResponse::from)
            .collect(Collectors.toList());
    }

}
