package com.sparta.newsfeed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.crypto.Data;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionController {

    //비밀번호 불일치
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<String> passwordMismatchException(PasswordMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST + " :  " + ex.getMessage());
    }

    //데이터 못찾음
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> dataNotFoundException(DataNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpStatus.NOT_FOUND + " : " + ex.getMessage());
    }

    //중복 데이터 검사
    @ExceptionHandler(DataDuplicationException.class)
    public ResponseEntity<String> dataDuplicationException(DataDuplicationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(HttpStatus.CONFLICT + " : " + ex.getMessage());
    }

    //@Valid 할때 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.append(fieldName).append(": ").append(message).append(". ");
        });
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }


    //입력으로 다른 타입 입력했을 때 에러 (ex . @PathVariable 이 Long 타입인데 String 타입 입력했을때)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String idTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ex.getName() + " 의 입력된 값은 잘못된 입력 입니다. " +  Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + " 타입으로 " + " 정확히 입력해주세요."
                + " 당신이 넣은 값은 " + ex.getValue() + " 입니다.";
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public String accessRights(ResponseStatusException ex) {
        return ex.getStatusCode() + ": " + ex.getReason() ;
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleFileProcessingException(FileProcessingException ex) {
        // 예외 메시지를 클라이언트에게 전달
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}

