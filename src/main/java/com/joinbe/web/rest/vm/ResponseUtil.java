package com.joinbe.web.rest.vm;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class ResponseUtil {

    private static final Integer RESPONSE_CODE_SUCCESS = 20000;

    private static <T> PageData<T> pageToResult(Page <T> page) {
        PageData<T> result = new PageData<>();
        result.setItems(page.getContent());
        result.setTotal(page.getTotalElements());
        return result;
    }

    public static <X> ResponseEntity<PageData<X>> toPageData(Page<X> page) {
        return new ResponseEntity (pageToResult(page), HttpStatus.OK);
    }

    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, null);
    }

    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse, HttpHeaders header) {
        return maybeResponse.map(response -> ResponseEntity.ok().headers(header).body(response))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
