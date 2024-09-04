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
        // 1. 요청된 URI에서 파일 경로 추출 (URL 디코딩 포함)
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());

        // 2. 서버의 기본 디렉토리 경로와 파일 경로를 설정
        File baseDir = new File(System.getProperty("user.dir") + fileUtils.getBaseFilePath() + "\\" + type);
        File requestedFile = new File(baseDir, decodedFilename);

        // 3. 경로 탐색 공격 방지: 요청된 파일 경로가 baseDir 경로를 벗어나지 않도록 검증
        if (!requestedFile.getCanonicalPath().startsWith(baseDir.getCanonicalPath())) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 권한 없음(403) 반환
            return;
        }

        // 4. 파일이 존재하는지 확인
        if (requestedFile.exists() && requestedFile.isFile()) {
            // 5. 파일의 MIME 타입 설정
            String mimeType = req.getServletContext().getMimeType(requestedFile.getAbsolutePath());

            // 6. 허용된 MIME 타입 리스트
            List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

            // 7. 허용되지 않은 MIME 타입은 차단
            if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 허용되지 않은 파일 형식에 대해 403 반환
                return;
            }

            // 8. 파일을 다운로드로 처리하도록 Content-Disposition 헤더 설정
            res.setHeader("Content-Disposition", "attachment; filename=\"" + requestedFile.getName() + "\"");
            res.setContentType(mimeType);

            // 9. 파일을 클라이언트로 전송
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
            // 10. 파일이 존재하지 않으면 404 상태 반환
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);  // 파일을 찾을 수 없을 경우 404 반환
        }
    }
}
