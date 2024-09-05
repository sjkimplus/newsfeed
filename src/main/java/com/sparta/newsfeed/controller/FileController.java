package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.utile.FileUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileUtils fileUtils;

    // 업로드된 이미지 파일을 불러오는 API (파일 경로 숨김)
    @GetMapping("/files/{type}/{filename:.+}")
    public void loadImage(@PathVariable("type") String type, @PathVariable("filename") String filename,
                          HttpServletRequest req, HttpServletResponse res) throws IOException {
        // URL 디코딩된 파일명
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());

        File baseDir = new File(System.getProperty("user.dir") + fileUtils.getBaseFilePath() + "\\" + type);
        File requestedFile = new File(baseDir, decodedFilename);

        if (!requestedFile.getCanonicalPath().startsWith(baseDir.getCanonicalPath())) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (requestedFile.exists() && requestedFile.isFile()) {
            String mimeType = req.getServletContext().getMimeType(requestedFile.getAbsolutePath());
            List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

            if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // 파일명 인코딩 처리
            String encodedFilename = URLEncoder.encode(requestedFile.getName(), StandardCharsets.UTF_8.toString());
            encodedFilename = encodedFilename.replace("+", "%20"); // 공백 처리

            // Content-Disposition 설정 (UTF-8 지원을 위한 브라우저별 설정)
            String headerValue = String.format("attachment; filename*=UTF-8''%s", encodedFilename);
            res.setHeader("Content-Disposition", headerValue);
            res.setContentType(mimeType);

            try (FileInputStream in = new FileInputStream(requestedFile);
                 ServletOutputStream outStream = res.getOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.flush();
            }
        } else {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
