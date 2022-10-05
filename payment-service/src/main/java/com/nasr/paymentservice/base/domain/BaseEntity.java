package com.nasr.paymentservice.base.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity <ID extends Serializable> implements Serializable{

    @Id
    protected ID id;

    @Transient
    protected Boolean isDeleted;
}
