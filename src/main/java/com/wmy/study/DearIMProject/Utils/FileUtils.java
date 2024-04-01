package com.wmy.study.DearIMProject.Utils;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public static List<Integer> imageWidth(MultipartFile file) throws Exception {
        ImageUtils.ImageInformation imageInformation = ImageUtils.readImageInformation(multipartFileToFile(file));
        assert imageInformation != null;
        return List.of(imageInformation.width, imageInformation.height);

    }


    /**
     * MultipartFile 转 File
     *
     * @param multipartFile
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        assert (multipartFile != null);
        assert (multipartFile.getOriginalFilename() != null);
        File tempFile = File.createTempFile(multipartFile.getOriginalFilename(), null);
        InputStream inputStream = multipartFile.getInputStream();
        OutputStream outputStream = new FileOutputStream(tempFile);
        int bytesRead;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return tempFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
