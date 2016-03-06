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
	 * 数据库名
	 * */
	public static final String DB_NAME = "mengshui_weather";
	
	/*
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	
	private static MengshuiWeatherDB mengshuiWeatherDB;
	
	private SQLiteDatabase db;
	
	/*
	 * 将构造方法私有化 当得到MengshuiWeatherDB实例的时候 三张表创建完毕。
	 */
	private MengshuiWeatherDB(Context context) {
		MengshuiWeatherOpenHelper dbHelper = new MengshuiWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/*
	 * 获取MengshuiWeatherDB的实例。因为其构造方法私有化，同时通过加锁，实现了全局范围内只有一个MengshuiWeatherDB实例。
	 * 加锁的例子
	 * pulbic class Something(){
         public synchronized void isSyncA(){}
         public synchronized void isSyncB(){}
         public static synchronized void cSyncA(){}
         public static synchronized void cSyncB(){}
     }
	   	那么，加入有Something类的两个实例a与b，那么下列组方法何以被1个以上线程同时访问呢
	   a.   x.isSyncA()与x.isSyncB() 
	   b.   x.isSyncA()与y.isSyncA()
	   c.   x.cSyncA()与y.cSyncB()
	   d.   x.isSyncA()与Something.cSyncA()
	    这里，很清楚的可以判断：
	   a，都是对同一个实例的synchronized域访问，因此不能被同时访问
	   b，是针对不同实例的，因此可以同时被访问
	   c，因为是static synchronized，所以不同实例之间仍然会被限制,相当于Something.isSyncA()与   Something.isSyncB()了，因此不能被同时访问。
	      那么，第d呢?，书上的 答案是可以被同时访问的，答案理由是synchronzied的是实例方法与synchronzied的类方法由于锁定（lock）不同的原因。
	      个人分析也就是synchronized 与static synchronized 相当于两帮派，各自管各自，相互之间就无约束了，可以被同时访问。目前还不是分清楚java内部设计synchronzied是怎么样实现的。


    	结论：A: synchronized static是某个类的范围，synchronized static cSync{}防止多个线程同时访问这个    类中的synchronized static 方法。它可以对类的所有对象实例起作用。
            B: synchronized 是某实例的范围，synchronized isSync(){}防止多个线程同时访问这个实例中的synchronized 方法。
	 * */
	public synchronized static MengshuiWeatherDB getInstance(Context context) {
		if (mengshuiWeatherDB == null) {
			mengshuiWeatherDB = new MengshuiWeatherDB(context);
		}
		return mengshuiWeatherDB;
	}
	
	/*
	 * 将Province实例存储到数据库。
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
	 * 从数据库读取全国所有的省份信息/
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
	 * 将City实例存储到数据库
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
	 * 从数据库读取某省所有的城市信息/
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
	
	 /* 将County实例存储到数据库
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
	 * 从数据库读取某城市下所有的县信息/
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
