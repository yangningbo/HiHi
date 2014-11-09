package com.gaopai.guiren.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.widget.indexlist.CharacterParser;

public class CountryCodeAdapter extends BaseAdapter implements SectionIndexer {
	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static abstract class Row {
	}

	public static final class Section extends Row {
		public final String text;

		public Section(String text) {
			this.text = text;
		}
	}

	public static final class Item extends Row implements Comparable<Item> {
		public final String text;
		public final String pingYinText;
		public final String code;
		public static final CharacterParser parser = new CharacterParser();

		public Item(String text, String code) {
			this.text = text;
			this.code = code;
			this.pingYinText = parser.getSelling(text).toUpperCase();
		}

		@Override
		public int compareTo(Item another) {
			// TODO Auto-generated method stub
			return this.pingYinText.compareTo(another.pingYinText);
		}

	}

	private List<Row> rows;

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	@Override
	public int getCount() {
		return rows.size();
	}

	@Override
	public Row getItem(int position) {
		return rows.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		  View view = convertView;
	        
	        if (getItemViewType(position) == 0) { // Item
	            if (view == null) {
	                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                view = (LinearLayout) inflater.inflate(R.layout.row_item, parent, false);  
	            }
	            
	            Item item = (Item) getItem(position);
	            TextView textView = (TextView) view.findViewById(R.id.textView1);
	            textView.setText(item.text);
	        } else { // Section
	            if (view == null) {
	                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                view = (LinearLayout) inflater.inflate(R.layout.row_section, parent, false);  
	            }
	            
	            Section section = (Section) getItem(position);
	            TextView textView = (TextView) view.findViewById(R.id.textView1);
	            textView.setText(section.text);
	        }
	        
	        return view;
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				Row row = getItem(j);
				if(row instanceof Section) {
					if(((Section) row).text.charAt(0) == mSections.charAt(section)){
						return j;
					}
				}
			}
		}
		return 0;
	}

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    
    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) {
            return 1;
        } else {
            return 0;
        }
    }
	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}
}
