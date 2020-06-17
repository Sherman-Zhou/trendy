package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public   class ResponseDTO implements Serializable {

        private int code;
        private String message;

        public ResponseDTO(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "ResponseDTO{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
