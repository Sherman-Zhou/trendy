package com.joinbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DynamicJpqlRepository<T> {

    Page<T> findByHql(Pageable pageable, String hql, String countHql, Map<String, Object> parameters);

    List<T> findByHql(String hql, Map<String, Object> parameters);
}
