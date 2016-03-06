package db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import db.MengshuiWeatherOpenHelper;
import model.City;
import model.County;
import model.Province;

public class MengshuiWeatherDB {
	/*
	 * ���ݿ���
	 * */
	public static final String DB_NAME = "mengshui_weather";
	
	/*
	 * ���ݿ�汾
	 */
	public static final int VERSION = 1;
	
	private static MengshuiWeatherDB mengshuiWeatherDB;
	
	private SQLiteDatabase db;
	
	/*
	 * �����췽��˽�л� ���õ�MengshuiWeatherDBʵ����ʱ�� ���ű�����ϡ�
	 */
	private MengshuiWeatherDB(Context context) {
		MengshuiWeatherOpenHelper dbHelper = new MengshuiWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/*
	 * ��ȡMengshuiWeatherDB��ʵ������Ϊ�乹�췽��˽�л���ͬʱͨ��������ʵ����ȫ�ַ�Χ��ֻ��һ��MengshuiWeatherDBʵ����
	 * ����������
	 * pulbic class Something(){
         public synchronized void isSyncA(){}
         public synchronized void isSyncB(){}
         public static synchronized void cSyncA(){}
         public static synchronized void cSyncB(){}
     }
	   	��ô��������Something�������ʵ��a��b����ô�����鷽�����Ա�1�������߳�ͬʱ������
	   a.   x.isSyncA()��x.isSyncB() 
	   b.   x.isSyncA()��y.isSyncA()
	   c.   x.cSyncA()��y.cSyncB()
	   d.   x.isSyncA()��Something.cSyncA()
	    ���������Ŀ����жϣ�
	   a�����Ƕ�ͬһ��ʵ����synchronized����ʣ���˲��ܱ�ͬʱ����
	   b������Բ�ͬʵ���ģ���˿���ͬʱ������
	   c����Ϊ��static synchronized�����Բ�ͬʵ��֮����Ȼ�ᱻ����,�൱��Something.isSyncA()��   Something.isSyncB()�ˣ���˲��ܱ�ͬʱ���ʡ�
	      ��ô����d��?�����ϵ� ���ǿ��Ա�ͬʱ���ʵģ���������synchronzied����ʵ��������synchronzied���෽������������lock����ͬ��ԭ��
	      ���˷���Ҳ����synchronized ��static synchronized �൱�������ɣ����Թܸ��ԣ��໥֮�����Լ���ˣ����Ա�ͬʱ���ʡ�Ŀǰ�����Ƿ����java�ڲ����synchronzied����ô��ʵ�ֵġ�


    	���ۣ�A: synchronized static��ĳ����ķ�Χ��synchronized static cSync{}��ֹ����߳�ͬʱ�������    ���е�synchronized static �����������Զ�������ж���ʵ�������á�
            B: synchronized ��ĳʵ���ķ�Χ��synchronized isSync(){}��ֹ����߳�ͬʱ�������ʵ���е�synchronized ������
	 * */
	public synchronized static MengshuiWeatherDB getInstance(Context context) {
		if (mengshuiWeatherDB == null) {
			mengshuiWeatherDB = new MengshuiWeatherDB(context);
		}
		return mengshuiWeatherDB;
	}
	
	/*
	 * ��Provinceʵ���洢�����ݿ⡣
	 * */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/*
	 * �����ݿ��ȡȫ�����е�ʡ����Ϣ/
	 */
	public List<Province> loadProvince() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor !=null) {
			cursor.close();
		}
		
		return list;
	}
	
	/*
	 * ��Cityʵ���洢�����ݿ�
	 * */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	} 
	
	/*
	 * �����ݿ��ȡĳʡ���еĳ�����Ϣ/
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", 
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor !=null) {
			cursor.close();
		}
		
		return list;
	}
	
	 /* ��Countyʵ���洢�����ݿ�
	 * */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	} 
	
	/*
	 * �����ݿ��ȡĳ���������е�����Ϣ/
	 */
	public List<County> loadCounty(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?", 
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		if (cursor !=null) {
			cursor.close();
		}
		
		return list;
	}
	
}
