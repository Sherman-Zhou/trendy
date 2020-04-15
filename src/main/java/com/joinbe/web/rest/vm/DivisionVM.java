package com.joinbe.web.rest.vm;

import com.joinbe.domain.enumeration.RecordStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class DivisionVM implements Serializable {

    @Size(max = 80)
    private String name;

    @Size(max = 200)
    private String description;

    @Size(max = 20)
    private String code;

    private RecordStatus status;
}
