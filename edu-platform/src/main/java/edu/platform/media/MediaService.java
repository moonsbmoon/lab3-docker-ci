package edu.platform.media;

import edu.platform.shared.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class MediaService {

    private final Path root = Paths.get("uploads");
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Transactional
    public String upload(MultipartFile file) {
        // 1. Проверка размера (до 5 МиБ)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        try (InputStream is = file.getInputStream()) {
            // 2. Проверка формата по магическим байтам
            byte[] bytes = is.readNBytes(12);
            String ext = getFileExtension(bytes);
            if (ext == null) {
                throw new IllegalArgumentException("Only PNG, JPEG, WEBP allowed");
            }

            // 3. Генерация имени (UUID)
            String fileName = UUID.randomUUID().toString() + "." + ext;
            Path path = root.resolve(fileName);
            Files.createDirectories(root);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 4. Откат файла при сбое транзакции
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        try { Files.deleteIfExists(path); } catch (Exception ignored) {}
                    }
                }
            });

            // 5. Сохранение метаданных в БД
            MediaFile mediaFile = new MediaFile();
            mediaFile.setFileName(fileName);
            mediaFile.setContentType(file.getContentType());
            mediaFile.setSize(file.getSize());
            mediaFile.setStoragePath(path.toString());
            mediaRepository.save(mediaFile);

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public Resource download(String fileName) {
        MediaFile mediaFile = mediaRepository.findByFileName(fileName)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        try {
            Path path = Paths.get(mediaFile.getStoragePath());
            return new UrlResource(path.toUri());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    public MediaFile getMetadata(String fileName) {
        return mediaRepository.findByFileName(fileName)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
    }

    // Проверка магических байтов
    private String getFileExtension(byte[] bytes) {
        if (bytes.length < 12) return null;
        // PNG: 89 50 4E 47
        if (bytes[0] == (byte)0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) return "png";
        // JPEG: FF D8 FF
        if (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xD8 && bytes[2] == (byte)0xFF) return "jpeg";
        // WEBP: RIFF (52 49 46 46) ... WEBP (57 45 42 50)
        if (bytes[0] == 0x52 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x46 &&
                bytes[8] == 0x57 && bytes[9] == 0x45 && bytes[10] == 0x42 && bytes[11] == 0x50) return "webp";
        return null;
    }
}