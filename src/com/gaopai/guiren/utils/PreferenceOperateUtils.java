package com.gaopai.guiren.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @Title TAPreferenceOperateUtils
 * @Package com.ta.util.config
 * @Description TAPreferenceOperateUtils是Preference的操作类
 * @author 白猫
 * @date 2013-1-7
 * @version V1.0
 */
public class PreferenceOperateUtils {
	private Context mContext;
	private SharedPreferences mSharedPreferences = null;
	private Editor edit = null;

	/**
	 * 创建 DefaultSharedPreferences
	 * 
	 * @param context
	 */
	public PreferenceOperateUtils(Context context) {
		// this(context,
		// PreferenceManager.getDefaultSharedPreferences(context));
		this(context, context.getSharedPreferences(SPConst.SP_DEFAULT, SPConst.getMode()));
	}

	/**
	 * 通过文件名 创建 SharedPreferences
	 * 
	 * @param context
	 * @param filename
	 *            文件名
	 */
	public PreferenceOperateUtils(Context context, String filename) {
		this(context, context.getSharedPreferences(filename, Context.MODE_WORLD_WRITEABLE));
	}

	public PreferenceOperateUtils(Context context, String filename, int mode) {
		this(context, context.getSharedPreferences(filename, mode));
	}

	/**
	 * 通过SharedPreferences创建 SharedPreferences
	 * 
	 * @param context
	 * @param sharedPreferences
	 */
	public PreferenceOperateUtils(Context context, SharedPreferences sharedPreferences) {
		this.mContext = context;
		this.mSharedPreferences = sharedPreferences;
		edit = mSharedPreferences.edit();
	}

	public void setString(String key, String value) {
		// TODO Auto-generated method stub
		edit.putString(key, value);
		edit.commit();
	}

	public void setInt(String key, int value) {
		// TODO Auto-generated method stub
		edit.putInt(key, value);
		edit.commit();
	}

	public void setBoolean(String key, Boolean value) {
		// TODO Auto-generated method stube
		edit.putBoolean(key, value);
		edit.commit();
	}

	public void setByte(String key, byte[] value) {
		// TODO Auto-generated method stub
		setString(key, String.valueOf(value));
	}

	public void setShort(String key, short value) {
		// TODO Auto-generated method stub
		setString(key, String.valueOf(value));
	}

	public void setLong(String key, long value) {
		// TODO Auto-generated method stub
		edit.putLong(key, value);
		edit.commit();
	}

	public void setFloat(String key, float value) {
		// TODO Auto-generated method stub
		edit.putFloat(key, value);
		edit.commit();
	}

	public void setDouble(String key, double value) {
		// TODO Auto-generated method stub
		setString(key, String.valueOf(value));
	}

	public void setString(int resID, String value) {
		// TODO Auto-generated method stub
		setString(this.mContext.getString(resID), value);

	}

	public void setInt(int resID, int value) {
		// TODO Auto-generated method stub
		setInt(this.mContext.getString(resID), value);
	}

	public void setBoolean(int resID, Boolean value) {
		// TODO Auto-generated method stub
		setBoolean(this.mContext.getString(resID), value);
	}

	public void setByte(int resID, byte[] value) {
		// TODO Auto-generated method stub
		setByte(this.mContext.getString(resID), value);
	}

	public void setShort(int resID, short value) {
		// TODO Auto-generated method stub
		setShort(this.mContext.getString(resID), value);
	}

	public void setLong(int resID, long value) {
		// TODO Auto-generated method stub
		setLong(this.mContext.getString(resID), value);
	}

	public void setFloat(int resID, float value) {
		// TODO Auto-generated method stub
		setFloat(this.mContext.getString(resID), value);
	}

	public void setDouble(int resID, double value) {
		// TODO Auto-generated method stub
		setDouble(this.mContext.getString(resID), value);
	}

	public String getString(String key, String defaultValue) {
		// TODO Auto-generated method stub
		return mSharedPreferences.getString(key, defaultValue);
	}

	public int getInt(String key, int defaultValue) {
		// TODO Auto-generated method stub
		return mSharedPreferences.getInt(key, defaultValue);
	}

	public boolean getBoolean(String key, Boolean defaultValue) {
		// TODO Auto-generated method stub
		return mSharedPreferences.getBoolean(key, defaultValue);
	}

	public byte[] getByte(String key, byte[] defaultValue) {
		// TODO Auto-generated method stub
		try {
			return getString(key, "").getBytes();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return defaultValue;
	}

	public short getShort(String key, Short defaultValue) {
		// TODO Auto-generated method stub
		try {
			return Short.valueOf(getString(key, ""));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return defaultValue;
	}

	public long getLong(String key, Long defaultValue) {
		// TODO Auto-generated method stub
		return mSharedPreferences.getLong(key, defaultValue);
	}

	public float getFloat(String key, Float defaultValue) {
		// TODO Auto-generated method stub
		return mSharedPreferences.getFloat(key, defaultValue);
	}

	public double getDouble(String key, Double defaultValue) {
		// TODO Auto-generated method stub
		try {
			return Double.valueOf(getString(key, ""));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return defaultValue;
	}

	public String getString(int resID, String defaultValue) {
		// TODO Auto-generated method stub
		return getString(this.mContext.getString(resID), defaultValue);
	}

	public int getInt(int resID, int defaultValue) {
		// TODO Auto-generated method stub
		return getInt(this.mContext.getString(resID), defaultValue);
	}

	public boolean getBoolean(int resID, Boolean defaultValue) {
		// TODO Auto-generated method stub
		return getBoolean(this.mContext.getString(resID), defaultValue);
	}

	public byte[] getByte(int resID, byte[] defaultValue) {
		// TODO Auto-generated method stub
		return getByte(this.mContext.getString(resID), defaultValue);
	}

	public short getShort(int resID, Short defaultValue) {
		// TODO Auto-generated method stub
		return getShort(this.mContext.getString(resID), defaultValue);
	}

	public long getLong(int resID, Long defaultValue) {
		// TODO Auto-generated method stub
		return getLong(this.mContext.getString(resID), defaultValue);
	}

	public float getFloat(int resID, Float defaultValue) {
		// TODO Auto-generated method stub
		return getFloat(this.mContext.getString(resID), defaultValue);
	}

	public double getDouble(int resID, Double defaultValue) {
		// TODO Auto-generated method stub
		return getDouble(this.mContext.getString(resID), defaultValue);
	}

	public void remove(String key) {
		// TODO Auto-generated method stub
		edit.remove(key);
		edit.commit();
	}

	public void remove(String... keys) {
		// TODO Auto-generated method stub
		for (String key : keys)
			remove(key);
	}

	public void clear() {
		// TODO Auto-generated method stub
		edit.clear();
		edit.commit();
	}

}
