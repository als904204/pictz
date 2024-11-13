package online.pictz.api.choice.repository.dsl;

import static online.pictz.api.choice.entity.QChoice.choice;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceCountResponse;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChoiceRepositoryImpl implements ChoiceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    /**
     * 선택지 목록 투표 수 조회
     * @param choiceIds 조회 할 선택지 id
     * @return 투표 수
     */
    @Override
    public List<ChoiceCountResponse> getChoiceTotalCounts(List<Long> choiceIds) {

        if (choiceIds == null || choiceIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory
            .select(Projections.constructor(ChoiceCountResponse.class,
                choice.id,
                choice.voteCount
            ))
            .from(choice)
            .where(choice.id.in(choiceIds))
            .fetch();
    }
}
