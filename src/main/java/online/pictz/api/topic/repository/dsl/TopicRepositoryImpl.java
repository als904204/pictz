package online.pictz.api.topic.repository.dsl;

import static online.pictz.api.topic.entity.QTopic.topic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;
import online.pictz.api.topic.entity.TopicStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TopicRepositoryImpl implements TopicRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TopicResponse> findActiveTopics(TopicSort sortType, int page) {

        if (sortType == null) {
            sortType = TopicSort.LATEST;
        }

        // 최신순, 인기순
        OrderSpecifier<?> orderBy = orderByType(sortType);

        // 페이지당 항목 수
        int size = 3;
        long offset = (long) page * size;

        List<TopicResponse> queryResult = queryFactory
            .select(Projections.constructor(TopicResponse.class,
                topic.id,
                topic.suggestedTopicId,
                topic.title,
                topic.slug,
                topic.status,
                topic.thumbnailImageUrl,
                topic.createdAt
            ))
            .from(topic)
            .where(topic.status.eq(TopicStatus.ACTIVE))
            .orderBy(orderBy)
            .offset(offset)
            .limit(size)
            .fetch();

        // 총 카운트
        long total = queryFactory
            .select(topic.count())
            .from(topic)
            .where(topic.status.eq(TopicStatus.ACTIVE))
            .fetchOne();

        return new PageImpl<>(queryResult, PageRequest.of(page, size), total);
    }

    private OrderSpecifier<?> orderByType(TopicSort sortType) {
        OrderSpecifier<?> orderSpecifier = null;
        switch (sortType) {
            case POPULAR:
                orderSpecifier = topic.totalVoteCount.desc();
                break;
            case LATEST:
                orderSpecifier = topic.createdAt.desc();
                break;
        }
        return orderSpecifier;
    }
}
