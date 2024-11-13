package com.obsidi.feedapp.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public class PageResponse<T> {

    private int pageNum;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<T> content;

    /**
     * Constructor that initializes PageResponse based on a Page object.
     *
     * @param paged Page object containing pagination information and content.
     */
    public PageResponse(Page<T> paged) {
        if (Optional.ofNullable(paged).isPresent()) {
            this.pageNum = paged.getNumber();
            this.pageSize = paged.getSize();
            this.totalPages = paged.getTotalPages();
            this.totalElements = paged.getTotalElements();
            this.content = paged.getContent();
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

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
