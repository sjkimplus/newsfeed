package com.sparta.newsfeed.utile;

import com.sparta.newsfeed.entity.Image;
import com.sparta.newsfeed.entity.Type;
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.exception.DataNotFoundException;
import com.sparta.newsfeed.exception.FileProcessingException;
import com.sparta.newsfeed.repository.ImageRepository;
import com.sparta.newsfeed.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sparta.newsfeed.entity.Type.USER;

@Component
public class FileUtils {

    @Value("${file.upload.path}")
    private String filePath;

    private final ImageRepository imageRepository;
    private final UserService userService;

    public FileUtils(ImageRepository imageRepository, @Lazy UserService userService) {
        this.imageRepository = imageRepository;
        this.userService = userService;
    }

    public String getBaseFilePath() {
        return filePath;
    }

    public List<String> parseInsertFileInfo(List<MultipartFile> mpFiles, Type type) throws IOException {
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile multipartFile : mpFiles) {
            if (!multipartFile.isEmpty()) {
                try {
                    String originalFileName = multipartFile.getOriginalFilename();
                    String storedFileName = UUID.randomUUID() + "_" + originalFileName;

                    // 파일을 저장할 경로 설정
                    String projectPath = System.getProperty("user.dir") + filePath + "\\" + type;

                    File saveFile = new File(projectPath, storedFileName);
                    if (!saveFile.exists()) {
                        saveFile.mkdirs();  // 디렉토리가 존재하지 않으면 생성
                    }
                    multipartFile.transferTo(saveFile);
                    System.out.println("Saved file path: " + saveFile.getAbsolutePath());  // 저장된 파일의 경로를 출력

                    // 저장된 파일의 상대 경로를 리스트에 추가
                    String relativePath = "/files/" + type + "/" + storedFileName;
                    filePaths.add(relativePath);
                } catch (IOException e) {
                    throw new FileProcessingException("파일 저장 중 오류가 발생했습니다. 파일명: " + multipartFile.getOriginalFilename(), e);
                }
            }
        }
        return filePaths;  // 저장된 파일의 상대 경로들을 반환
    }

    public void deleteExistingImages(List<Image> imagesToDelete) throws IOException {
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            for (Image image : imagesToDelete) {
                // 1. 데이터베이스에서 이미지 삭제
                imageRepository.delete(image);

                // 2. 파일 시스템에서 파일 삭제
                deleteImageFiles(image);
            }
        }
    }

    public void deleteImageFiles(Image image) {
        // 프로젝트 경로 가져오기
        String projectPath = System.getProperty("user.dir");

        // 이미지 URL 리스트 가져오기
        List<String> imageUrls = image.getImageUrl();

        for (String imageUrl : imageUrls) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // /files 경로를 제거하고 실제 파일 경로를 생성
                imageUrl = imageUrl.replace("/files", "");

                // 절대 경로 생성 (OS에 따라 파일 구분자를 일관되게 처리)
                String absoluteFilePath = projectPath + File.separator + filePath + imageUrl.replace("/", File.separator);
                System.out.println("삭제할 파일 경로: " + absoluteFilePath);

                // 파일 객체 생성 및 파일 삭제
                File fileToDelete = new File(absoluteFilePath);
                if (fileToDelete.exists()) {
                    if (!fileToDelete.delete()) {
                        throw new FileProcessingException("파일 삭제 실패: " + fileToDelete.getAbsolutePath());
                    }
                } else {
                    throw new DataNotFoundException("파일이 존재하지 않습니다: " + fileToDelete.getAbsolutePath());
                }
            }
        }
    }

    public List<String> modifyUsersImage(String email, List<MultipartFile> multipartFile) throws IOException {
        User user = userService.findUser(email);

        // 1. 기존 이미지 파일을 가져오기
        List<Image> imagesToDelete = imageRepository.findByItemId(user.getId());


        if (multipartFile != null && !multipartFile.isEmpty()) {
            // 1.1 기존 파일 삭제 (DB 및 파일 시스템)
            deleteExistingImages(imagesToDelete);

            // 1.2 새로운 이미지 파일 저장
            List<String> newImagePaths = parseInsertFileInfo(multipartFile, USER);

            // 1.3 새 이미지 경로를 DB에 저장
            for (String imagePath : newImagePaths) {
                Image newImage = new Image(user.getId(), USER, imagePath);
                imageRepository.save(newImage);  // 새 이미지 저장
            }

            return newImagePaths;  // 업데이트된 이미지 리스트 반환
        } else {
            if (imagesToDelete.size() <= 0){
                throw new FileProcessingException("등록된 이미지가 없습니다.");
            }
            // 2. 새로운 이미지가 없는 경우, 기존 파일만 삭제
            deleteExistingImages(imagesToDelete);
        }

        return new ArrayList<>();  // 빈 리스트 반환
    }

    public List<String> createUsersImage(String email, List<MultipartFile> multipartFile) throws IOException {
        User user = userService.findUser(email);
        List<Image> allByTypeAndItemId = imageRepository.findAllByTypeAndItemId(USER, user.getId());

        if (allByTypeAndItemId.size() > 0) {
            throw new FileProcessingException("이미 저장된 사진이 있습니다.");
        }

        if (multipartFile.size() == 1) {
            return saveImage(USER, multipartFile, user.getId());
        }
        throw new IllegalArgumentException("이미지를 한 개 이상 업로드해야 합니다.");
    }

    public List<String> saveImage(Type type, List<MultipartFile> multipartFile, Long ItemId) throws IOException {
        List<String> imagePaths = parseInsertFileInfo(multipartFile, type);

        for (String imagePath : imagePaths) {
            // 이미지 URL을 DB에 저장
            if (!imagePath.isEmpty()) {
                Image img = new Image(ItemId, type, imagePath);
                imageRepository.save(img);
            }
        }
        return imagePaths;
    }

    public List<String> getImage(Type type, Long itemId) {
        List<String> imageUrls = new ArrayList<>();
        List<Image> images = imageRepository.findAllByTypeAndItemId(type, itemId);
        for (Image file : images) {
            imageUrls.addAll(file.getImageUrl()); // Add all image URLs to the list
        }
        return imageUrls;
    }

}
