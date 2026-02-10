package com.example.studygroup.repository;

import com.example.studygroup.domain.room.RoomNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomNoticeRepository extends JpaRepository<RoomNotice, Long> {

    List<RoomNotice> findByStudyIdOrderByIdDesc(Long studyId);

}
