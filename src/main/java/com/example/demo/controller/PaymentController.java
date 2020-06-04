package com.example.demo.controller;

import lombok.AllArgsConstructor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.common.exception.commonException;
import com.example.demo.service.PaymentService;
import com.example.demo.service.dto.PaymentDto;

@AllArgsConstructor
@RequestMapping(value="/payment")
@RestController
public class PaymentController {
	
	private PaymentService paymentService;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
    public Map<String, Object> postPayment(@RequestBody PaymentDto paymentDto){
		if(paymentDto.getCrdtCardNum() == null || paymentDto.getCvc() == null 
				|| paymentDto.getInstallment() == null || paymentDto.getPayAmount() == null || paymentDto.getValidYm() == null) {
			throw new commonException(HttpStatus.BAD_REQUEST, "Parameter Error");
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			result = paymentService.postPaymentData(paymentDto);
		}catch(Exception e){
			
		}
        
		return result;

    }
	
	//결제내역조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getPayment(@RequestBody PaymentDto paymentDto){
		if(paymentDto.getId() == null) {
			throw new commonException(HttpStatus.BAD_REQUEST, "Parameter Error");
		}

        return paymentService.getPaymentData(paymentDto);
    }
	
	//결제취소(전체/부분)
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
    public Map<String, Object> putPayment(@RequestBody PaymentDto paymentDto){
		if(paymentDto.getCancelAmount() == null) {
			throw new commonException(HttpStatus.BAD_REQUEST, "Parameter Error");
		}
		
		if(paymentDto.getCancelVat() != null && paymentDto.getCancelVat() > paymentDto.getCancelAmount()) {
			throw new commonException(HttpStatus.BAD_REQUEST, "Vat is bigger than Amout");
		}

        return paymentService.putPaymentData(paymentDto);

    }
}
