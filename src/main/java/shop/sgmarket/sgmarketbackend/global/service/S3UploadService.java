package shop.sgmarket.sgmarketbackend.global.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.global.properties.S3Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String uploadImage(MultipartFile file, String keyPrefix) throws IOException {
        // 고유한 파일명 생성
        String extension = getFileExtension(file.getOriginalFilename());
        String key = String.format("%s/%s.%s", keyPrefix, UUID.randomUUID(), extension);

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    s3Properties.bucket(), s3Properties.region(), key);
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 찾을 수 없습니다: " + originalFilename);
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
    }
}
