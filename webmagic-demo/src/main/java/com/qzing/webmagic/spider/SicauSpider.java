package com.qzing.webmagic.spider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qzing.webmagic.pojo.ClassInfo;
import com.qzing.webmagic.pojo.RoomInfo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
@Component
public class SicauSpider implements PageProcessor {
	@Autowired
	private SicauPipeline sicauPipeline;
	private static String roomPageUrl = "http://jiaowu.sicau.edu.cn/web/web/lanmu/jshi.asp";
	private static String classPageUrl = "http://jiaowu.sicau.edu.cn/web/web/lanmu/kbjshi.asp?bianhao=";
	@Override
	public void process(Page page) {
		String url = page.getRequest().getUrl();
		//教室信息页面
		if(url.contains(roomPageUrl)) {
		//page.putField("roomInfo", page.getHtml().css("head > title","text").toString());
			crawlRoomInfo(page);
			//抓取所有的课程信息页面链接
			crawlClassInks(page);
		}
		//课程信息页面
		else if(url.contains(classPageUrl)){
			crawlClassInfo(page);
		}
	}

	Site site = Site.me().setRetryTimes(3).setSleepTime(100)
//			.addHeader("Cookie", "ASPSESSIONIDCSRARDRA=MBOKABCAEKMOPGBBLMLACNJM; jcrj%5Ftymfg=%C0%B6%C9%AB%BA%A3%CC%B2; jcrj%5Fuser=web; jcrj%5Fpwd=web; jcrj%5Fauth=True; jcrj%5Fjwc%5Fcheck=y; jcrj%5Fsession=tymfg%2Ctymfg%2Cjwc%5Fcheck%2Cauth%2Cuser%2Cpwd%2Csf%2C; jcrj%5Fsf=%D1%A7%C9%FA")
//			.addCookie("jiaowu.sicau.edu.cn", "ASPSESSIONIDCSRARDRA", "MBOKABCAEKMOPGBBLMLACNJM")
			//.addCookie("jiaowu.sicau.edu.cn", "ASPSESSIONIDCSRARDRA", "MBOKABCAEKMOPGBBLMLACNJM")
			;

	@Override
	public Site getSite() {
		return site;
	}
	/**
	 * 爬取课程的所有链接到爬取队列
	 * @param page
	 */
	public void crawlClassInks(Page page){
		List<String> linksList = page.getHtml().links().all();
		linksList = linksList.stream().filter(link->link.contains(classPageUrl)).collect(Collectors.toList());
		page.addTargetRequests(linksList);
	}
	/**
	 * 爬取课程信息
	 * @param page
	 */
	public void crawlClassInfo(Page page) {
		//获取整个表格
		List<Selectable> nodes = page.getHtml().xpath("/html/body/div[2]/center/table/tbody/tr/td/div/center/table/tbody/tr").nodes();
		List <ClassInfo>classList =  new ArrayList<ClassInfo>();
		//获取教室编号
		String url = page.getUrl().toString();
		System.out.println("::::::爬取课程页面："+url);
		String roomId = url.substring(url.lastIndexOf("=")+1);
		//遍历每一行tr,一行代表一堂课
		for (int i=2;i<6; i++) {
			//i=2=>第一堂课 以此类推+1,第几堂课12345,一堂课两节
			//遍历td对应星期几的关系
			//注意1，3堂课表格多一格（合并单元格）故定义初始值和步长
			int j = 2;//第几格开始遍历
			int step = 7;
			if(i==2||i==4) {
				j++;
			}
			for(int h=j;h<j+step;h++) {
				//解析对应的课程信息
				String classContent = Jsoup.parse(nodes.get(i).xpath("/tr/td["+h+"]").toString()).text();//replaceAll("\\s*|\t|\r|\n", "");
				//此处有个神奇的空格StringUtils解决不了
				if(StringUtils.isBlank(classContent)||" ".equals(classContent)) {
					continue;
				}
				ClassInfo classInfo = new ClassInfo();
				//教室编号
				classInfo.setRoomId(roomId);
				//星期几,1代表星期一
				int weekDay = h-1;
				if(i==2||i==4) {
					weekDay--;
				}
				classInfo.setWeekDay(weekDay+"");
				//课程内容
				classInfo.setClassContent(classContent);
				//第几堂课
				int classNum = i-1;
				classInfo.setLessonNum(classNum+"");
				classInfo.setUpdateTime(new Date());
				classList.add(classInfo);
			}
			
		}
		page.putField("classList", classList);
	}
	/**
	 * 爬取教室信息
	 * @param page
	 */
	public void crawlRoomInfo(Page page) {
		String url = page.getUrl().toString();
		System.out.println("::::::爬取教室页面："+url);
		//获取整个表格
		List<Selectable> nodes = page.getHtml().xpath("/html/body/div[2]/center/table[3]/tbody/tr[@height=20]").nodes();
		List<RoomInfo>roomList = new ArrayList<RoomInfo>();
		//遍历每一行
		for (Selectable rowNode : nodes) {
			RoomInfo roomInfo = new RoomInfo();
			String campus = Jsoup.parse(rowNode.xpath("/tr/td[2]").toString()).text();
			String roomName = Jsoup.parse(rowNode.xpath("/tr/td[3]").toString()).text();
			String capacity = Jsoup.parse(rowNode.xpath("/tr/td[4]").toString()).text();
			String roomType = Jsoup.parse(rowNode.xpath("/tr/td[5]").toString()).text();
			String classUrl = rowNode.xpath("/tr/td[6]/a[1]/@href").toString();
			String roomId = classUrl.substring(classUrl.lastIndexOf("=")+1);
			roomInfo.setUpdateTime(new Date());
			roomInfo.setCampus(campus);
			roomInfo.setRoomName(roomName);
			roomInfo.setCapacity(capacity);
			roomInfo.setRoomType(roomType);
			roomInfo.setRoomId(roomId);
			roomList.add(roomInfo);
			//page.putField("roomInfo", roomInfo);
		}
		page.putField("roomList", roomList);
	}
	//@Scheduled(fixedRate=1000*60*60*5,initialDelay = 1000*5)
	public void sicauSpiderRun() {
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("========任务执行时间："+time);
		Spider.create(new SicauSpider())
		        .addUrl("http://jiaowu.sicau.edu.cn/web/web/web/index.asp")
				.addUrl(roomPageUrl)
				//.addUrl(classPageUrl+3347)
				.addPipeline(sicauPipeline)//new SicauPipeline())
				.thread(1)
				.run();
	}
}
