package com.snail.commons.utils

/**
 * 各地图API坐标系统比较与转换;
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系,
 * 谷歌地图采用的是WGS84地理坐标系（中国范围除外）;
 * GCJ02坐标系：即火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。
 * 谷歌中国地图和搜搜中国地图采用的是GCJ02地理坐标系; BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系;
 * 搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。
 */
object CoordConverter {
    var pi = 3.1415926535897932384626
    var a = 6378245.0
    var ee = 0.00669342162296594323

    /**
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System to Mars Geodetic System
     */
    @JvmStatic
    fun gps84ToGcj02(lat: Double, lon: Double): Gps? {
        if (outOfChina(lat, lon)) {
            return null
        }
        var dLat = transformLat(lon - 105.0, lat - 35.0)
        var dLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * pi
        var magic = Math.sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dLon = dLon * 180.0 / (a / sqrtMagic * Math.cos(radLat) * pi)
        val mgLat = lat + dLat
        val mgLon = lon + dLon
        return Gps(mgLat, mgLon)
    }

    /**
     * 火星坐标系 (GCJ-02) to 84
     */
    @JvmStatic
    fun gcjToGps84(lat: Double, lon: Double): Gps {
        val gps = transform(lat, lon)
        val lontitude = lon * 2 - gps.longitude
        val latitude = lat * 2 - gps.latitude
        return Gps(latitude, lontitude)
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
     */
    @JvmStatic
    fun gcj02ToBd09(gg_lat: Double, gg_lon: Double): Gps {
        val z = Math.sqrt(gg_lon * gg_lon + gg_lat * gg_lat) + 0.00002 * Math.sin(gg_lat * pi)
        val theta = Math.atan2(gg_lat, gg_lon) + 0.000003 * Math.cos(gg_lon * pi)
        val bdLon = z * Math.cos(theta) + 0.0065
        val bdLat = z * Math.sin(theta) + 0.006
        return Gps(bdLat, bdLon)
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法，将 BD-09 坐标转换成GCJ-02 坐标
     */
    @JvmStatic
    fun bd09ToGcj02(bd_lat: Double, bd_lon: Double): Gps {
        val x = bd_lon - 0.0065
        val y = bd_lat - 0.006
        val z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi)
        val theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi)
        val ggLon = z * Math.cos(theta)
        val ggLat = z * Math.sin(theta)
        return Gps(ggLat, ggLon)
    }

    /**
     * (BD-09) to 84
     */
    @JvmStatic
    fun bd09ToGps84(bd_lat: Double, bd_lon: Double): Gps {
        val gcj02 = CoordConverter.bd09ToGcj02(bd_lat, bd_lon)
        return CoordConverter.gcjToGps84(gcj02.latitude, gcj02.longitude)

    }

    @JvmStatic
    fun outOfChina(lat: Double, lon: Double): Boolean {
        return lon < 72.004 || lon > 137.8347 || lat < 0.8293 || lat > 55.8271
    }

    @JvmStatic
    fun transform(lat: Double, lon: Double): Gps {
        if (outOfChina(lat, lon)) {
            return Gps(lat, lon)
        }
        var dLat = transformLat(lon - 105.0, lat - 35.0)
        var dLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * pi
        var magic = Math.sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dLon = dLon * 180.0 / (a / sqrtMagic * Math.cos(radLat) * pi)
        val mgLat = lat + dLat
        val mgLon = lon + dLon
        return Gps(mgLat, mgLon)
    }

    @JvmStatic
    fun transformLat(x: Double, y: Double): Double {
        var ret = (-100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x)))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0
        return ret
    }

    @JvmStatic
    fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0
        return ret
    }

    class Gps(var latitude: Double, var longitude: Double)
}
