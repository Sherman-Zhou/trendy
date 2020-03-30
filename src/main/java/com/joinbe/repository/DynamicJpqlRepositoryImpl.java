package com.joinbe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicJpqlRepositoryImpl<T> implements DynamicJpqlRepository<T> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<T> findByHql(Pageable pageable, String hql, String countHql, Map<String, Object> parameters) {
        Page page = null;
        Query totalQuery = em.createQuery(countHql);

        applyParameters(totalQuery, parameters);
        Long totalRecords = (Long) totalQuery.getSingleResult();
        Query query = em.createQuery(hql);
        applyParameters(query, parameters);
        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());
        List<T> result = query.getResultList();
        page = new PageImpl(result, pageable, totalRecords);
        return page;
    }

    @Override
    public List<T> findByHql(String hql, Map<String, Object> parameters) {
        Query query = em.createQuery(hql);
        applyParameters(query, parameters);
        return query.getResultList();
    }

    private void applyParameters(Query query, Map<String, Object> parameters) {
        if (parameters != null) {
            Set<String> keys = parameters.keySet();
            for (String key : keys) {
                query.setParameter(key, parameters.get(key));
            }
        }
    }
}
