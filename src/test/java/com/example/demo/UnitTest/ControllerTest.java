package com.example.demo.UnitTest;

import com.example.demo.domain.payment.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {

	@Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
	@Test
    public void createPayment() throws Exception {
        //��û ����
        Payment payment = Payment.builder()
                .crdtCardNum("9490100100166706")
                .cvc("946")
                .installment(0L)
                .payAmount(10000L)
                .payVat(1000L)
                .validYm("0222")
                .build();

        mockMvc.perform(post("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payment)))
                .andDo(print()) 			// ��� ����� ��û�� ������ �� �� ����
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.AMOUNT").value(Matchers.hasToString("10000")))
                .andExpect(jsonPath("$.INSERT_DTM").exists())
                .andExpect(jsonPath("$.VAT").value(Matchers.hasToString("1000")))
                ;
    }
	
	@Test
    public void createPayment_badRequest() throws Exception {

        mockMvc.perform(post("/payment")
                	.contentType(MediaType.APPLICATION_JSON)
                	.accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;

    }
	
	@Test
    public void selectPayment() throws Exception {
		
		Payment payment = Payment.builder()
                .crdtCardNum("9490100100166706")
                .cvc("946")
                .installment(0L)
                .payAmount(10000L)
                .payVat(1000L)
                .validYm("0222")
                .build();

		mockMvc.perform(post("/payment")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(payment)))
           .andDo(print()) 			
           .andExpect(status().isOk())
           ;
        
		Payment newPayment = Payment.builder()
                .id(1L)
                .build();

        mockMvc.perform(get("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.CARD_INFO").exists())
                .andExpect(jsonPath("$.Amount_INFO").exists())
                .andExpect(jsonPath("$.PaymentType").value(Matchers.hasToString("Cancel")))
                ;
    }
	
	@Test
    public void cancelPaymentAll() throws Exception {
		
		Payment payment = Payment.builder()
                .crdtCardNum("9490100100166706")
                .cvc("946")
                .installment(0L)
                .payAmount(40000L)
                .payVat(1000L)
                .validYm("0222")
                .build();

		mockMvc.perform(post("/payment")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(payment)))
           .andDo(print()) 			
           .andExpect(status().isOk())
           ;
        
		Payment newPayment = Payment.builder()
                .id(5L)
                .cancelAmount(40000L)
                .cancelVat(1000L)
                .build();

        mockMvc.perform(put("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
                .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("40000")))
                .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("1000")))
                ;
    }
	
	@Test
    public void cancelPaymentPartialCase1() throws Exception {
		
		Payment payment = Payment.builder()
                .crdtCardNum("9490100100166706")
                .cvc("946")
                .installment(0L)
                .payAmount(11000L)
                .payVat(1000L)
                .validYm("0222")
                .build();

		mockMvc.perform(post("/payment")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(payment)))
           .andDo(print()) 			
           .andExpect(status().isOk())
           ;
        
		Payment newPayment1 = Payment.builder()
                .id(1L)
                .cancelAmount(1100L)
                .cancelVat(100L)
                .build();

        mockMvc.perform(put("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment1)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
                .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("1100")))   //������� �� ��ұݾ�
                .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("100")))       //������� �� ��� �ΰ���ġ��
                ;
        
        Payment newPayment2 = Payment.builder()
                .id(1L)
                .cancelAmount(3300L)
                .build();

        mockMvc.perform(put("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment2)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
                .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("4400")))  //������� �� ��ұݾ�
                .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("400")))      //������� �� ��� �ΰ���ġ��
                ;
        
        Payment newPayment3 = Payment.builder()
                .id(1L)
                .cancelAmount(7000L)
                .build();
        
        mockMvc.perform(put("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment3)))
            .andDo(print()) 			
            .andExpect(status().isBadRequest())   //��Ұ��ɱݾ׺��� ũ�⶧���� Exception ����
            ;
        
        Payment newPayment4 = Payment.builder()
                .id(1L)
                .cancelAmount(6600L)
                .cancelVat(700L)
                .build();
        
        mockMvc.perform(put("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment4)))
            .andDo(print()) 			
            .andExpect(status().isBadRequest())   //��Ұ��� �ΰ������� �Ķ���Ͱ� ũ�⶧���� Exception ����
            ;
        
        Payment newPayment5 = Payment.builder()
                .id(1L)
                .cancelAmount(6600L)
                .cancelVat(600L)
                .build();

        mockMvc.perform(put("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment5)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
                .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("11000")))  //������� �� ��ұݾ�
                .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("1000")))      //������� �� ��� �ΰ���ġ��
                ;
        
        Payment newPayment6 = Payment.builder()
                .id(1L)
                .cancelAmount(100L)
                .build();
        
        mockMvc.perform(put("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment6)))
            .andDo(print()) 			
            .andExpect(status().isBadRequest())   //��Ұ��� �ݾ��� 0�̱⶧���� Exception ����
            ;
    }
	
	@Test
    public void cancelPaymentPartialCase2() throws Exception {
		Payment payment = Payment.builder()
                .crdtCardNum("9490100100166706")
                .cvc("946")
                .installment(0L)
                .payAmount(20000L)
                .payVat(909L)
                .validYm("0222")
                .build();
		
		mockMvc.perform(post("/payment")
	               .contentType(MediaType.APPLICATION_JSON)
	               .accept(MediaType.APPLICATION_JSON)
	               .content(objectMapper.writeValueAsString(payment)))
	           .andDo(print()) 			
	           .andExpect(status().isOk())
	           ;
		
		Payment newPayment1 = Payment.builder()
                .id(2L)
                .cancelAmount(10000L)
                .cancelVat(0L)
                .build();

        mockMvc.perform(put("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment1)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
                .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("10000")))   //������� �� ��ұݾ�
                .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("0")))       //������� �� ��� �ΰ���ġ��
                ;
        
        Payment newPayment2 = Payment.builder()
                .id(2L)
                .cancelAmount(10000L)
                .cancelVat(0L)
                .build();
        
        mockMvc.perform(put("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment2)))
            .andDo(print()) 			
            .andExpect(status().isBadRequest())   //�����ݾ� �ܿ��� ��������̳� �ΰ����� ��ҵ��� �������� ���������Ƿ� Exception ����
            ;
        
        Payment newPayment3 = Payment.builder()
                .id(2L)
                .cancelAmount(10000L)
                .cancelVat(909L)
                .build();

        mockMvc.perform(put("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment3)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
                .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("20000")))   //������� �� ��ұݾ�
                .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("909")))       //������� �� ��� �ΰ���ġ��
                ;
	}
	
	@Test
    public void cancelPaymentPartialCase3() throws Exception {
		Payment payment = Payment.builder()
                .crdtCardNum("9490100100166706")
                .cvc("946")
                .installment(0L)
                .payAmount(20000L)
                .validYm("0222")
                .build();
		
		mockMvc.perform(post("/payment")
	               .contentType(MediaType.APPLICATION_JSON)
	               .accept(MediaType.APPLICATION_JSON)
	               .content(objectMapper.writeValueAsString(payment)))
	           .andDo(print()) 			
	           .andExpect(status().isOk())
	           ;
		
		Payment newPayment1 = Payment.builder()
                .id(3L)
                .cancelAmount(10000L)
                .cancelVat(1000L)
                .build();

        mockMvc.perform(put("/payment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPayment1)))
                .andDo(print()) 			
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ID").exists())
                .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
                .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("10000")))   //������� �� ��ұݾ�
                .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("1000")))       //������� �� ��� �ΰ���ġ��
                ;
        
        Payment newPayment2 = Payment.builder()
                .id(3L)
                .cancelAmount(10000L)
                .cancelVat(909L)
                .build();
        
        mockMvc.perform(put("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment2)))
            .andDo(print()) 			
            .andExpect(status().isBadRequest())   // �ܿ� �ΰ������� ����Ϸ��� �ΰ����� �� ũ�Ƿ� Exception ����
            ;
        
        Payment newPayment3 = Payment.builder()
                .id(3L)
                .cancelAmount(10000L)
                .build();
        
        mockMvc.perform(put("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment3)))
            .andDo(print()) 			
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.ID").exists())
            .andExpect(jsonPath("$.Payment_Cancel_Dtm").exists())
            .andExpect(jsonPath("$.Cancel_Amount").value(Matchers.hasToString("20000")))   //������� �� ��ұݾ�
            .andExpect(jsonPath("$.Cancel_VAT").value(Matchers.hasToString("1818")))       //������� �� ��� �ΰ���ġ��
            ;
	}
}
