package bean;

import java.util.List;

public class Page<T> {

    /**
     * 列表属性
     */
    private List<T> list;

    private int totalRecordsNum;

    private int currentPageNum;

    private int totalPageNum;

    private int prevPageNum;

    private int nextPageNum;

    /**
     * 对于一个final变量，如果是基本数据类型的变量，则其数值一旦在初始化之后便不能更改；
     * 如果是引用类型的变量，则在对其初始化之后便不能再让其指向另一个对象。
     */
    private static final int PAGE_SIZE = 5;

    public List<T> getList() {
        return list;
    }


    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalRecordsNum() {
        return totalRecordsNum;
    }

    public void setTotalRecordsNum(int totalRecordsNum) {
        this.totalRecordsNum = totalRecordsNum;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public int getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(int totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public int getPrevPageNum() {
        return prevPageNum;
    }

    public void setPrevPageNum(int prevPageNum) {
        this.prevPageNum = prevPageNum;
    }

    public int getNextPageNum() {
        return nextPageNum;
    }

    public void setNextPageNum(int nextPageNum) {
        this.nextPageNum = nextPageNum;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public String toString() {
        return "Page{" +
                "list=" + list +
                ", totalRecordsNum=" + totalRecordsNum +
                ", currentPageNum=" + currentPageNum +
                ", totalPageNum=" + totalPageNum +
                ", prevPageNum=" + prevPageNum +
                ", nextPageNum=" + nextPageNum +
                '}';
    }
}
