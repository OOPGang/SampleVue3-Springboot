// package oop.io.demo.pass;

// import java.net.http.HttpHeaders;
// import java.util.*;

// import javax.validation.ConstraintViolationException;

// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.context.request.WebRequest;
// import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// @ControllerAdvice
// public class RestExceptionHandler extends ResponseEntityExceptionHandler {

//     @ExceptionHandler({ PassNotFoundException.class })
//     protected ResponseEntity<Object> handleNotFound(
//       Exception ex, WebRequest request) {
//         return handleExceptionInternal(ex, "Pass not found", 
//           new HttpHeaders(), HttpStatus.NOT_FOUND, request);
//     }

//     @ExceptionHandler({ PassIdMismatchException.class, 
//       ConstraintViolationException.class, 
//       DataIntegrityViolationException.class })
//     public ResponseEntity<Object> handleBadRequest(
//       Exception ex, WebRequest request) {
//         return handleExceptionInternal(ex, ex.getLocalizedMessage(), 
//           new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//     }
// }