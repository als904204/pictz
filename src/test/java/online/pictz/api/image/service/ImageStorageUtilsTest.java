package online.pictz.api.image.service;

import static org.assertj.core.api.Assertions.assertThat;

import online.pictz.api.common.util.image.ImageStorageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageStorageUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void isImageFile() {
        boolean jpeg = ImageStorageUtils.isImageFile("image.jpeg");
        boolean jpg = ImageStorageUtils.isImageFile("image.jpg");
        boolean png = ImageStorageUtils.isImageFile("image.png");
        boolean bmp = ImageStorageUtils.isImageFile("image.bmp");

        assertThat(jpeg).isTrue();
        assertThat(jpg).isTrue();
        assertThat(png).isTrue();
        assertThat(bmp).isTrue();
    }

    @Test
    void isNotImageFile() {
        boolean exe = ImageStorageUtils.isImageFile("image.exe");
        boolean svg = ImageStorageUtils.isImageFile("image.svg");
        boolean war = ImageStorageUtils.isImageFile("image.war");

        assertThat(exe).isFalse();
        assertThat(svg).isFalse();
        assertThat(war).isFalse();
    }

    @Test
    void getFileExtension() {
        String jpeg = "image.jpeg";
        String jpg = "image.jpg";
        String png = "image.png";

        String jpegExtension = ImageStorageUtils.getFileExtension(jpeg);
        String jpgExtension = ImageStorageUtils.getFileExtension(jpg);
        String pngExtension = ImageStorageUtils.getFileExtension(png);

        assertThat(jpegExtension).isEqualTo("jpeg");
        assertThat(jpgExtension).isEqualTo("jpg");
        assertThat(pngExtension).isEqualTo("png");
    }

    @Test
    void cleanFilename() {
        String dirtyImgFile = "Hello Image .jpg";

        String cleanImgFile = ImageStorageUtils.cleanFilename(dirtyImgFile);

        assertThat(cleanImgFile).isEqualTo("HelloImage");
    }
}