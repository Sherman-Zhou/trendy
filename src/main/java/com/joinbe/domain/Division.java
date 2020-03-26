package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.RecordStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * A Department.
 */
@Entity
@Table(name = "division")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = false)
public class Division extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @Size(max = 20)
    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "status")
    private RecordStatus status;
    @Column(name = "parent_id", updatable = false, insertable = false)
    private Long parentId;


    @ManyToOne
    @JsonIgnoreProperties("divisions")
    @TableField(exist = false)
    private Division parent;

    @OneToMany(mappedBy = "parent")
    @TableField(exist = false)
    private List<Division> children = new ArrayList<>();

}
