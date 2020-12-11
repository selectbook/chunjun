package com.iiot.util;

import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */
public class Pager<E> {
    /**
     * 每页最大记录数限制
     */
    public static final Integer MAX_PAGE_SIZE = Integer.MAX_VALUE;

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 每页记录数
     */
    private Integer pageSize = 20;

    /**
     * 总记录数
     */
    private Integer totalCount = 0;

    /**
     * 总页数
     */
    private Integer pageCount = 0;

    /**
     * 数据List
     */
    private List<E> list;

    /**
     * 排序字段
     */
    protected String orderColumns;

    public boolean hasNext() {
        if (this.pageCount > this.currentPage) {
            return true;
        }
        return false;
    }

    public boolean hasForward() {
        if (this.currentPage <= 1) {
            return false;
        }
        return true;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage < 1 ? 0 : currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize < 1 ? 1 : pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        this.pageCount = (totalCount + pageSize - 1) / pageSize;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public String getOrderColumns() {
        return orderColumns;
    }

    public void setOrderColumns(String orderColumns) {
        this.orderColumns = orderColumns;
    }
}
