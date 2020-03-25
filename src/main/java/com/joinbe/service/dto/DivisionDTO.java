package com.joinbe.service.dto;

import com.joinbe.domain.enumeration.RecordStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = false)
public class DivisionDTO implements Serializable {

    private Long id;

    @Size(max = 80)
    private String name;

    @Size(max = 200)
    private String description;

    @Size(max = 20)
    private String code;

    private RecordStatus status;

    private Long parentId;

}
