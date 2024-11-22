package online.pictz.api.vote.service.test;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.NonNull;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.vote.entity.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class VoteBatchProcessor {

    private static final Logger log = LoggerFactory.getLogger(VoteBatchProcessor.class);

    private final JdbcTemplate jdbcTemplate;
    private final TimeProvider timeProvider;

    public VoteBatchProcessor(JdbcTemplate jdbcTemplate, TimeProvider timeProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.timeProvider = timeProvider;
    }

    /**
     * UPDATE choice SET count = CASE id WHEN 1 THEN count + 10 WHEN 2 THEN count + 20 ... END WHERE
     * id IN (1, 2)
     */
    public void updateChoices(Map<Long, Integer> existsChoicesMap) {

        log.info("Starting updateChoices with {} entries, TIME {}", existsChoicesMap.size(),timeProvider.getCurrentTime());

        StringBuilder sb = new StringBuilder();
        StringJoiner joiner = new StringJoiner(", ");

        List<Object> params = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        sb.append("UPDATE choice SET count = CASE id ");

        for (Map.Entry<Long, Integer> entry : existsChoicesMap.entrySet()) {
            Long choiceId = entry.getKey();
            int count = entry.getValue();
            sb.append("WHEN ").append(choiceId).append(" THEN count + ? ");
            params.add(count);
            ids.add(choiceId);
        }

        sb.append("END WHERE id IN (");
        for (Long id : ids) {
            joiner.add("?");
        }

        sb.append(joiner).append(")");

        params.addAll(ids);
        String sql = sb.toString();
        try {
            jdbcTemplate.update(sql, params.toArray());
            log.info("Successfully updated {} choices", existsChoicesMap.size());
        } catch (Exception e) {
            log.error("Failed to update choices with ids {}. Error: {}", ids, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * UPDATE topic SET total_count = CASE id WHEN 1 THEN total_count + 100 WHEN 2 THEN total_count
     * + 200 END  WHERE id IN (1, 2)
     */
    public void updateTopic(Map<Long, Integer> existsChoicesMap, List<Choice> choices) {
        log.info("Starting updateTopic with {} entries and {} choices, TIME {}",
            existsChoicesMap.size(), choices.size(), timeProvider.getCurrentTime());

        // [choiceID : topicID]
        Map<Long, Long> choiceToTopic = choices.stream()
            .collect(Collectors.toMap(
                Choice::getId,
                Choice::getTopicId
            ));

        Map<Long, Integer> topicTotalCount = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : existsChoicesMap.entrySet()) {
            Long choiceId = entry.getKey();
            Integer count = entry.getValue();
            Long topicId = choiceToTopic.get(choiceId);

            if (topicId != null) {
                topicTotalCount.merge(topicId, count, Integer::sum);
            }
        }

        StringBuilder sb = new StringBuilder();
        StringJoiner joiner = new StringJoiner(", ");

        List<Object> params = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        sb.append("UPDATE topic SET total_count = CASE id ");
        for (Map.Entry<Long, Integer> entry : topicTotalCount.entrySet()) {
            Long topicId = entry.getKey();
            Integer count = entry.getValue();

            sb.append("WHEN ").append(topicId).append(" THEN total_count + ? ");
            params.add(count);

            ids.add(topicId);
        }

        sb.append("END WHERE id IN (");
        for (Long id : ids) {
            joiner.add("?");
        }

        sb.append(joiner).append(")");
        params.addAll(ids);
        String sql = sb.toString();
        try {
            jdbcTemplate.update(sql, params.toArray());
            log.info("Successfully updated {} topics", topicTotalCount.size());
        } catch (Exception e) {
            log.error("Failed to update topics with ids {}. Error: {}", ids, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * INSERT INTO vote (choice_id, count, voted_at, ip) VALUES (?, ?, ?, ?), (?, ?, ?, ?)...
     */
    public void insertVoteRecords(Map<Long, Integer> existsChoicesMap,
        LocalDateTime votedAt) {

        log.info("Starting insertVoteRecords with {} entries, TIME: {}", existsChoicesMap.size(), votedAt);

        List<Vote> votes = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : existsChoicesMap.entrySet()) {
            Long choiceId = entry.getKey();
            Integer count = entry.getValue();
            votes.add(
                Vote.builder()
                    .choiceId(choiceId)
                    .count(count)
                    .votedAt(votedAt)
                    .build()
            );
        }

        String sql = "INSERT INTO vote (choice_id, count, voted_at) VALUES (?, ?, ?)";

        int[] batchSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                Vote vote = votes.get(i);
                ps.setLong(1, vote.getChoiceId());
                ps.setInt(2, vote.getCount());
                ps.setTimestamp(3, Timestamp.valueOf(vote.getVotedAt()));
            }

            @Override
            public int getBatchSize() {
                return votes.size();
            }

        });
        log.info("Successfully inserted {} vote records into the database.", batchSize.length);
    }
}
