package edu.platform.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findByFileName(String fileName);
}