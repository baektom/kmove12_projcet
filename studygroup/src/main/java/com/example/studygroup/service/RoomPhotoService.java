package com.example.studygroup.service;

import com.example.studygroup.domain.room.RoomPhoto;
import com.example.studygroup.repository.RoomPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomPhotoService {

    private final RoomPhotoRepository roomPhotoRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // ✅ 갤러리 목록
    @Transactional(readOnly = true)
    public List<RoomPhoto> list(Long studyId) {
        return roomPhotoRepository.findByStudyIdOrderByIdDesc(studyId);
    }

    // ✅ 대표이미지 1장 조회 (없으면 null)
    @Transactional(readOnly = true)
    public RoomPhoto getCover(Long studyId) {
        return roomPhotoRepository.findFirstByStudyIdAndIsCoverTrue(studyId).orElse(null);
    }

    // ✅ 업로드
    public void upload(Long studyId, Long uploaderId, String uploaderName, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("파일이 비었습니다.");

        // 폴더 준비
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String originalName = file.getOriginalFilename();

        // 확장자 추출
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        String storedName = UUID.randomUUID() + ext;

        // 실제 저장
        File saved = new File(dir, storedName);
        try {
            file.transferTo(saved);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        RoomPhoto photo = new RoomPhoto();
        photo.setStudyId(studyId);
        photo.setUploaderId(uploaderId);
        photo.setUploaderName(uploaderName);
        photo.setOriginalName(originalName == null ? "image" : originalName);
        photo.setStoredName(storedName);
        photo.setUrl("/uploads/" + storedName);

        // ✅ 첫 업로드면 자동으로 대표 설정(원하면 이 로직 지워도 됨)
        boolean hasCover = roomPhotoRepository.findFirstByStudyIdAndIsCoverTrue(studyId).isPresent();
        if (!hasCover) {
            photo.setCover(true);
        }

        roomPhotoRepository.save(photo);
    }

    // ✅ 삭제
    public void delete(Long studyId, Long photoId, Long loginUserId) {
        RoomPhoto photo = roomPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("사진 없음"));

        if (!photo.getStudyId().equals(studyId)) throw new SecurityException("잘못된 접근");

        // 업로더만 삭제 가능
        if (!photo.getUploaderId().equals(loginUserId)) throw new SecurityException("삭제 권한 없음");

        boolean wasCover = photo.isCover();

        // 실제 파일 삭제
        File f = new File(uploadDir, photo.getStoredName());
        if (f.exists()) f.delete();

        // DB 삭제
        roomPhotoRepository.delete(photo);

        // ✅ 삭제한 게 대표였으면, 남아있는 사진 중 최신 1장을 대표로 자동 지정(선택)
        if (wasCover) {
            List<RoomPhoto> rest = roomPhotoRepository.findByStudyIdOrderByIdDesc(studyId);
            if (!rest.isEmpty()) {
                RoomPhoto newCover = rest.get(0);
                newCover.setCover(true);
                roomPhotoRepository.save(newCover);
            }
        }
    }

    // ✅ 대표 이미지 설정
    public void setCover(Long studyId, Long photoId, Long loginUserId) {
        RoomPhoto photo = roomPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("사진 없음"));

        if (!photo.getStudyId().equals(studyId)) throw new SecurityException("잘못된 접근");

        // ✅ 대표 설정 권한: 업로더만 가능(원하면 "스터디 방장만"으로 바꿔도 됨)
        if (!photo.getUploaderId().equals(loginUserId)) throw new SecurityException("대표 설정 권한 없음");

        // 1) 기존 대표 해제
        roomPhotoRepository.clearCoverByStudyId(studyId);

        // 2) 새 대표 지정
        photo.setCover(true);
        roomPhotoRepository.save(photo);
    }
}
