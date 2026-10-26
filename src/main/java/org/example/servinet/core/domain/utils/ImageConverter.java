package org.example.servinet.core.domain.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.example.servinet.core.domain.exception.FileExistException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ImageConverter {
    public static Image toImage(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return toImage("/multimedia/images/NoUser.png");
        }

        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            return new Image(input);
        } catch (Exception e) {
            return toImage("/multimedia/images/NoUser.png");
        }
    }

    public static Image toImage(String path) {
        InputStream inputStream =
                ImageConverter.class.getResourceAsStream(path);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "No se encontró el recurso: " + path
            );
        }

        return new Image(inputStream);
    }

    public static Image toImage(Path image) throws IOException {
        try (InputStream inputStream = Files.newInputStream(image)) {
            return new Image(inputStream);
        }
    }

    public static byte[] toBytes(Path image) throws IOException {
        return Files.readAllBytes(image);
    }
    public static byte[] toBytes(Image image) {
        if (image == null) {
            return null;
        }
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
