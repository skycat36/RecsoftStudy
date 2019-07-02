package com.recsoft.utils;

import io.swagger.annotations.*;
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

    @ApiOperation(value = "Генерация уникального идентификатора.")
    public static String getUnicalUUID(){
        return UUID.randomUUID().toString();
    }

    @ApiOperation(value = "Обработка изображения.")
    public static BufferedImage changeImage(
            @ApiParam(value = "Файл изображения.", required = true) MultipartFile file,
            @ApiParam(value = "Ширина.", required = true) int weight,
            @ApiParam(value = "Высота.", required = true) int height) throws IOException {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new IOException();
            }
        } catch (IOException e) {
            throw new IOException("Картинка не грузится.");
        }

        BufferedImage changeImage = new BufferedImage(weight,height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = changeImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(bufferedImage, 0, 0, weight, height, null);
        g.dispose();

        return changeImage;
    }

    @ApiOperation(value = "Загрузка файла с сервера.")
    public static void downloadFile(
            @ApiParam(value = "Для передачи файла на сторону клиента и информации о нем.", required = true) HttpServletResponse resonse,
            @ApiParam(value = "Путь откуда будет браться файл.", required = true) String path
    ) throws IOException {
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        resonse.setContentType(mediaType.getType());

        File file = new File(path);

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

    @ApiOperation(value = "Сохранение файла на сервер.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Имя сохраненного файла")})
    public static String saveFile(
            @ApiParam(value = "Файл изображения.", required = true) MultipartFile multipartFile,
            @ApiParam(value = "Ширина.", required = true) int weight,
            @ApiParam(value = "Высота.", required = true) int height,
            @ApiParam(value = "Путь к папке хранения данных.", required = true) String uploadPath
            ) throws IOException {
        try {
            ImageIO.read(multipartFile.getInputStream());
        } catch (IOException e) {
            throw new IOException("Картинка не загружается.");
        }
        String resultFilename = "";
        if (multipartFile != null && !multipartFile.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            resultFilename = ServiceUtils.getUnicalUUID() + "." + multipartFile.getOriginalFilename();
            ImageIO.write(ServiceUtils.changeImage(multipartFile, weight, height), "JPEG", new File(uploadPath + "/" + resultFilename));
        }
        else{
            throw new IOException("Ошибка создания файла");
        }
            return resultFilename;
    }

}
