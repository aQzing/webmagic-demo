package com.qzing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.qzing.webmagic.pojo.RoomInfo;

@Component
public interface RoomInfoJpa  extends JpaRepository<RoomInfo,Integer>{

	List<RoomInfo> findByRoomName(String roomName);

	List<RoomInfo> findByRoomId(String roomId);

}
