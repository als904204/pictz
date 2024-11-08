package online.pictz.api.topic.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.pictz.api.common.dto.PagedResponse;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;
import online.pictz.api.topic.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopicServiceImpl implements TopicService{

    private final TopicRepository topicRepository;
    private final TimeProvider timeProvider;


    @Deprecated
    @Override
    public List<TopicResponse> findAll() {
        return topicRepository.findActiveTopics()
            .stream()
            .map(TopicResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResponse<TopicResponse> getActiveTopics(TopicSort sortType, int page) {
        Page<TopicResponse> topicPage = topicRepository.findActiveTopics(sortType, page);
        return new PagedResponse<>(
            topicPage.getContent(),
            topicPage.getNumber(),
            topicPage.getSize(),
            topicPage.getTotalElements(),
            topicPage.getTotalPages(),
            topicPage.isLast()
        );
    }

}
