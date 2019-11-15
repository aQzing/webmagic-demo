package com.qzing.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.qzing.webmagic.pojo.ClassInfo;
@Component
public interface ClassInfoJpa  extends JpaRepository<ClassInfo,Integer> {

	List<ClassInfo> findByClassContent(String classContent);
}
