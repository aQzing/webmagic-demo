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
 * 课程实体
 * @author 11073
 *
 */
@Data
@Entity
@Table(name="class_info")
public class ClassInfo {
	//教室编号
	@Id //这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "room_id")
	private String roomId;
	//星期几
	@Column(name = "week_day")
	private String weekDay;
	//第几节课
	@Column(name = "lesson_num")
	private String lessonNum;
	//课程信息
	@Column(name = "class_content")
	private String classContent;
    //更新时间
	@Column(name = "update_time")
	private Date updateTime;
	
}
