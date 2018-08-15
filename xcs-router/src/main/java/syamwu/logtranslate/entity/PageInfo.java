package syamwu.logtranslate.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PageInfo implements Serializable {

    public static final Integer DEFAULT_PAGE = 1;

    public static final Integer DEFAULT_LIMIT = 10;

    private Long total;       // 总记录数
    private Integer pageNum;  // 第几页
    private Integer pageSize; // 每页记录数
    private Integer pages;    // 总页数
    private Integer size;     // 当前页的数量 <= pageSize，该属性来自ArrayList的size属性

    public PageInfo() {

    }

    public PageInfo(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
