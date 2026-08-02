package com.openfree_api.common.response;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;

    public PageResponse() {
    }

    public static <E, T> PageResponse<T> from(
            Page<E> source,
            Function<E, T> mapper
    ) {

        PageResponse<T> response = new PageResponse<>();

        response.setContent(
                source.getContent()
                        .stream()
                        .map(mapper)
                        .toList()
        );

        response.setPage(source.getNumber());
        response.setSize(source.getSize());
        response.setTotalElements(source.getTotalElements());
        response.setTotalPages(source.getTotalPages());
        response.setFirst(source.isFirst());
        response.setLast(source.isLast());
        response.setEmpty(source.isEmpty());

        return response;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}