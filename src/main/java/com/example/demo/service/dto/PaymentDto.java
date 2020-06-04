package com.example.demo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentDto {
	
	private Long id;
	
	private String crdtCardNum;
	
	private String validYm;
	
	private String cvc;
	
	private String encryptedCardInfo;
	
	private Long installment;
	
	private Long payAmount;
	
	private Long cancelAmount;
	
	private Long payVat;
	
	private Long cancelVat;
	
	private String apprYn;
	
	private String instDtm;
	 
}
