package com.recsoft.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

public class ServiceUtils {

    /*Путь к папке хранения данных*/
    @Value("${upload.path}")
    private String uploadPath;

    public static String getUnicalUUID(){
        return UUID.randomUUID().toString();
    }

    public static BufferedImage changeImage(MultipartFile file, int weight, int height) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

        BufferedImage changeImage = new BufferedImage(weight,height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = changeImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(bufferedImage, 0, 0, weight, height, null);
        g.dispose();

        return changeImage;
    }

    public static void readFile(File file, HttpServletResponse response) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             OutputStream outputStream = response.getOutputStream()) {
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read()) != -1) {
                outputStream.write(bytesRead);
            }
        }
    }

    public void downloadFile(HttpServletResponse resonse, String fileName) throws IOException {
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        resonse.setContentType(mediaType.getType());

        System.out.println("fileName: " + fileName);
        System.out.println("mediaType: " + mediaType);

        File file = new File(uploadPath + "/" + fileName);


        // Content-Type
        // application/pdf


        // Content-Disposition
        resonse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());

        // Content-Length
        resonse.setContentLength((int) file.length());

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(resonse.getOutputStream());


        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.flush();
    }

}
