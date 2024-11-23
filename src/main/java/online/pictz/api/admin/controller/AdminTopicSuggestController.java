package online.pictz.api.admin.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.admin.dto.AdminTopicSuggestUpdateRequest;
import online.pictz.api.admin.dto.AdminTopicSuggestUpdateResponse;
import online.pictz.api.admin.service.AdminTopicSuggestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/optimistic/admin/topic-suggests")
@RestController
public class AdminTopicSuggestController {

    private final AdminTopicSuggestService adminTopicSuggestService;

    @GetMapping
    public ResponseEntity<List<AdminTopicSuggestResponse>> getAllTopicSuggests() {
        List<AdminTopicSuggestResponse> responses = adminTopicSuggestService.getAllTopicSuggests();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminTopicSuggestResponse> getTopicSuggestById(@PathVariable("id") Long id) {
        AdminTopicSuggestResponse response = adminTopicSuggestService.getSuggestById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminTopicSuggestUpdateResponse> patchTopicSuggestStatus(
        @PathVariable("id") Long id, @RequestBody AdminTopicSuggestUpdateRequest request) {
        AdminTopicSuggestUpdateResponse response = adminTopicSuggestService.patchTopicSuggestStatus(id,
            request.getStatus(), request.getReason());
        return ResponseEntity.ok(response);
    }


}
