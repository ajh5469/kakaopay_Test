package com.example.demo.domain.payment;

import com.example.demo.domain.common.CommonEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@Entity
@DynamicInsert 
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment extends CommonEntity{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(length=20, nullable=false)
	private String crdtCardNum;
	
	@Column(length=4, nullable=false)
	private String validYm;
	
	@Column(length=3, nullable=false)
	private String cvc;
	
	@Column(nullable=false)
	private String encryptedCardInfo;
	
	@Column(length=2, nullable=false)
	private Long installment;
	
	@Column(length=10, nullable=false)
	private Long payAmount;

	@Column(length=10)
	private Long cancelAmount;
	
	@Column(length=10, nullable=false)
	private Long payVat;
	
	@Column(length=10)
	private Long cancelVat;
	
	@Column
	private String note;
}
