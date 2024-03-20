package com.wmy.study.DearIMProject.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Slf4j
public class FileUtils {
    public static String encodeFileToBase64(MultipartFile file) throws IOException {
        if (file == null) {
            return null;
        }

        byte[] fileBytes = file.getBytes();
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    public static String caculateMd5(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream is = file.getInputStream(); DigestInputStream digestInputStream = new DigestInputStream(is, digest)) {
            byte[] buffer = new byte[4096];
            while (digestInputStream.read(buffer) != -1) {
                // 读取文件内容以更新哈希值
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder md5Hex = new StringBuilder();
        for (byte b : hashBytes) {
            md5Hex.append(String.format("%02x", b));
        }
        return md5Hex.toString().toUpperCase();
    }

    public static List<Integer> imageWidth(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        BufferedImage image = ImageIO.read(inputStream);

        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            return List.of(width, height);
        } else {
            return null;
        }
    }
}
