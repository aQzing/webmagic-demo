package com.qzing.webmagic.spider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import com.qzing.repository.ClassInfoJpa;
import com.qzing.repository.RoomInfoJpa;
import com.qzing.webmagic.pojo.ClassInfo;
import com.qzing.webmagic.pojo.RoomInfo;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

//自定义实现Pipeline接口
@Component
@SuppressWarnings("all")
class SicauPipeline implements Pipeline {
	@Autowired
	private ClassInfoJpa classInfoJpa;
	@Autowired
	private RoomInfoJpa roomInfoJpa;

	// @Transactional
	public void process(ResultItems resultitems, Task task) {
		Map<String, Object> mapResults = resultitems.getAll();
		Iterator<Entry<String, Object>> iter = mapResults.entrySet().iterator();
		Map.Entry<String, Object> entry;
		// 输出到控制台
		while (iter.hasNext()) {
			entry = iter.next();
			if (entry.getKey().equals("classList") && entry.getValue() != null) {
				List list = (List) entry.getValue();
				// 持久化
				// saveclassInfoByList(list);
				saveclassInfoBySinle(list);
			}
			if (entry.getKey().equals("roomList") && entry.getValue() != null) {
				List list = (List) entry.getValue();
				// 持久化
				// saveRoomInfoByList(list);
				saveRoomInfoBySinle(list);
			}
		}

	}

	// 批量存储不去重
	public void saveclassInfoByList(List list) {
		classInfoJpa.saveAll(list);
	}

	// 批量存储不去重
	public void saveRoomInfoByList(List list) {
		roomInfoJpa.saveAll(list);

	}

	// 单条存储查询去重
	public void saveclassInfoBySinle(List list) {
		for (Object object : list) {
			List classInfoList = findClassInfoByContent((ClassInfo) object);
			if (classInfoList != null && classInfoList.size() < 1) {
				classInfoJpa.saveAndFlush((ClassInfo) object);
			}
		}
	}

	// 单条存储查询去重
	public void saveRoomInfoBySinle(List list) {
		for (Object object : list) {
			List roomInfoList = findRoomInfoByRoomId((RoomInfo) object);
			if (roomInfoList != null && roomInfoList.size() < 1) {
				roomInfoJpa.saveAndFlush((RoomInfo) object);
			}else {
				//System.out.println("重复数据>>>>>"+((RoomInfo)roomInfoList.get(0)).getRoomName());
			}
		}

	}

	
	// 查询数据库
	public List findClassInfo(ClassInfo classInfo) {
		Example<ClassInfo> example = Example.of(classInfo);
		List<ClassInfo> classList = classInfoJpa.findAll(example);
		return classList;
	}
	public List findClassInfoByContent(ClassInfo classInfo) {
		List<ClassInfo> classList = classInfoJpa.findByClassContent(classInfo.getClassContent());
		return classList;
	}
	public List findRoomInfoByRoomName(RoomInfo roomInfo) {
		List<RoomInfo> roomList = roomInfoJpa.findByRoomName(roomInfo.getRoomName());
		return roomList;
	}
	public List findRoomInfoByRoomId(RoomInfo roomInfo) {
		List<RoomInfo> roomList = roomInfoJpa.findByRoomId(roomInfo.getRoomId());
		return roomList;
	}
	public List findRoomInfo(RoomInfo roomInfo) {
		Example<RoomInfo> example = Example.of(roomInfo);
		List<RoomInfo> roomList = roomInfoJpa.findAll(example);
		return roomList;
	}
}