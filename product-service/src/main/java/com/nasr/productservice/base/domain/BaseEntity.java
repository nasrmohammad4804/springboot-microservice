package com.nasr.productservice.base.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity<ID extends Serializable> implements Serializable {

    @Id
    protected ID id;

    @Transient
    protected Boolean isDeleted;

    public BaseEntity(ID id) {
        this.id = id;
    }
}
