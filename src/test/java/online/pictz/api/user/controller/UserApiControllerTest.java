package online.pictz.api.user.controller;

import static org.assertj.core.api.Assertions.assertThat;

import online.pictz.api.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class UserApiControllerTest {

    private UserApiController userApiController;

    @BeforeEach
    void setUp() {
        userApiController = new UserApiController();
    }

    @Test
    void profile() {
        // given
        Long userId = 1L;
        String nickname = "foo";
        UserDto userDto = new UserDto(userId, nickname);

        // when
        ResponseEntity<UserDto> response = userApiController.profile(userDto);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(userId);
        assertThat(response.getBody().getNickname()).isEqualTo(nickname);
    }


}