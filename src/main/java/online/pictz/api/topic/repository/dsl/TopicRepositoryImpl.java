package online.pictz.api.topic.repository.dsl;

import static online.pictz.api.topic.entity.QTopic.topic;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.topic.dto.TopicCountResponse;
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
    private static final int PAGE_ITEM_SIZE = 3; // 페이지당 항목 수

    @Override
    public Page<TopicResponse> findActiveTopics(TopicSort sortType, int page) {


        long offset = (long) page * PAGE_ITEM_SIZE;

        // 최신순, 인기순
        OrderSpecifier<?> orderBy = orderByType(sortType);

        List<TopicResponse> queryResult = queryFactory
            .select(Projections.constructor(TopicResponse.class,
                topic.id,
                topic.suggestedTopicId,
                topic.title,
                topic.slug,
                topic.status,
                topic.totalCount,
                topic.thumbnailImageUrl,
                topic.createdAt
            ))
            .from(topic)
            .where(topic.status.eq(TopicStatus.ACTIVE))
            .orderBy(orderBy)
            .offset(offset)
            .limit(PAGE_ITEM_SIZE)
            .fetch();

        // 토픽 총 개수
        Long total = queryFactory
            .select(topic.count().coalesce(0L))
            .from(topic)
            .where(topic.status.eq(TopicStatus.ACTIVE))
            .fetchOne();

        return new PageImpl<>(queryResult, PageRequest.of(page, PAGE_ITEM_SIZE), total);
    }

    /**
     * 페이지에 존재하는 토픽 총 투표수 조회
     * @param page 현재 페이지
     * @return 투표 수 목록
     */
    @Override
    public List<TopicCountResponse> getTopicTotalCounts(int page) {

        long offset = (long) page * PAGE_ITEM_SIZE;

        return queryFactory
            .select(Projections.constructor(TopicCountResponse.class,
                topic.id,
                topic.totalCount
            ))
            .from(topic)
            .where(topic.status.eq(TopicStatus.ACTIVE))
            .offset(offset)
            .limit(PAGE_ITEM_SIZE)
            .fetch();
    }

    @Override
    public Page<TopicResponse> searchTopics(String query, TopicSort sortType, int page) {

        long offset = (long) page * PAGE_ITEM_SIZE;

        OrderSpecifier<?> orderBy = orderByType(sortType);

        BooleanBuilder builder = searchByKeyword(query);

        List<TopicResponse> queryResult = queryFactory
            .select(Projections.constructor(TopicResponse.class,
                topic.id,
                topic.suggestedTopicId,
                topic.title,
                topic.slug,
                topic.status,
                topic.totalCount,
                topic.thumbnailImageUrl,
                topic.createdAt
            ))
            .from(topic)
            .where(topic.status.eq(TopicStatus.ACTIVE).and(builder))
            .orderBy(orderBy)
            .offset(offset)
            .limit(PAGE_ITEM_SIZE)
            .fetch();

        // 검색 조건을 포함하여 토픽 총 개수 계산
        Long total = queryFactory
            .select(topic.count().coalesce(0L))
            .from(topic)
            .where(topic.status.eq(TopicStatus.ACTIVE).and(builder))
            .fetchOne();

        return new PageImpl<>(queryResult, PageRequest.of(page, PAGE_ITEM_SIZE), total);
    }


    private OrderSpecifier<?> orderByType(TopicSort sortType) {
        OrderSpecifier<?> orderSpecifier = null;
        switch (sortType) {
            case POPULAR:
                orderSpecifier = topic.totalCount.desc();
                break;
            case LATEST:
                orderSpecifier = topic.createdAt.desc();
                break;
        }
        return orderSpecifier;
    }

    private BooleanBuilder searchByKeyword(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.trim().isEmpty()) {
            builder.and(topic.title.containsIgnoreCase(keyword));
        }
        return builder;
    }
}
