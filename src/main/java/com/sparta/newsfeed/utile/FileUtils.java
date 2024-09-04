package com.sparta.newsfeed.utile;

import com.sparta.newsfeed.entity.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${file.upload.path}")
    private String filePath;

    public String getBaseFilePath() {
        return filePath;
    }

    public List<String> parseInsertFileInfo(List<MultipartFile> mpFiles, Type type) throws IOException {
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile multipartFile : mpFiles) {
            if (!multipartFile.isEmpty()) {
                String originalFileName = multipartFile.getOriginalFilename();
                String storedFileName = getRandomString() + "_" + originalFileName;

                // 파일을 저장할 경로 설정
                String projectPath = System.getProperty("user.dir") + filePath +"\\"+ type;

                File saveFile = new File(projectPath, storedFileName);
                if (!saveFile.exists()) {
                    saveFile.mkdirs();  // 디렉토리가 존재하지 않으면 생성
                }
                multipartFile.transferTo(saveFile);
                System.out.println("Saved file path: " + saveFile.getAbsolutePath());  // 저장된 파일의 경로를 출력


                // 저장된 파일의 상대 경로를 리스트에 추가
                String relativePath = "/files/"+ type +"/"+ storedFileName;
                filePaths.add(relativePath);
            }
        }
        return filePaths;  // 저장된 파일의 상대 경로들을 반환
    }

    private String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
