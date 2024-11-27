package online.pictz.api.topic.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.pictz.api.common.dto.PagedResponse;
import online.pictz.api.topic.dto.TopicCountResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;
import online.pictz.api.topic.repository.TopicRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopicServiceImpl implements TopicService{

    private final TopicRepository topicRepository;

    /**
     * 페이지에 해당하는 토픽 조회
     *
     * @param sortType 정렬 조건
     * @param page     현재 페이지
     * @return 토픽 목록
     */
    @Transactional(readOnly = true)
    @Override
    @Cacheable(
        value = "popularTopics",
        key = "'POPULAR-' + #page", // 캐시 키: 'POPULAR-' + 페이지 번호
        condition = "#sortType == T(online.pictz.api.topic.entity.TopicSort).POPULAR && #page < 5" // POPULAR 정렬일 때만 캐시 적용
    )
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

    /**
     * 토픽 총 투표 수 조회
     * @param page 현재 페이지
     * @return 토픽 투표 수
     */
    @Transactional(readOnly = true)
    @Override
    public List<TopicCountResponse> getAllTopicCounts(int page) {
        return topicRepository.getTopicTotalCounts(page);
    }

    /**
     * 토픽 검색
     * @param query 검색 쿼리
     * @param sortBy 정렬 조건
     * @param page 현재 페이지
     * @return 토픽 검색 결과
     */
    @Transactional(readOnly = true)
    @Override
    public PagedResponse<TopicResponse> searchTopics(String query, TopicSort sortBy, int page) {
        Page<TopicResponse> queryResult = topicRepository.searchTopics(query, sortBy, page);
        return new PagedResponse<>(
            queryResult.getContent(),
            queryResult.getNumber(),
            queryResult.getSize(),
            queryResult.getTotalElements(),
            queryResult.getTotalPages(),
            queryResult.isLast()
        );
    }

    /**
     * 토픽의 총 투표수 업데이트
     *
     * topicId
     * @param topicVoteMap (topicId : count)
     */
    @Override
    public void updateTopicTotalCounts(Map<Long, Integer> topicVoteMap) {
        for (Map.Entry<Long, Integer> e : topicVoteMap.entrySet()) {
            Long topicId = e.getKey();
            Integer voteTotalCount = e.getValue();
            int updatedRows = topicRepository.incrementTotalCount(topicId, voteTotalCount);
            if (updatedRows == 0) {
                throw new OptimisticLockingFailureException("Failed to update totalCount for Topic ID: " + topicId);
            }
        }
    }

}
