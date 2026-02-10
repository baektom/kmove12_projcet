package com.example.studygroup.repository;

import com.example.studygroup.domain.room.RoomPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomPhotoRepository extends JpaRepository<RoomPhoto, Long> {

    // ✅ 갤러리(최신순)
    List<RoomPhoto> findByStudyIdOrderByIdDesc(Long studyId);

    // ✅ 대표이미지(1장)
    Optional<RoomPhoto> findFirstByStudyIdAndIsCoverTrue(Long studyId);

    // ✅ 해당 스터디의 대표이미지 전부 해제 (1장만 유지하기 위해)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RoomPhoto p set p.isCover = false where p.studyId = :studyId and p.isCover = true")
    int clearCoverByStudyId(@Param("studyId") Long studyId);
}
