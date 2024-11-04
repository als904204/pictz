package online.pictz.api.admin.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.admin.service.AdminTopicSuggestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/topic_suggests")
@RestController
public class AdminTopicSuggestController {

    private final AdminTopicSuggestService adminTopicSuggestService;

    @GetMapping
    public ResponseEntity<List<AdminTopicSuggestResponse>> getAllTopicSuggests() {
        List<AdminTopicSuggestResponse> responses = adminTopicSuggestService.getAllTopicSuggests();
        return ResponseEntity.ok(responses);
    }

}
