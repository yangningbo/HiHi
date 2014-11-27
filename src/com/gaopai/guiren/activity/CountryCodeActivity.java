package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.CountryCodeAdapter;
import com.gaopai.guiren.adapter.CountryCodeAdapter.Item;
import com.gaopai.guiren.adapter.CountryCodeAdapter.Row;
import com.gaopai.guiren.adapter.CountryCodeAdapter.Section;
import com.gaopai.guiren.widget.indexlist.IndexableListView;
import com.gaopai.guiren.widget.indexlist.SingleIndexScroller;
import com.nineoldandroids.animation.ObjectAnimator;

public class CountryCodeActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_country_code);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("选择国家和地区代码");
		
		mItems = new ArrayList<Item>();
		mRows = new ArrayList<Row>();
		
		mItems.add(new Item("安哥拉","244"));
		mItems.add(new Item("阿富汗","93"));
		mItems.add(new Item("阿尔巴尼亚","355"));
		mItems.add(new Item("阿尔及利亚","213"));
		mItems.add(new Item("安道尔共和国","376"));
		mItems.add(new Item("安圭拉岛","1264"));
		mItems.add(new Item("安提瓜和巴布达","1268"));
		mItems.add(new Item("阿根廷","54"));
		mItems.add(new Item("亚美尼亚","374"));
		mItems.add(new Item("阿森松",""));
		mItems.add(new Item("澳大利亚","61"));
		mItems.add(new Item("奥地利","43"));
		mItems.add(new Item("阿塞拜疆","994"));
		mItems.add(new Item("巴哈马","1242"));
		mItems.add(new Item("巴林","973"));
		mItems.add(new Item("孟加拉国","880"));
		mItems.add(new Item("巴巴多斯","1246"));
		mItems.add(new Item("白俄罗斯","375"));
		mItems.add(new Item("比利时","32"));
		mItems.add(new Item("伯利兹","501"));
		mItems.add(new Item("贝宁","229"));
		mItems.add(new Item("百慕大群岛","1441"));
		mItems.add(new Item("玻利维亚","591"));
		mItems.add(new Item("博茨瓦纳","267"));
		mItems.add(new Item("巴西","55"));
		mItems.add(new Item("文莱","673"));
		mItems.add(new Item("保加利亚","359"));
		mItems.add(new Item("布基纳法索","226"));
		mItems.add(new Item("缅甸","95"));
		mItems.add(new Item("布隆迪","257"));
		mItems.add(new Item("喀麦隆","237"));
		mItems.add(new Item("加拿大","1"));
		mItems.add(new Item("开曼群岛",""));
		mItems.add(new Item("中非共和国","236"));
		mItems.add(new Item("乍得","235"));
		mItems.add(new Item("智利","56"));
		mItems.add(new Item("中国","86"));
		mItems.add(new Item("哥伦比亚","57"));
		mItems.add(new Item("刚果","242"));
		mItems.add(new Item("库克群岛","682"));
		mItems.add(new Item("哥斯达黎加","506"));
		mItems.add(new Item("古巴","53"));
		mItems.add(new Item("塞浦路斯","357"));
		mItems.add(new Item("捷克","420"));
		mItems.add(new Item("丹麦","45"));
		mItems.add(new Item("吉布提","253"));
		mItems.add(new Item("多米尼加共和国","1890"));
		mItems.add(new Item("厄瓜多尔","593"));
		mItems.add(new Item("埃及","20"));
		mItems.add(new Item("萨尔瓦多","503"));
		mItems.add(new Item("爱沙尼亚","372"));
		mItems.add(new Item("埃塞俄比亚","251"));
		mItems.add(new Item("斐济","679"));
		mItems.add(new Item("芬兰","358"));
		mItems.add(new Item("法国","33"));
		mItems.add(new Item("法属圭亚那","594"));
		mItems.add(new Item("加蓬","241"));
		mItems.add(new Item("冈比亚","220"));
		mItems.add(new Item("格鲁吉亚","995"));
		mItems.add(new Item("德国","49"));
		mItems.add(new Item("加纳","233"));
		mItems.add(new Item("直布罗陀","350"));
		mItems.add(new Item("希腊","30"));
		mItems.add(new Item("格林纳达","1809"));
		mItems.add(new Item("关岛","1671"));
		mItems.add(new Item("危地马拉","502"));
		mItems.add(new Item("几内亚","224"));
		mItems.add(new Item("圭亚那","592"));
		mItems.add(new Item("海地","509"));
		mItems.add(new Item("洪都拉斯","504"));
		mItems.add(new Item("香港","852"));
		mItems.add(new Item("匈牙利","36"));
		mItems.add(new Item("冰岛","354"));
		mItems.add(new Item("印度","91"));
		mItems.add(new Item("印度尼西亚","62"));
		mItems.add(new Item("伊朗","98"));
		mItems.add(new Item("伊拉克","964"));
		mItems.add(new Item("爱尔兰","353"));
		mItems.add(new Item("以色列","972"));
		mItems.add(new Item("意大利","39"));
		mItems.add(new Item("科特迪瓦",""));
		mItems.add(new Item("牙买加","1876"));
		mItems.add(new Item("日本","81"));
		mItems.add(new Item("约旦","962"));
		mItems.add(new Item("柬埔寨","855"));
		mItems.add(new Item("哈萨克斯坦","327"));
		mItems.add(new Item("肯尼亚","254"));
		mItems.add(new Item("韩国","82"));
		mItems.add(new Item("科威特","965"));
		mItems.add(new Item("吉尔吉斯坦","331"));
		mItems.add(new Item("老挝","856"));
		mItems.add(new Item("拉脱维亚","371"));
		mItems.add(new Item("黎巴嫩","961"));
		mItems.add(new Item("莱索托","266"));
		mItems.add(new Item("利比里亚","231"));
		mItems.add(new Item("利比亚","218"));
		mItems.add(new Item("列支敦士登","423"));
		mItems.add(new Item("立陶宛","370"));
		mItems.add(new Item("卢森堡","352"));
		mItems.add(new Item("澳门","853"));
		mItems.add(new Item("马达加斯加","261"));
		mItems.add(new Item("马拉维","265"));
		mItems.add(new Item("马来西亚","60"));
		mItems.add(new Item("马尔代夫","960"));
		mItems.add(new Item("马里","223"));
		mItems.add(new Item("马耳他","356"));
		mItems.add(new Item("马里亚那群岛",""));
		mItems.add(new Item("马提尼克",""));
		mItems.add(new Item("毛里求斯","230"));
		mItems.add(new Item("墨西哥","52"));
		mItems.add(new Item("摩尔多瓦","373"));
		mItems.add(new Item("摩纳哥","377"));
		mItems.add(new Item("蒙古","976"));
		mItems.add(new Item("蒙特塞拉特岛","1664"));
		mItems.add(new Item("摩洛哥","212"));
		mItems.add(new Item("莫桑比克","258"));
		mItems.add(new Item("纳米比亚","264"));
		mItems.add(new Item("瑙鲁","674"));
		mItems.add(new Item("尼泊尔","977"));
		mItems.add(new Item("荷属安的列斯",""));
		mItems.add(new Item("荷兰","31"));
		mItems.add(new Item("新西兰","64"));
		mItems.add(new Item("尼加拉瓜","505"));
		mItems.add(new Item("尼日尔","227"));
		mItems.add(new Item("尼日利亚","234"));
		mItems.add(new Item("朝鲜","850"));
		mItems.add(new Item("挪威","47"));
		mItems.add(new Item("阿曼","968"));
		mItems.add(new Item("巴基斯坦","92"));
		mItems.add(new Item("巴拿马","507"));
		mItems.add(new Item("巴布亚新几内亚","675"));
		mItems.add(new Item("巴拉圭","595"));
		mItems.add(new Item("秘鲁","51"));
		mItems.add(new Item("菲律宾","63"));
		mItems.add(new Item("波兰","48"));
		mItems.add(new Item("法属玻利尼西亚","689"));
		mItems.add(new Item("葡萄牙","351"));
		mItems.add(new Item("波多黎各","1787"));
		mItems.add(new Item("卡塔尔","974"));
		mItems.add(new Item("留尼旺",""));
		mItems.add(new Item("罗马尼亚","40"));
		mItems.add(new Item("俄罗斯","7"));
		mItems.add(new Item("圣卢西亚","1758"));
		mItems.add(new Item("圣文森特岛","1784"));
		mItems.add(new Item("东萨摩亚(美)",""));
		mItems.add(new Item("西萨摩亚",""));
		mItems.add(new Item("圣马力诺","378"));
		mItems.add(new Item("圣多美和普林西比","239"));
		mItems.add(new Item("沙特阿拉伯","966"));
		mItems.add(new Item("塞内加尔","221"));
		mItems.add(new Item("塞舌尔","248"));
		mItems.add(new Item("塞拉利昂","232"));
		mItems.add(new Item("新加坡","65"));
		mItems.add(new Item("斯洛伐克","421"));
		mItems.add(new Item("斯洛文尼亚","386"));
		mItems.add(new Item("所罗门群岛","677"));
		mItems.add(new Item("索马里","252"));
		mItems.add(new Item("南非","27"));
		mItems.add(new Item("西班牙","34"));
		mItems.add(new Item("斯里兰卡","94"));
		mItems.add(new Item("圣卢西亚","1758"));
		mItems.add(new Item("圣文森特","1784"));
		mItems.add(new Item("苏丹","249"));
		mItems.add(new Item("苏里南","597"));
		mItems.add(new Item("斯威士兰","268"));
		mItems.add(new Item("瑞典","46"));
		mItems.add(new Item("瑞士","41"));
		mItems.add(new Item("叙利亚","963"));
		mItems.add(new Item("台湾省","886"));
		mItems.add(new Item("塔吉克斯坦","992"));
		mItems.add(new Item("坦桑尼亚","255"));
		mItems.add(new Item("泰国","66"));
		mItems.add(new Item("多哥","228"));
		mItems.add(new Item("汤加","676"));
		mItems.add(new Item("特立尼达和多巴哥","1809"));
		mItems.add(new Item("突尼斯","216"));
		mItems.add(new Item("土耳其","90"));
		mItems.add(new Item("土库曼斯坦","993"));
		mItems.add(new Item("乌干达","256"));
		mItems.add(new Item("乌克兰","380"));
		mItems.add(new Item("阿拉伯联合酋长国","971"));
		mItems.add(new Item("英国","44"));
		mItems.add(new Item("美国","1"));
		mItems.add(new Item("乌拉圭","598"));
		mItems.add(new Item("乌兹别克斯坦","233"));
		mItems.add(new Item("委内瑞拉","58"));
		mItems.add(new Item("越南","84"));
		mItems.add(new Item("也门","967"));
		mItems.add(new Item("南斯拉夫","381"));
		mItems.add(new Item("津巴布韦","263"));
		mItems.add(new Item("扎伊尔","243"));
		mItems.add(new Item("赞比亚","260"));




		Collections.sort(mItems);
		
		char character = '0';
		for(int i=0; i<mItems.size(); i++) {
			Item item = mItems.get(i);
			char first = item.pingYinText.charAt(0);
			if(i==0 && ( first < 'A' || first > 'Z')) {
				mRows.add(new Section("#"));
			} 
			Log.d("ss", "first = " + first + "  char = " + character);
			if(first >= 'A'&& first <= 'Z') {
				if(character != first) {
					mRows.add(new Section(String.valueOf(first)));
					character = first;
				}
			}
			mRows.add(item);
		}
		
		final CountryCodeAdapter myAdapter = new CountryCodeAdapter();
		myAdapter.setRows(mRows);
		mListView = (IndexableListView) findViewById(R.id.listView);
		mListView.addHeaderView(new View(mContext));
		mListView.setAdapter(myAdapter);
		indexScroller = (SingleIndexScroller) findViewById(R.id.scroller);
		indexScroller.setListView(mListView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Row row = myAdapter.getItem(position);
				if(row instanceof Item) {
					Intent intent = new Intent();
					intent.putExtra(KEY_COUNTRY_CODE, ((Item) row).code);
					intent.putExtra(KEY_COUNTRY_NAME, ((Item) row).text);
					setResult(RESULT_OK, intent);
					CountryCodeActivity.this.finish();
				}
			}
		});
		
	}
	
	private ArrayList<Item> mItems;
	private IndexableListView mListView;
	private ArrayList<Row> mRows;
	private SingleIndexScroller indexScroller;
	
	public final static String KEY_COUNTRY_CODE = "key_code";
	public final static String KEY_COUNTRY_NAME = "key_name";
	

}
