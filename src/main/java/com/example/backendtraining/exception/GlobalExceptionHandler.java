package com.example.backendtraining.exception;

import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ValidationErrorResponse> handleBindException(BindException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.putIfAbsent(error.getObjectName(), error.getDefaultMessage());
        }
        return badRequest(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> {
                String field;
                if (error instanceof FieldError fieldError) {
                    field = fieldError.getField();
                    if (result.getContainerIndex() != null) {
                        field = parameterName + "[" + result.getContainerIndex() + "]." + field;
                    }
                } else if (result.getContainerIndex() != null) {
                    field = parameterName + "[" + result.getContainerIndex() + "]";
                } else {
                    field = parameterName;
                }
                errors.putIfAbsent(field, error.getDefaultMessage());
            });
        });
        return badRequest(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return badRequest(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        String message = readableJacksonMessage(ex);
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("body", message);
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(400, message, errors));
    }

    private ResponseEntity<ValidationErrorResponse> badRequest(Map<String, String> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(400, "Validation failed", errors));
    }

    private String readableJacksonMessage(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof InvalidFormatException ife) {
            String field = jacksonPath(ife);
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String allowed = Arrays.stream(ife.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                String prefix = field.isBlank() ? "Value" : field;
                return prefix + " must be one of: " + allowed;
            }
            return field.isBlank() ? "Invalid request body" : field + " has an invalid value";
        }
        if (cause instanceof MismatchedInputException) {
            return "Invalid request body";
        }
        return "Invalid request body";
    }

    private String jacksonPath(MismatchedInputException ex) {
        if (ex.getPath() == null || ex.getPath().isEmpty()) {
            return "";
        }
        StringBuilder path = new StringBuilder();
        ex.getPath().forEach(ref -> {
            if (ref.getPropertyName() != null) {
                if (!path.isEmpty()) {
                    path.append('.');
                }
                path.append(ref.getPropertyName());
            } else if (ref.getIndex() >= 0) {
                path.append('[').append(ref.getIndex()).append(']');
            }
        });
        return path.toString();
    }
}
