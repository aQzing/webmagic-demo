package com.qzing.webmagic.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
/**
 * 教室实体
 * @author 11073
 *
 */
@Data
@Entity
@Table(name="room_info")
public class RoomInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	//校区
	@Column(name="campus")
	private String campus;
	//教室名称
	@Column(name="room_name")
	private String roomName;
	//可容纳人数
	@Column(name="capacity")
	private String capacity;
	//教室类别
	@Column(name="room_type")
	private String roomType;
	//教室编号
	@Column(name = "room_id")
	private String roomId;
    //更新时间
	@Column(name="update_time")
	private Date updateTime;
}
