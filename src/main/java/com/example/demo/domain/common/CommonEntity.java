package com.example.demo.domain.common;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CommonEntity {
	
	@Column(nullable = false, updatable = false)
	@CreatedDate
	private LocalDateTime insertDtm;
	
	@Column(nullable = false)
	@LastModifiedDate
	private LocalDateTime modifyDtm;
	
}
