package com.joinbe.data.collector.service.dto;

public class ResponseForPageDTO extends ResponseDTO {

    private Long totalCount;
    private Integer pageNum;
    private Integer pageSize;

    public ResponseForPageDTO(int code, String message) {
        super(code, message);
    }

    public ResponseForPageDTO(int code, String message, Long totalCount, Integer pageNum, Integer pageSize) {
        super(code, message);
        this.totalCount = totalCount;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
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

    @Override
    public String toString() {
        return "ResponseForPageDTO{" +
            "totalCount=" + totalCount +
            ", pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            "} " + super.toString();
    }
}
