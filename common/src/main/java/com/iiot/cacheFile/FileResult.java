package com.iiot.cacheFile;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/18.
 */
public class FileResult {

    private boolean next = true;

    private Map<String,List<byte[]>> data;

    private List<byte[]> dataList;

    public List<byte[]> getDataList() {
        return dataList;
    }

    public void setDataList(List<byte[]> dataList) {
        this.dataList = dataList;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public Map<String, List<byte[]>> getData() {
        return data;
    }

    public void setData(Map<String, List<byte[]>> data) {
        this.data = data;
    }
}
