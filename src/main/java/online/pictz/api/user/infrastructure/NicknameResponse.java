package online.pictz.api.user.infrastructure;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NicknameResponse {

    private String msg;

    private String code;

    private String data;

    public NicknameResponse(String msg, String code, String data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }
}
