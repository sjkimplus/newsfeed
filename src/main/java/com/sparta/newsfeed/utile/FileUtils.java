package com.sparta.newsfeed.utile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${file.upload.path}")
    private String filePath;

    @Value("${server.host:http://localhost}")  // 기본 호스트 (필요에 따라 수정)
    private String host;

    @Value("${server.port:8080}")  // 기본 포트
    private String port;

    public List<String> parseInsertFileInfo(List<MultipartFile> mpFiles) throws IOException {
        List<String> fullUrls = new ArrayList<>();

        for (MultipartFile multipartFile : mpFiles) {
            if (!multipartFile.isEmpty()) {
                String originalFileName = multipartFile.getOriginalFilename();
                String storedFileName = getRandomString() + "_" + originalFileName;

                // 프로젝트 경로와 파일 경로를 결합하여 최종 경로 생성
                String projectPath = System.getProperty("user.dir") + filePath;
                File saveDir = new File(projectPath);

//                if (!saveDir.exists()) {
//                    saveDir.mkdirs();
//                }

                File saveFile = new File(saveDir, storedFileName);

                // 파일 저장
                multipartFile.transferTo(saveFile);

                // 파일의 HTTP URL 생성
                String fileUrl = host + ":" + port + "/files/" + storedFileName;
                fullUrls.add(fileUrl);
            }
        }
        return fullUrls;
    }

    public static String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
