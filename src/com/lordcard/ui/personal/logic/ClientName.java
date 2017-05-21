package com.lordcard.ui.personal.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientName {
	public static List<String> allClientNames = null;
	public static String[] client_manNames = new String[] { "乔峰", "段誉", "虚竹", "慕容复", "郭靖", "杨康",
			"胡斐", "令狐冲", "独孤求败", "岳不群", "杨过", "张无忌", "韦小宝", "张辽", "郭嘉", "荀彧", "贾诩", "司马懿", "关羽",
			"张飞", "赵云", "黄忠", "魏延", "诸葛亮", "庞统", "周瑜", "陆逊", "太史慈", "曹操", "孙权", "刘备", "吕布", "武松",
			"宋江", "卢俊义", "李逵", "燕青", "林冲", "清风", "明月", "日游神", "夜游神", "九头虫", "云里雾", "雾里云", "急如火",
			"快如风", "千里眼", "顺风耳" };
	public static String[] client_womanNames = new String[] { 
			"貂禅", "赛金花", "妲己", "王昭君", "文成公主",
			"李师师", "蔡文姬", "萧燕燕", "班昭", "薛涛", "秋瑾", "夏姬", "贾南风", "董小宛", "朱淑真", "万贞儿", "苏小小", "虞姬",
			"李香君", "杨玉环", "冯小怜", "吕四娘", "张丽华", "潘玉奴", "洪宣娇", "卞玉京", "卓文君", "唐婉", "王聪儿", "金城公主",
			"李季兰", "伊帕尔汗", "钟无艳", "绿珠", "武则天", "关盼盼", "顾眉生", "田倩", "卫夫人", "寇白门", "胡充华", "花见羞",
			"徐昭佩", "陈阿娇", "黄月英", "窦漪", "侯光姬", "上官婉儿", "窦漪房",
			"赵四小姐","西施","东施","李清照","柳如是","步非烟"};

	public static List<String> getallClientNames() {
		allClientNames = new ArrayList<String>();
		for (String str : client_womanNames) {
			allClientNames.add(str);
		}
		List<String> ClientNames = new ArrayList<String>();
		for (int i = 0; i < 2; i++) {
			int nameSize = allClientNames.size();
			Random rd = new Random();
			int index = rd.nextInt(nameSize);
			ClientNames.add(allClientNames.get(index));
			allClientNames.remove(index);
		}
		return ClientNames;
	}
}
