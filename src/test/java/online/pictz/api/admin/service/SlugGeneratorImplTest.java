package online.pictz.api.admin.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SlugGeneratorImplTest {

    SlugGenerator slugGenerator = new SlugGeneratorImpl();

    @Test
    void createSlug() {
        String randomSlug = slugGenerator.generate();

        assertThat(randomSlug).isNotNull();
        assertThat(randomSlug).isInstanceOf(String.class);
    }

}