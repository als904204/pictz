package online.pictz.api.common.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommonControllerTest {

    @Test
    void index() {
        CommonController commonController = new CommonController();
        String index = commonController.index();
        assertThat(index).isEqualTo("index");
    }
}