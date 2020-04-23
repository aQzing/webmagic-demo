package com.qzing.webmagic_demo;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.qzing.repository.ClassInfoJpa;
import com.qzing.webmagic.pojo.ClassInfo;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUser {

    @Autowired
    private ClassInfoJpa classInfoJpa1;
    @Test
    public void testClass() throws Exception{
    	List<ClassInfo> findAll = classInfoJpa1.findAll();
    	System.out.println(findAll.get(0).getClassContent());
    	ClassInfo classInfo =  new ClassInfo();
    	classInfo.setClassContent(new String("你好啊".getBytes(), "utf-8"));
    	classInfoJpa1.save(classInfo);
    	//classInfo.setClassContent(new String("你好啊".getBytes(), "gbk"));
    	//classInfoJpa1.save(classInfo);
    }


}
