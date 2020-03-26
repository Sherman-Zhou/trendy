package com.joinbe.common.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Filter implements Serializable {

    private static final long serialVersionUID = -8712382358441065075L;

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public enum Operator {

        /**
         * 等于
         */
        eq(" = "),

        /**
         * 不等于
         */
        ne(" != "),

        /**
         * 大于
         */
        gt(" > "),

        /**
         * 小于
         */
        lt(" < "),

        /**
         * 大于等于
         */
        ge(" >= "),

        /**
         * 小于等于
         */
        le(" <= "),

        /**
         * 类似
         */
        like(" like "),

        /**
         * 包含
         */
        in(" in "),

        /**
         * 为Null
         */
        isNull(" is NULL "),

        /**
         * 不为Null
         */
        isNotNull(" is not NULL "),

        between(" Between");

        Operator(String operator) {
            this.operator = operator;
        }

        private String operator;

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }

    public enum DataType {
        text,
        select,
        date,
        daterange,
        year
    }

    private static final boolean DEFAULT_IGNORE_CASE = false;


    private String path;


    private Operator operator;

    private DataType type;


    private Object value;


    private Boolean ignoreCase = DEFAULT_IGNORE_CASE;


    public Filter() {
    }


    public Filter(String path, Operator operator, Object value) {
        this.path = path;
        this.operator = operator;
        this.value = value;
    }

    public Filter(String path, Operator operator, Object value, DataType type) {
        this.path = path;
        this.operator = operator;
        this.value = value;
        this.type = type;
    }

    public Filter(String path, String operator, Object value) {
        this.path = path;
        this.operator = Operator.valueOf(operator);
        this.value = value;
    }

    public Filter(String path, Operator operator, Object value, boolean ignoreCase) {
        this.path = path;
        this.operator = operator;
        this.value = value;
        this.ignoreCase = ignoreCase;
    }


    public static Filter eq(String path, Object value) {
        return new Filter(path, Operator.eq, value);
    }


    public static Filter eq(String path, Object value, boolean ignoreCase) {
        return new Filter(path, Operator.eq, value, ignoreCase);
    }


    public static Filter ne(String path, Object value) {
        return new Filter(path, Operator.ne, value);
    }


    public static Filter ne(String path, Object value, boolean ignoreCase) {
        return new Filter(path, Operator.ne, value, ignoreCase);
    }


    public static Filter gt(String path, Object value) {
        return new Filter(path, Operator.gt, value);
    }


    public static Filter lt(String path, Object value) {
        return new Filter(path, Operator.lt, value);
    }


    public static Filter ge(String path, Object value) {
        return new Filter(path, Operator.ge, value);
    }


    public static Filter le(String path, Object value) {
        return new Filter(path, Operator.le, value);
    }


    public static Filter like(String path, Object value) {
        return new Filter(path, Operator.like, value);
    }


    public static Filter in(String path, Object value) {
        return new Filter(path, Operator.in, value);
    }


    public static Filter isNull(String path) {
        return new Filter(path, Operator.isNull, null);
    }


    public static Filter isNotNull(String path) {
        return new Filter(path, Operator.isNotNull, null);
    }


    public Filter ignoreCase() {
        this.ignoreCase = true;
        return this;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public Operator getOperator() {
        return operator;
    }


    public void setOperator(Operator operator) {
        this.operator = operator;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public Boolean getIgnoreCase() {
        return ignoreCase;
    }


    public void setIgnoreCase(Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Filter other = (Filter) obj;
        return new EqualsBuilder().append(getPath(), other.getPath()).append(getOperator(), other.getOperator()).append(getValue(), other.getValue()).isEquals();
    }


    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getPath()).append(getOperator()).append(getValue()).toHashCode();
    }

    @Override
    public String toString() {
        return "Filter{" +
            "path='" + path + '\'' +
            ", operator=" + operator +
            ", type=" + type +
            ", value=" + value +
            ", ignoreCase=" + ignoreCase +
            '}';
    }
}
