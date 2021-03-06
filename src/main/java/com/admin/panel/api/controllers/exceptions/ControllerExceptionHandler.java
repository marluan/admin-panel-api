package com.admin.panel.api.controllers.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.admin.panel.api.interceptors.HttpLogInterceptor;
import com.admin.panel.api.security.exception.UnauthorizedException;
import com.admin.panel.api.services.exceptions.AuthenticationCredentialsNotFoundException;
import com.admin.panel.api.services.exceptions.MailException;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	@Autowired
	private HttpLogInterceptor httpLogInterceptor;
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> dataIntegrity(MethodArgumentNotValidException e, HttpServletRequest request) {

		Integer status = HttpStatus.UNPROCESSABLE_ENTITY.value();
		String message = "Erro de validação.";
		Long timestamp = System.currentTimeMillis();
		String path = request.getRequestURI().toString();
		StackTraceElement[] cause = e.getStackTrace();

		ValidationError validationError = new ValidationError(status, message, timestamp, path, cause);

		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			validationError.addError(fieldError.getField(), fieldError.getDefaultMessage());
		}
		
		httpLogInterceptor.afterCompletion(validationError, request);

		return ResponseEntity.status(status).body(validationError);
	}

	@ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
	public ResponseEntity<StandardError> authorization(AuthenticationCredentialsNotFoundException e,
			HttpServletRequest request) {

		Integer status = HttpStatus.UNPROCESSABLE_ENTITY.value();
		String message = e.getMessage();
		Long timestamp = System.currentTimeMillis();
		String path = request.getRequestURI().toString();
		StackTraceElement[] cause = e.getStackTrace();

		ValidationError validationError = new ValidationError(status, message, timestamp, path, cause);
		validationError.addError("password", e.getMessage());
		
		httpLogInterceptor.afterCompletion(validationError, request);

		return ResponseEntity.status(status).body(validationError);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<StandardError> authorization(UnauthorizedException e, HttpServletRequest request) {

		Integer status = HttpStatus.UNAUTHORIZED.value();
		String message = e.getMessage();
		Long timestamp = System.currentTimeMillis();
		String path = request.getRequestURI().toString();
		StackTraceElement[] cause = e.getStackTrace();

		StandardError standardError = new StandardError(status, message, timestamp, path, cause);
		httpLogInterceptor.afterCompletion(standardError, request);

		return ResponseEntity.status(status).body(standardError);
	}
	
	@ExceptionHandler(MailException.class)
	public ResponseEntity<StandardError> authorization(MailException e, HttpServletRequest request) {

		Integer status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		String message = e.getMessage();
		Long timestamp = System.currentTimeMillis();
		String path = request.getRequestURI().toString();
		StackTraceElement[] cause = e.getStackTrace();

		StandardError standardError = new StandardError(status, message, timestamp, path, cause);
		httpLogInterceptor.afterCompletion(standardError, request);

		return ResponseEntity.status(status).body(standardError);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> authorization(Exception e, HttpServletRequest request) {

		e.printStackTrace();
		
		Integer status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		String message = e.getMessage();
		Long timestamp = System.currentTimeMillis();
		String path = request.getRequestURI().toString();
		StackTraceElement[] cause = e.getStackTrace();

		StandardError standardError = new StandardError(status, message, timestamp, path, cause);
		httpLogInterceptor.afterCompletion(standardError, request);

		return ResponseEntity.status(status).body(standardError);
	}
}
