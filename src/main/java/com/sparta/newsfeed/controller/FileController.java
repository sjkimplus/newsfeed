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
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileUtils fileUtils;

    // 업로드된 이미지 파일을 불러오는 API (파일 경로 숨김)
    @GetMapping("/files/{type}/{filename:.+}")
    public void loadImage(@PathVariable("type") String type, HttpServletRequest req, HttpServletResponse res) throws IOException {
        // 요청된 URI에서 파일 경로 추출 (URL 디코딩 포함)
        String requestURI = req.getRequestURI();
        String decodedURI = URLDecoder.decode(requestURI, StandardCharsets.UTF_8.toString());
        String filePath = System.getProperty("user.dir") + fileUtils.getBaseFilePath() + "\\" + type + decodedURI.replace("/files/" + type, "");

        File imgFile = new File(filePath);
        System.out.println("Looking for file at: " + imgFile.getAbsolutePath());  // 실제 찾으려는 파일 경로 출력

        if (imgFile.exists()) {
            try (FileInputStream in = new FileInputStream(imgFile);
                 ServletOutputStream outStream = res.getOutputStream()) {

                // 파일의 MIME 타입 설정
                String mimeType = req.getServletContext().getMimeType(imgFile.getAbsolutePath());
                res.setContentType(mimeType != null ? mimeType : "application/octet-stream");

                byte[] buffer = new byte[1024];
                int bytesRead;
                // 파일을 클라이언트에게 전송
                while ((bytesRead = in.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }

                outStream.flush();
            }
        } else {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);  // 파일이 없을 경우 404 응답
        }
    }
}
