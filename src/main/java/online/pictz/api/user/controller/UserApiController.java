package online.pictz.api.user.controller;

import lombok.RequiredArgsConstructor;
import online.pictz.api.common.annotation.CurrentUser;
import online.pictz.api.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserApiController {

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@CurrentUser UserDto userDto) {
        return ResponseEntity.ok(userDto);
    }

}
