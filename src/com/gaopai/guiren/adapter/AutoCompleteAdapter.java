package com.gaopai.guiren.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gaopai.guiren.R;
import com.tencent.a.b.l;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
	private Context mContext;
	public List<String> mListdata;
	public String mSearchResult;
	private ArrayFilter mFilter;
	private String mAddrName;

	public AutoCompleteAdapter(Context context, List<String> listdata) {
		super();
		mContext = context;
		mListdata = listdata;
	}

	@Override
	public int getCount() {
		return mListdata.size();
	}

	@Override
	public Object getItem(int position) {
		return mSearchResult;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_search_popup, null);
			((TextView) convertView).setText(resultList.get(position));
		} else {
			((TextView) convertView).setText(resultList.get(position));
		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			mSearchResult = prefix.toString();
			resultList = getAutoText(prefix.toString());
			results.values = resultList;
			results.count = resultList.size();
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// TODO Auto-generated method stub
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}

	}
	private List<String> resultList;
	private List<String> getAutoText(String text) {
		List<String> list = new ArrayList<String>();
		for (String str:mListdata) {
			list.add("在“"+str+"”中搜索"+"   "+text);
		}
		return list;
	}
}
