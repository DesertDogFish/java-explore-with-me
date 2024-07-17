package ru.practicum.explorewithme.ewmservice.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestHelper extends PageRequest {
    private final int from;

    public PageRequestHelper(int from, int size, Sort sort) {
        super(from / size, size, sort == null ? Sort.unsorted() : sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}
