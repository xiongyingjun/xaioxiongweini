package com.hezd.lottiedemo;

import android.content.Context;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import static com.drew.metadata.exif.GpsDirectory.TAG_ALTITUDE;

/**
 * """
 * 每一组都是传5张照片,杆塔首尾加线上三点
 * 其中第一组的第一张照片作为point_base,即新坐标原点
 * """
 * """"
 * 获取图片经纬度，高度信息
 * path:获取该目录下所有文件，存入列表中
 * return:latitude,longitude,height
 * """
 */
public class CalculateUtils {

    /**
     * 杆塔的图片地址
     *
     * @throws ImageProcessingException
     * @throws Exception
     */
    public static double calculate(List<String> pathList) throws ImageProcessingException, Exception {
        if (pathList == null || pathList.size() == 0) {
            System.out.println("数据为空:");
            return -404;
        }
        if (pathList.size() != 10) {
            System.out.println("线线交跨需要提供十张图");
            return -404;
        }
        Double[] latitude = new Double[5];
        Double[] longitude = new Double[5];
        Double[] height = new Double[5];

        Double[] latitude2 = new Double[5];
        Double[] longitude2 = new Double[5];
        Double[] height2 = new Double[5];

        int size = pathList.size();
        for (int i = 0; i < size; i++) {
            String path = pathList.get(i);
            Metadata metadata = ImageMetadataReader.readMetadata(new File(path));
            Collection<GpsDirectory> directoriesOfType = metadata.getDirectoriesOfType(GpsDirectory.class);
            Iterator iterator = directoriesOfType.iterator();
            while (iterator.hasNext()) {
                GpsDirectory gpsDirectory = (GpsDirectory) iterator.next();
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                double latitudeValue = geoLocation.getLatitude();
                double longitudeValue = geoLocation.getLongitude();
                if (i <= 4) {
                    latitude[i] = latitudeValue;
                    longitude[i] = longitudeValue;
                } else {
                    latitude2[i - 5] = latitudeValue;
                    longitude2[i - 5] = longitudeValue;
                }
            }
            for (Directory directory : metadata.getDirectories()) {
                String simpleName = directory.getClass().getSimpleName();
                if (simpleName.equals("GpsDirectory")) {
                    System.err.println("simpleName: " + simpleName);
                    String string = directory.getString(TAG_ALTITUDE);
                    if (string != null) {
                        String value1 = string.split("/")[0];
                        String value2 = string.split("/")[1];
                        double heightValue = (Double.parseDouble(value1) / Double.parseDouble(value2));
                        if (i <= 4) {
                            height[i] = heightValue;
                        } else {
                            height2[i - 5] = heightValue;
                        }
                    }
                }
            }
        }
        double v = callPythonCode(latitude, longitude, height, latitude2, longitude2, height2);
        Log.i("callPythonCode：", v + "---callPythonCode");
        return v;
    }

    // 初始化Python环境
    public static void initPython(Context context) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }
    }

    public static double callPythonCode(Double[] latitude, Double[] longitude
            , Double[] height, Double[] latitude2, Double[] longitude2
            , Double[] height2) {
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("ah").callAttr("sus_chain_line_Cross", latitude2, longitude2, height2, latitude, longitude, height);
        if (pyObject == null) {
            return -404;
        } else {
            return Double.parseDouble(pyObject.toString());
        }
    }

    public static void testLog() {
        Log.i("aihang","ssuccess");
    }
}