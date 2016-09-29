package com.pyn.mobilemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.activity.FlowSortActivity;
import com.pyn.mobilemanager.domain.AppInfo;
import com.pyn.mobilemanager.util.TextFormater;

import java.util.List;

/**
 * 流量详情的适配器
 */
public class FlowSortListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private FlowSortActivity mContext;
	private List<AppInfo> appInfos;

	public FlowSortListAdapter(FlowSortActivity context, List<AppInfo> infos) {
		mInflater = LayoutInflater.from(context);
		this.appInfos = infos;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return appInfos.size();
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public Object getItem(int position) {
		return appInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		AppInfo info = appInfos.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.flow_sort_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.flow_sort_item_iv_icon);
			holder.appName = (TextView) convertView
					.findViewById(R.id.flow_sort_tv_app_name);
			holder.upData = (TextView) convertView
					.findViewById(R.id.flow_sort_tv_updata);
			holder.downData = (TextView) convertView
					.findViewById(R.id.flow_sort_tv_downdata);
			holder.allData = (TextView) convertView
					.findViewById(R.id.flow_sort_tv_alldata);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.icon.setImageDrawable(info.getIcon());
		holder.appName.setText(info.getAppName());

		holder.upData.setText(TextFormater.getDataSize(info.getTxFlow()));
		holder.downData.setText(TextFormater.getDataSize(info.getRxFlow()));
		long all = info.getTxFlow() + info.getRxFlow();

		holder.allData.setText(TextFormater.getDataSize(all));
		return convertView;
	}

	private static class ViewHolder {
		ImageView icon;
		TextView appName;
		TextView upData;
		TextView downData;
		TextView allData;
	}

}
