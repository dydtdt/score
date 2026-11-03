package com.grade.util;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页对象。
 *
 * @param <T> 列表元素类型
 */
public class Page<T> {

    private int pageNum = 1;
    private int pageSize = 10;
    private long total;
    private List<T> list = Collections.emptyList();

    public Page() {
    }

    public Page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /** 把前端传来的页码/每页条数修正到合法范围 */
    public static int normalize(String value, int defaultValue, int min, int max) {
        try {
            int v = Integer.parseInt(value);
            if (v < min) {
                return min;
            }
            if (v > max) {
                return max;
            }
            return v;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list == null ? Collections.<T>emptyList() : list;
    }

    /** 总页数（至少 1 页） */
    public int getTotalPages() {
        if (pageSize <= 0) {
            return 1;
        }
        long pages = (total + pageSize - 1) / pageSize;
        return (int) Math.max(pages, 1);
    }

    public boolean isHasPrev() {
        return pageNum > 1;
    }

    public boolean isHasNext() {
        return pageNum < getTotalPages();
    }

    public int getPrevPage() {
        return Math.max(pageNum - 1, 1);
    }

    public int getNextPage() {
        return Math.min(pageNum + 1, getTotalPages());
    }

    /** SQL 的 offset */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
