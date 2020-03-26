package com.joinbe.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joinbe.service.util.SpringContextUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class QueryParams<T> implements Specification<T> {

    private final Logger log = LoggerFactory.getLogger(QueryParams.class);

    private static final String PROPERTY_SEPARATOR = ".";

    private static final String SQL_LIKE_PATTERN = "%%%s%%";

    private static final String ENCODING_UTF8 = "UTF-8";

    private List<Filter> andFilters = new ArrayList<>();

    private boolean isDistinct = false;

    public QueryParams() {
    }

    public QueryParams(String params) {
        if (StringUtils.isNotEmpty(params)) {
            toFilters(params);
        }

    }

    public QueryParams(String params, List<Filter> additionalFilters) {
        if (StringUtils.isNotEmpty(params)) {
            toFilters(params);
        }
        if (!CollectionUtils.isEmpty(additionalFilters)) {
            this.andFilters.addAll(additionalFilters);
        }

    }

    public void setDistinct(boolean distinct) {
        isDistinct = distinct;
    }

    private void toFilters(String params) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String decodedParams = URLDecoder.decode(new String(Base64.getDecoder().decode(params), ENCODING_UTF8), ENCODING_UTF8);
            log.debug("filter params: {}", decodedParams);
            List<Filter> filters = (mapper.readValue(decodedParams, new TypeReference<List<Filter>>() {
            }));
            andFilters.addAll(filters.stream().filter(filter -> isNotEmpty(filter.getValue())).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <X> Path<X> getPath(Path<?> path, String propertyPath) {
        if (path == null || StringUtils.isEmpty(propertyPath)) {
            return (Path<X>) path;
        }
        String property = StringUtils.substringBefore(propertyPath, PROPERTY_SEPARATOR);
        return getPath(path.get(property), StringUtils.substringAfter(propertyPath, PROPERTY_SEPARATOR));
    }


    @SuppressWarnings("unchecked")
    public Predicate applySearchConditions(Root<T> root, CriteriaBuilder criteriaBuilder, Predicate restrictions) {

        if (root == null || CollectionUtils.isEmpty(andFilters)) {
            return restrictions;
        }
        for (Filter filter : andFilters) {
            if (filter == null) {
                continue;
            }
            String key = filter.getPath();
            Filter.Operator operator = filter.getOperator();
            Boolean ignoreCase = filter.getIgnoreCase();
            Path<?> path = getPath(root, key);
            if (path == null) {
                continue;
            }
            log.debug("JPA model (properties->java type : {}->{})", key, path.getJavaType());
            Object value = convertToJPAType(filter.getValue(), path.getJavaType());
            switch (operator) {
                case eq:
                    if (isNotEmpty(value)) {
                        if (BooleanUtils.isTrue(ignoreCase) && String.class.isAssignableFrom(path.getJavaType()) && value instanceof String) {
                            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(criteriaBuilder.lower((Path<String>) path),
                                ((String) value).toLowerCase()));
                        } else {
                            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(path, value));
                        }
                    } else {
                        restrictions = criteriaBuilder.and(restrictions, path.isNull());
                    }
                    break;
                case ne:
                    if (value != null) {
                        if (BooleanUtils.isTrue(ignoreCase) && String.class.isAssignableFrom(path.getJavaType()) && value instanceof String) {
                            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(criteriaBuilder.lower((Path<String>) path),
                                ((String) value).toLowerCase()));
                        } else {
                            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(path, value));
                        }
                    } else {
                        restrictions = criteriaBuilder.and(restrictions, path.isNotNull());
                    }
                    break;
                case gt:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && value instanceof Number) {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.gt((Path<Number>) path, (Number) value));
                    }
                    break;
                case lt:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && value instanceof Number) {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lt((Path<Number>) path, (Number) value));
                    }
                    break;
                case ge:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && value instanceof Number) {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge((Path<Number>) path, (Number) value));
                    }
                    break;
                case le:
                    if (Number.class.isAssignableFrom(path.getJavaType()) && value instanceof Number) {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le((Path<Number>) path, (Number) value));
                    }
                    break;
                case like:
                    if (String.class.isAssignableFrom(path.getJavaType()) && value instanceof String) {
                        String likeValue = String.format(SQL_LIKE_PATTERN, value);
                        if (BooleanUtils.isTrue(ignoreCase)) {
                            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(criteriaBuilder.lower((Path<String>) path),
                                (likeValue).toLowerCase()));
                        } else {
                            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like((Path<String>) path, likeValue));
                        }
                    }
                    break;
                case in:
                    restrictions = criteriaBuilder.and(restrictions, path.in(value));
                    break;
                case isNull:
                    restrictions = criteriaBuilder.and(restrictions, path.isNull());
                    break;
                case isNotNull:
                    restrictions = criteriaBuilder.and(restrictions, path.isNotNull());
                    break;
                case between:
                    if (Instant.class.isAssignableFrom(path.getJavaType()) && value instanceof List && ((List) value).size() >= 2) {
                        List<Instant> values = (List) value;
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.between((Expression) path, criteriaBuilder.literal(values.get(0)),
                            criteriaBuilder.literal(values.get(1))));
                    } else if (Date.class.isAssignableFrom(path.getJavaType()) && value instanceof List && ((List) value).size() >= 2) {
                        List<Date> values = (List) value;
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.between((Expression) path, criteriaBuilder.literal(values.get(0)),
                            criteriaBuilder.literal(values.get(1))));
                    }

                    break;
                default:
                    log.warn("no match condition");
            }
        }
        return restrictions;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.distinct(this.isDistinct);
        Predicate restrictions = cb.conjunction();
        return cb.and(applySearchConditions(root, cb, restrictions));
    }

    public QueryParams and(String path, Filter.Operator operator, Object value) {
        if (value instanceof String) {
            value = ((String) value).trim();
        }
        this.andFilters.add(new Filter(path, operator, value));
        return this;
    }

    public QueryParams and(String path, Filter.Operator operator, Object value, boolean ignoreCase) {
        if (value instanceof String) {
            value = ((String) value).trim();
        }
        this.andFilters.add(new Filter(path, operator, value, ignoreCase));
        return this;
    }

    public QueryParams and(Filter filter) {
        this.andFilters.add(filter);
        return this;
    }

    public QueryParams and(Filter... filter) {
        this.andFilters.addAll(Arrays.asList(filter));
        return this;
    }

    private boolean isNotEmpty(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String) {
            return StringUtils.isNotEmpty((String) value);
        } else if (value instanceof Collection) {
            if (!CollectionUtils.isEmpty((Collection) value)) {
                Collection values = (Collection) value;
                for (Object item : values) {
                    if (item == null || StringUtils.isEmpty(item.toString())) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
            //2018-11-07T16:00:00.000Z
        }
        return true;
    }

    private Object convertToJPAType(Object value, Class type) {

        FormattingConversionService conversionService = SpringContextUtils.getBean(FormattingConversionService.class);
        if (value instanceof Collection) {
            Collection values = (Collection) value;
            List<Object> newValues = new ArrayList<>(values.size());
            for (Object item : values) {
                newValues.add(conversionService.convert(item, type));
            }
            return newValues;
        } else {
            return conversionService.convert(value, type);
        }
    }


    @Override
    public String toString() {
        return "QueryParams{" +
            "andFilters=" + andFilters +
            ", isDistinct=" + isDistinct +
            '}';
    }
}

