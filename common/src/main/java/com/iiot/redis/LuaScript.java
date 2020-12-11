package com.iiot.redis;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.iiot.util.ExceptionUtil;

import com.iiot.jedis.Jedis;
import com.iiot.jedis.JedisCluster;
import com.iiot.jedis.JedisPool;

public class LuaScript {

	static Logger logger = Logger.getLogger(LuaScript.class);

	// 脚本解析生产的字符串，对于同一段脚本来说，
	// 不同的REDIS节点解析后都是相同的。
	private String sha = null;;

	/**
	 * 构造方法
	 */
	public LuaScript() {
		super();
	}

	public LuaScript(JedisCluster jc, String script) {
		super();
		// 解析脚本
		scriptParser(jc, script);
	}

	public String getSha() {
		return sha;
	}

	/**
	 * 写报警的lua脚本
	 * @return
	 */
	public static String getAlarmLuaScript() {
		String luaStr = "local gid = KEYS[1];" 
	            + "local vid = ARGV[1];" 
				
	            + "local alarmFlag = ARGV[2];"

				+ "local oldAlarmJson = redis.call('HGET',gid,vid);"

				+ "if(oldAlarmJson ~= false) then "

				+ "local oldData = cjson.decode(oldAlarmJson); "

				+ "local flag = false;"

				+ "oldData[alarmFlag] = flag;"

				+ "local newData = cjson.encode(oldData);"

				+ "redis.call('HSET',gid,vid,newData);"

				+ "end ";

		return luaStr;
	}
	
	
	/**
	 * 
	 */
	public static String hsetGAlarmInfosLuaScript() {
		String luaStr = "local clearTimeKey = KEYS[1];"
				//负载清除的时间(它是删除报警时终端时间，或写入报警时终端时间)
				+ "local clearTime = tonumber(ARGV[1]);"
                //查询历史负载清除时间
                + "local oldlearTime = redis.call('GET',clearTimeKey);"
               
                //如果有清除时间
                + "if (oldlearTime ~= false) then "
                //报警清除时间转数字
				+     "local clearTimeNum = tonumber(oldlearTime);"
                //如果新的时间小于上次存储的(删除时间或报警时间)，则不作操作
                + "    if(clearTime > clearTimeNum) then " 
                //执行报警存储，同时设置过期时间
				+ "        redis.call('SET',clearTimeKey,ARGV[1]);"
				//15秒过期
				+ "        redis.call('EXPIRE',clearTimeKey,30);"
				+ "    else"
				+ "        return nil;"
				+ "    end "
            	+ "else"
            	//执行报警存储，同时设置过期时间
				+ "        redis.call('SET',clearTimeKey,ARGV[1]);"
				//15秒过期
				+ "        redis.call('EXPIRE',clearTimeKey,30);"
				//设置key的存活时间
                + "end;"
                
				//返回操作状态
				+ "return clearTimeKey;";
		return luaStr;
	}

	
	
	/**
	 * 批量查询中间状态，同时进行比较计数(方法二)，存储结构是一个散列
	 * 存储结构为:大key： "SX"+车辆ID
	 *         小key，字段名称：比较计数值count，业务中间状态midData
	 * 查询的时候：传入1个key,为"SX"+车辆ID；传入1个value，为临时序列编号serNum
	 * 返回值是一个字符串，用"*"分隔：如 [100*11*业务中间状态]，100是临时序列编号，10是比较计数值
	 */
	public static String getHashMedInfosLuaScript() {
		String luaStr = "local sxVid = KEYS[1];"
				//临时序列编号
				+ "local serNum = ARGV[1];"
				
				//自增比较计数值count
	            + "local count = redis.call('HINCRBY',sxVid,\"count\",1);"
				
	            //取得 比较计数值count，业务中间状态midData
				+ "local oldMidData = redis.call('HGET',sxVid,\"midData\");"
				
				//建立表
				+ "local tab ={serNum,\"@\",count};"
				
				
				//如果有历史业务中间状态
				+ "if (oldMidData ~= false) then "
				// + " if next(oldMidData) ~= nil then "
				+"     table.insert(tab,4,\"@\") ; "
				+"     table.insert(tab,5,oldMidData);"
			    +"  end;"
				
				//拼接字符串
				+ "return table.concat(tab) ; ";
		
		return luaStr;
	}

	
	
	
	/**
	 * 批量设置中间状态，同时进行比较计数(方法二)
	 * @return
	 * 
	 */
	public static String setHashMedInfosLuaScript() {
		String luaStr = "local sxVid = KEYS[1];"
				//临时序列编号
				+ "    local serNum = tonumber(ARGV[1]);"
				//上次次查询的计数比较值
                + "    local newCount = tonumber(ARGV[2]);"
                //传入的业务中间状态
                + "    local newData = ARGV[3];"
				 //查询Redis中当前存储的 业务中间状态midData
				+ "local oldMedOfCount = redis.call('HGET',sxVid,\"count\");"
              
				//判断是否为空
				+ " if (oldMedOfCount ~= false) then "
				+ "          local oldCount =  tonumber(oldMedOfCount);"
            	//新旧比较计数值相等，则更新中间状态
            	+ "          if(newCount ==oldCount) then "
	            + "                redis.call('HSET',sxVid,\"midData\",newData);"
	            + "          else "
	            + "                serNum=0;"
	            + "          end;"
                + " else "
				+ "          serNum=0;"
	            +"   end;"
				
                + "  return tostring(serNum);";
		return luaStr;
		
	}

	/**
	 * World写REDIS的lua脚本
	 * @return
	 */

	public static String getWorldToReidsScript() {
		String cmdStr = "local gid = KEYS[1];"
	            + "local vid = ARGV[1];"
		        // 得是一个整数
				+ "local newTime = tonumber(ARGV[2]);"
		        + "local newData = ARGV[3];"
				+ "local isNotA5NotPos = tonumber(ARGV[4]);" 
		        
				//定位时间(本次定位取本次时间，本次不定位取上次定位时间)
				+ "local lastPosTime = tonumber(ARGV[5]);"
				
				//终端类型编号，0其它类型，1则为A5M类型
				+ "local terTypeNum = tonumber(ARGV[6]);"
				
				//定位状态，0不定位,其余为定位
				+ "local isPos = tonumber(ARGV[7]);"
				
				// A5E-3设备类型
				+ "local isA5E_3Type = tonumber(ARGV[8]);"
				
				// 上条位置数据
				+ "local posJsonOld = redis.call('HGET',gid,vid);"

				//****(1)如果没有历史VI数据获取数据
				+ "if(posJsonOld == false) then "
				
				//****(1.1)原来没有这个车的位置，而且不是A5也不定位，需要把经纬度改为0
				+ "    if(isNotA5NotPos == 1) then " 
				+ "        local dataNewObj = cjson.decode(newData);"
				+ "        dataNewObj.lon=0;" 
				+ "        dataNewObj.lat=0;"
				+ "        newData=cjson.encode(dataNewObj);" 
				+ "    end"
				
				//****(1.2)原来REDIS没有数据且当前数据定位，直接把最后定位时间写入REDIS
				+ "    if(lastPosTime ~= 0) then " 
				+ "        local dataNewObj = cjson.decode(newData);"
				+ "        local extend = dataNewObj['extend'];"	
				+ "        if(nil == extend) then " 
				+ "              extend = {};"	
				+ "        end"
				+ "        extend['lastPosTime']=lastPosTime;" 
				+ "        newData=cjson.encode(dataNewObj);" 
				+ "    end"			
				+ "    redis.call('HSET',gid,vid,newData);" 
				
				//****(2)如果有历史VI数据获取数据****
				+ "else "
				+ "    local oldData = cjson.decode(posJsonOld); "
				+ "    local oldTime = oldData['devTime'];"
				+ "    local oldLon = oldData['lon'];"
				+ "    local oldLat = oldData['lat'];"
				
				//上次定位状态
				+ "    local oldIsPos = oldData['isPos'];"
				+ "    local dataNewObj = cjson.decode(newData);"
				+ "    local newExtend = dataNewObj['extend'];"
				
				//****2.1  如果没有本次扩展的Tab，则新建一个Tab
				+ "    if(nil == newExtend) then " 
				+ "          newExtend = {};"	
				+ "    end"
				
				// 如果是A5E-3设备，获取当前位置数据的电量，如没有电量则取上一条数据的电量
				+ "    if(isA5E_3Type == 1) then " 
				+ "        local newPower = newExtend['power'];" 
				+ "        if(newPower == nil) then "
				+ "            local oldExtend = oldData['extend'];"
				+ "            if(nil ~= oldExtend) then " 
				+ "                local oldPower = oldExtend['power'];"
				+ "                if(nil ~= oldPower) then " 
				+ "                     newExtend['power'] = oldPower;"	
				+ "                end"
				+ "            end"
				+ "        end"
				+ "    end"
				
				//****2.2  本次时间小于上次时间，不更新redis,直接返回
				+ "    if(newTime <= oldTime) then " 
				+ "         return;"
				+ "    end "
				
				//****(2.3)非A5+本次不定位，则位置取上次有效经纬度
				+ "    if(isNotA5NotPos == 1 and oldLon~= 0 and oldLat ~= 0) then " 
				+ "        dataNewObj.lon=oldLon;" 
		        + "        dataNewObj.lat=oldLat;" 
				+ "    end"
		        
				
		       //****(2.4.1)原来REDIS有数据且当前数据定位，直接把最后定位时间写入REDIS
				+ "    if(lastPosTime ~= 0) then "
				+ "        newExtend['lastPosTime']=lastPosTime;"	
				
				// 原来REDIS有数据且当前不定位，取REDIS中的最后定位时间
				// 如REDIS中也没有最后定位时间，那么不需要设置最后定位时间
				+ "    else"
				
				//****(2.4.2)
				+ "        local oldExtend = oldData['extend'];"
				+ "            if(nil ~= oldExtend) then " 
				+ "                local oldLastPosTime = oldExtend['lastPosTime'];"
				+ "                if(nil ~= oldLastPosTime) then " 
				+ "                     newExtend['lastPosTime'] = oldLastPosTime;"	
				+ "                end"
				+ "            end"
				+ "    end"
		
				//****(2.5)如果是A5M终端类型，如果不定位则取上次定位经纬度，取上次定位终端时间，取上次的定位状态(isPos维持原来值)
				+ "    if(terTypeNum == 1 and isPos ==0 and oldLon~= 0 and oldLat ~= 0) then " 
				+ "        dataNewObj.lon=oldLon;" 
		        + "        dataNewObj.lat=oldLat;" 
		        + "        dataNewObj.isPos=oldIsPos;" 
				+ "        local oldExtend = oldData['extend'];"
				+ "            if(nil ~= oldExtend) then " 
				+ "                local oldLastPosTime = oldExtend['lastPosTime'];"
				+ "                if(nil ~= oldLastPosTime) then " 
				+ "                     newExtend['lastPosTime'] = oldLastPosTime;"	
				+ "                end"
				+ "            end"
				+ "    end"
							
				//****(2.6)最终写入Redis
				+ "        newData=cjson.encode(dataNewObj);" 
				+ "        redis.call('HSET',gid,vid,newData);" 
				//****(3)*****
				+ "end "

		        //****(4)返回激活判断值*****
		        + "if(posJsonOld == false) then "
			    + "    return 0;"
		        + "else "
		        + "    return 1;"
	            + "end " ;
		return cmdStr;
	}
	

	
	/**
	 * 网关World写REDIS的lua脚本，同时统计V3协议类型的里程
	 * @return
	 */
	public static String getWorldToReidsSumMilageScript() {
		String cmdStr = "local gid = KEYS[1];"
				+ "local vid = ARGV[1];"
				// 得是一个整数
				+ "local newTime = tonumber(ARGV[2]);"
				+ "local newData = ARGV[3];"
				+ "local isNotA5NotPos = tonumber(ARGV[4]);" 
				
				//定位时间(本次定位取本次时间，本次不定位取上次定位时间)
				+ "local lastPosTime = tonumber(ARGV[5]);"
				
				//终端类型编号，0其它类型，1则为A5M类型
				+ "local terTypeNum = tonumber(ARGV[6]);"
				
				//定位状态，0不定位,其余为定位
				+ "local isPos = tonumber(ARGV[7]);"
				
				// A5E-3设备类型
				+ "local isA5E_3Type = tonumber(ARGV[8]);"
				
				// V3协议类型，而且经纬度不为0,而且定位状态为1: 0不符合条件，1为需要经纬度计算里程的V3类型
				+ "local V3 = tonumber(ARGV[9]);"
				
				// 上条位置数据
				+ "local posJsonOld = redis.call('HGET',gid,vid);"
				
				//****(1)如果没有历史VI数据获取数据
				+ "if(posJsonOld == false) then "
				
				//****(1.1)原来没有这个车的位置，而且不是A5也不定位，需要把经纬度改为0
				+ "    if(isNotA5NotPos == 1) then " 
				+ "        local dataNewObj = cjson.decode(newData);"
				+ "        dataNewObj.lon=0;" 
				+ "        dataNewObj.lat=0;"
				+ "        newData=cjson.encode(dataNewObj);" 
				+ "    end"
				
				//****(1.2)原来REDIS没有数据且当前数据定位，直接把最后定位时间写入REDIS
				+ "    if(lastPosTime ~= 0) then " 
				+ "        local dataNewObj = cjson.decode(newData);"
				+ "        local extend = dataNewObj['extend'];"	
				+ "        if(nil == extend) then " 
				+ "              extend = {};"	
				+ "        end"
				+ "        extend['lastPosTime']=lastPosTime;" 
				+ "        newData=cjson.encode(dataNewObj);" 
				+ "    end"			
				//***(1.3)没有历史VI数据，不用统计里程，到此可以执行Redis写入
				+ "    redis.call('HSET',gid,vid,newData);" 
				
				//****(2)如果有历史VI数据获取数据****
				+ "else "
				+ "    local oldData = cjson.decode(posJsonOld); "
				+ "    local oldTime = oldData['devTime'];"
				+ "    local oldLon = oldData['lon'];"
				+ "    local oldLat = oldData['lat'];"
				//上次的统计里程
				+ "    local oldMileage = oldData['mileage'];"
				//如果上次统计里程为空，则把上次里程设置为0
				+ "    if(nil == oldMileage) then " 
				+ "          oldMileage = 0;"	
				+ "    end"
				
				
				//上次定位状态
				+ "    local oldIsPos = oldData['isPos'];"
				+ "    local dataNewObj = cjson.decode(newData);"
				+ "    local newExtend = dataNewObj['extend'];"
				
				//****2.1  如果没有本次扩展的Tab，则新建一个Tab
				+ "    if(nil == newExtend) then " 
				+ "          newExtend = {};"	
				+ "    end"
				
				// 如果是A5E-3设备，获取当前位置数据的电量，如没有电量则取上一条数据的电量
				+ "    if(isA5E_3Type == 1) then " 
				+ "        local newPower = newExtend['power'];" 
				+ "        if(newPower == nil) then "
				+ "            local oldExtend = oldData['extend'];"
				+ "            if(nil ~= oldExtend) then " 
				+ "                local oldPower = oldExtend['power'];"
				+ "                if(nil ~= oldPower) then " 
				+ "                     newExtend['power'] = oldPower;"	
				+ "                end"
				+ "            end"
				+ "        end"
				+ "    end"
				
				//****2.2.0  本次时间小于上次时间，不更新redis,直接返回
				+ "    if(newTime <= oldTime) then " 
				+ "         return;"
				+ "    end "
				
				//****(2.2.1)统计里程：(本次经纬度不为0,而且定位状态为1;同时保证上次经纬度不为0)
				//必须保证是V3类型才累加
				+ "   if(V3 == 1) then " 
				+ "      if(oldLon~= 0 and oldLat ~= 0) then " 
				//这里经纬度一定存在，否则不能进入这个分支
				+ "       local newLon = dataNewObj['lon'];"
				+ "       local newLat = dataNewObj['lat'];"
				//计算的前后两次里程差值
				+ "       local deltaMileage = 0;"
				//用于校验的前后两次里程最值，这里取了200公里
				+ "       local CHECKDISTANCE = 200000;"
				//用于校验的前后两次速度值
				+ "       local AVERAGEVELOCITY = 60;"
				//地球半径，单位千米
				+ "       local EARTH_RADIUS = 6378.137;"
				//Math.PI
				+ "       local PI = 3.14159265358979323846;"
				+ "       local radLat1 = newLat*PI/180.0;"
				+ "       local radLat2 = oldLat*PI/180.0;"
				+ "       local a = radLat1-radLat2;"
				+ "       local radLon1 = newLon*PI/180.0;"
				+ "       local radLon2 = oldLon*PI/180.0;"
				+ "       local b = radLon1-radLon2;"
				//计算两个经纬度之间的距离     单位是 米
				+ "       local s = 2 * math.asin(math.sqrt(math.pow(math.sin(a/2),2)+math.cos(radLat1)*math.cos(radLat2)*math.pow(math.sin(b/2),2)));"
				+ "       s = s * EARTH_RADIUS*1000;"
				//四舍五入处理
				//+ "     deltaMileage = Math.round(s * 10000)/10000;"
				+ "       deltaMileage = math.floor(s * 10000+0.5)/10000;"
				//计算平均速度单位 米/秒
				+ "       local averageSpeed = deltaMileage*1000/(newTime-oldTime);"
				+ "       local newMileage=0;"
				//如果前后里程差值过大,或平均速度过大
				+ "       if(CHECKDISTANCE <= deltaMileage or averageSpeed >= AVERAGEVELOCITY) then " 
				+ "            newMileage=oldMileage;"
				+ "       else "
				+ "            newMileage = oldMileage+deltaMileage;"
				+ "       end "
				//设置累加结果的新里程
				+ "       dataNewObj.mileage=newMileage;" 
				+ "    else"
				//不符合条件则保持上次的统计里程
				+ "       dataNewObj.mileage=oldMileage;"
				+ "    end"
				
				+ "   end "
				
				//****(2.2.1) 如果是V3协议，但不符合累加条件则保留上次的累加结果 
				+ "    if(V3 == 2) then " 
				+ "       dataNewObj.mileage=oldMileage;"
				+ "    end"
				
				
				//****(2.3)非A5+本次不定位，则位置取上次有效经纬度
				+ "    if(isNotA5NotPos == 1 and oldLon~= 0 and oldLat ~= 0) then " 
				+ "        dataNewObj.lon=oldLon;" 
				+ "        dataNewObj.lat=oldLat;" 
				+ "    end"
				
		       
		       //****(2.4.1)原来REDIS有数据且当前数据定位，直接把最后定位时间写入REDIS
		       + "    if(lastPosTime ~= 0) then "
		       + "        newExtend['lastPosTime']=lastPosTime;"	
		       
				// 原来REDIS有数据且当前不定位，取REDIS中的最后定位时间
				// 如REDIS中也没有最后定位时间，那么不需要设置最后定位时间
				+ "    else"
				
				//****(2.4.2)
				+ "        local oldExtend = oldData['extend'];"
				+ "            if(nil ~= oldExtend) then " 
				+ "                local oldLastPosTime = oldExtend['lastPosTime'];"
				+ "                if(nil ~= oldLastPosTime) then " 
				+ "                     newExtend['lastPosTime'] = oldLastPosTime;"	
				+ "                end"
				+ "            end"
				+ "    end"
				
				//****(2.5)如果是A5M终端类型，如果不定位则取上次定位经纬度，取上次定位终端时间，取上次的定位状态(isPos维持原来值)
				+ "    if(terTypeNum == 1 and isPos ==0 and oldLon~= 0 and oldLat ~= 0) then " 
				+ "        dataNewObj.lon=oldLon;" 
				+ "        dataNewObj.lat=oldLat;" 
				+ "        dataNewObj.isPos=oldIsPos;" 
				+ "        local oldExtend = oldData['extend'];"
				+ "            if(nil ~= oldExtend) then " 
				+ "                local oldLastPosTime = oldExtend['lastPosTime'];"
				+ "                if(nil ~= oldLastPosTime) then " 
				+ "                     newExtend['lastPosTime'] = oldLastPosTime;"	
				+ "                end"
				+ "            end"
				+ "    end"
				
				//****(2.6)最终写入Redis
				+ "        newData=cjson.encode(dataNewObj);" 
				+ "        redis.call('HSET',gid,vid,newData);" 
				//****(3)*****
				+ "end "
				
				//****(4)返回激活判断值*****
				+ "if(posJsonOld == false) then "
				+ "    return 0;"
				+ "else "
				+ "    return 1;"
				+ "end " ;
		return cmdStr;
	}
	
	/**
	 * 获取监控车辆上下行数据的脚本
	 * @return
	 */
	public static String getMoniterDataLuaScript() {
		String luaStr = "local terminalNo = KEYS[1];" 
				+ "local startIndex = ARGV[1];" 
				+ "local endIndex = ARGV[2];"
				+ "local retList = redis.call('LRANGE',terminalNo,startIndex,endIndex);"
				+ "if(retList ~= nil) then "
				+    "redis.call('DEL',terminalNo);"
				+ "end "
				+ "return retList";
		return luaStr;
	}

	public void scriptParser(JedisCluster jc, String script) {
		if (jc == null || script == null || "".equals(script)) {
			logger.error("参数错误.");
		}

		Map<String, JedisPool> nodes = jc.getClusterNodes();
		if (nodes != null) {
			Iterator<Entry<String, JedisPool>> iter = nodes.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, JedisPool> entry = iter.next();
				String key = entry.getKey();
				JedisPool jedisPool = entry.getValue();
				Jedis jedis = jedisPool.getResource();
				try {
					String shaStr = jedis.scriptLoad(script);
					if (sha == null && shaStr != null) {
						sha = shaStr;
					}
				} catch (Exception e) {
					logger.error("解析LUA脚本失败：" + ExceptionUtil.getStackStr(e));
				} finally {
					jedis.close();
				}
			}
		}
	}
}
