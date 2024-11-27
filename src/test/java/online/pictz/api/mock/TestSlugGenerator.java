package online.pictz.api.mock;

import online.pictz.api.admin.service.SlugGenerator;

public class TestSlugGenerator implements SlugGenerator {

    private final String slug;

    public TestSlugGenerator(String slug) {
        this.slug = slug;
    }

    @Override
    public String generate() {
        return slug;
    }
}
