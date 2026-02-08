package br.com.mini_erp.config;

import br.com.mini_erp.shared.exception.ApiError;
import br.com.mini_erp.shared.exception.BusinessException;
import br.com.mini_erp.shared.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice // Indica que esta classe é um "advice" global para controllers
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Lida com recursos não encontrados (404 NOT FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.warn("Resource Not Found: " + ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }

    // Lida com regras de negócio ou validações customizadas (400 BAD REQUEST)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        logger.warn("Business Exception: " + ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }

    // Lida com falhas de validação de argumentos de métodos (ex: @Valid em DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.error("Validation Error: " + ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(status.value(), "Validation Error", "Um ou mais campos estão inválidos.", request.getRequestURI(), errors);
        return new ResponseEntity<>(apiError, status);
    }

    // Lida com RuntimeException genéricas (500 INTERNAL SERVER ERROR)
    // Este é um "catch-all" para qualquer exceção não tratada especificamente
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleGenericRuntimeException(RuntimeException ex, HttpServletRequest request) {
        logger.error("Internal Server Error: " + ex.getMessage(), ex); // Loga o stack trace
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(), "Ocorreu um erro interno no servidor.", request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }

    // Opcional: Lidar com exceções mais genéricas se necessário
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        logger.error("An unexpected error occurred: " + ex.getMessage(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(), "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.", request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }
}
