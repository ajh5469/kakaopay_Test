package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.common.exception.commonException;
import com.example.demo.common.util.CryptoUtil;
import com.example.demo.domain.payment.DataTransferRepository;
import com.example.demo.domain.payment.Payment;
import com.example.demo.domain.payment.PaymentCancel;
import com.example.demo.domain.payment.DataTransfer;
import com.example.demo.domain.payment.PaymentCancelRepository;
import com.example.demo.domain.payment.PaymentRepository;
import com.example.demo.service.dto.PaymentDto;

@Service
public class PaymentService {
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PaymentCancelRepository pamentCancelRepository;
	
	@Autowired
	private DataTransferRepository dataTransferRepository;
	
	/**
     * 신규거래 등록
     * @param paymentDto
     */
	@Transactional
	public Map<String, Object> postPaymentData(PaymentDto paymentDto) throws Exception{	
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String cardInfo = paymentDto.getCrdtCardNum() + "|" + paymentDto.getValidYm() + "|" + paymentDto.getCvc(); 
		Payment payment = new Payment();
		DataTransfer dataForTransfer = new DataTransfer();
		String data = "_446PAYMENT";
		
		try {
			payment.setEncryptedCardInfo(CryptoUtil.encrypt(cardInfo));
			
		}catch(Exception e){
			throw new commonException(HttpStatus.UNPROCESSABLE_ENTITY, "Encrypt Fail");
		}
		
		payment.setCrdtCardNum(paymentDto.getCrdtCardNum());
		payment.setValidYm(paymentDto.getValidYm());
		payment.setCvc(paymentDto.getCvc());
		payment.setPayAmount(paymentDto.getPayAmount());
		payment.setInstallment(paymentDto.getInstallment());
		payment.setCancelAmount(0L);
		payment.setCancelVat(0L);
		
		if(paymentDto.getPayVat() != null) {
			payment.setPayVat(paymentDto.getPayVat());
		}else {
			payment.setPayVat((long)(Math.round(paymentDto.getPayAmount()/11)));
		}
		
		Payment newPayment = paymentRepository.save(payment);                      //save Data
		
		outputMap.put("ID", newPayment.getId());
		outputMap.put("AMOUNT", newPayment.getPayAmount());
		outputMap.put("INSERT_DTM", newPayment.getInsertDtm());
		outputMap.put("VAT", newPayment.getPayVat());
		
		data += String.format("%-20s", newPayment.getId().toString());
		data += String.format("%-20s", newPayment.getCrdtCardNum());
		data += String.format("%02d", newPayment.getInstallment());
		data += String.format("%-4s", newPayment.getValidYm());
		data += String.format("%-3s", newPayment.getCvc());
		data += String.format("%10s", newPayment.getPayAmount().toString());
		data += String.format("%-10s", newPayment.getPayVat().toString());
		data += String.format("%-20s", "");
		data += String.format("%-300s", newPayment.getEncryptedCardInfo());
		data += String.format("%-47s", "");
				
		dataForTransfer.setTransferData(data);
		dataForTransfer.setSuccessYn("Y");  //현재는 모두 전송에 성공한 것으로 가정
		dataTransferRepository.save(dataForTransfer);
		
		return outputMap;
	};
	
	/**
     * 거래 조회
     * @param paymentDto
     */
	public Map<String, Object> getPaymentData(PaymentDto paymentDto){	
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> cardInfo  = new HashMap<String, Object>();
		Map<String, Object> amountInfo  = new HashMap<String, Object>();
		Long cancelAmt;
		
		Long id = paymentDto.getId();
		Optional<Payment> payment = paymentRepository.findById(id);
		
		try{
			String decryptedInfo = CryptoUtil.decrypt(payment.get().getEncryptedCardInfo());
			String[] dataArr = decryptedInfo.split("\\|");
			
			char[] charList = dataArr[0].toCharArray();
			for(int i =6; i<dataArr[0].length()-3; i++){
		          charList[i]='*';
		        }
			String maskedCardNum = new String(charList);
			
			cardInfo.put("CREDIT_CARD_NUMBER", maskedCardNum);
			cardInfo.put("Valid_YM", dataArr[1]);
			cardInfo.put("CVC", dataArr[2]);

		}catch(Exception e){
			throw new commonException(HttpStatus.UNPROCESSABLE_ENTITY, "Decrypt Fail");
		}
		
		
		if(payment.get().getCancelAmount() == null ) {
			cancelAmt = 0L;
		}else {
			cancelAmt = payment.get().getCancelAmount();
		}
		
		if(payment.get().getPayAmount().longValue() > cancelAmt.longValue()) {
			outputMap.put("PaymentType", "Pay");
			outputMap.put("Initial_Payment_Dtm", payment.get().getInsertDtm());
			
			amountInfo.put("Pay_Amount", payment.get().getPayAmount() - cancelAmt);
		} else {
			outputMap.put("PaymentType", "Cancel");
			outputMap.put("Initial_Payment_Dtm", payment.get().getInsertDtm());
			outputMap.put("Last_Payment_Cancel_Dtm", payment.get().getModifyDtm());
			
			amountInfo.put("Cancel_Amount", cancelAmt);
		}
		
		amountInfo.put("VAT", payment.get().getPayVat());
		
		outputMap.put("ID", payment.get().getId());
		outputMap.put("CARD_INFO", cardInfo);
		outputMap.put("Amount_INFO", amountInfo);
		
		return outputMap;
	}
	
	/**
     * 결제 취소(전체/부분)
     * @param paymentDto
     */
	public Map<String, Object> putPaymentData(PaymentDto paymentDto){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Long paymentID = paymentDto.getId();
		Optional<Payment> payment = paymentRepository.findById(paymentID);
		
		Payment newPayment = new Payment();
		PaymentCancel paymentCancel = new PaymentCancel();
		
		Long totalAmt = payment.get().getPayAmount() - payment.get().getCancelAmount();
		Long totalVat = payment.get().getPayVat() - payment.get().getCancelVat();
		Long cancelAmt = 0L;
		Long cancelVat = 0L;
		
		DataTransfer dataForTransfer = new DataTransfer();
		String data = "_446CANCEL";
		
		
		//이미 전체취소된 건인지 우선 확인
		if(totalAmt.longValue() == 0L) {
			throw new commonException(HttpStatus.BAD_REQUEST, "Canceled Already");
		}
		
		cancelAmt = paymentDto.getCancelAmount();
		
		if(paymentDto.getCancelVat() == null) {
			if(totalAmt.longValue() != cancelAmt.longValue()){
				cancelVat = (long) Math.round(cancelAmt/11);
			}else {
				cancelVat = totalVat;
			}
			
		}else {
			cancelVat = paymentDto.getCancelVat();
		}

		if(totalAmt.longValue() < cancelAmt.longValue()) {
			throw new commonException(HttpStatus.BAD_REQUEST, "Too much Amount");
			
		}else {
			if(totalAmt.longValue() == cancelAmt.longValue()){
				payment.get().setCancelAmount(payment.get().getCancelAmount()+cancelAmt);
				
				if(totalVat.longValue() != cancelVat.longValue()) {
					throw new commonException(HttpStatus.BAD_REQUEST, "Check the VAT Amount");
				}else {
					payment.get().setCancelVat(payment.get().getCancelVat()+cancelVat);
				}
			}else {
				payment.get().setCancelAmount(payment.get().getCancelAmount()+cancelAmt);
				
				if(totalVat.longValue() < cancelVat.longValue()) {
					throw new commonException(HttpStatus.BAD_REQUEST, "Too much VAT");
				}else {
					payment.get().setCancelVat(payment.get().getCancelVat() + cancelVat);
				}
			}
		}
		paymentCancel.setPaymentId(payment.get().getId());
		paymentCancel.setCancelAmount(cancelAmt);
		paymentCancel.setVat(cancelVat);
		
		newPayment = paymentRepository.save(payment.get());
		pamentCancelRepository.save(paymentCancel);
		
		data += String.format("%-20s", newPayment.getId().toString());
		data += String.format("%-20s", newPayment.getCrdtCardNum());
		data += String.format("%02d", newPayment.getInstallment());
		data += String.format("%-4s", newPayment.getValidYm());
		data += String.format("%-3s", newPayment.getCvc());
		data += String.format("%10s", paymentCancel.getCancelAmount().toString());
		data += String.format("%-10s", paymentCancel.getVat().toString());
		data += String.format("%-20s", newPayment.getId().toString());
		data += String.format("%-300s", newPayment.getEncryptedCardInfo());
		data += String.format("%-47s", "");
				
		dataForTransfer.setTransferData(data);
		dataForTransfer.setSuccessYn("Y");  //현재는 모두 전송에 성공한 것으로 가정
		dataTransferRepository.save(dataForTransfer);
		
		
		outputMap.put("ID", newPayment.getId());
		outputMap.put("Payment_Cancel_Dtm", newPayment.getModifyDtm());
		outputMap.put("Cancel_Amount", newPayment.getCancelAmount());
		outputMap.put("Cancel_VAT", newPayment.getCancelVat());
		
		return outputMap;
	}
}
