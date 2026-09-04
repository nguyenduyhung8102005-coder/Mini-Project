package vn.hungjava.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler({
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "Handle exception when the data invalid. (@RequestBody)",
                                            summary = "Handle Bad Request",
                                            value = """
                            {
                                "timestamp": "2024-04-07T11:38:54",
                                "status": 400,
                                "path": "/api/v1/...",
                                "error": "Invalid Payload",
                                "message": "{data} must be not null"
                            }
                            """
                                    )
                            )
                    }
            )
    })
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[") + 1;
            int end = message.lastIndexOf("]") - 1;
            message = message.substring(start, end);
            errorResponse.setError("Invalid Payload");
            errorResponse.setMessage(message);
        } else if (e instanceof MissingServletRequestParameterException) {
            errorResponse.setError("Invalid Parameter");
            errorResponse.setMessage(message);
        } else if (e instanceof ConstraintViolationException) {
            errorResponse.setError("Invalid Parameter");
            errorResponse.setMessage(message.substring(message.indexOf(" ") + 1));
        } else {
            errorResponse.setError("Invalid Data");
            errorResponse.setMessage(message);
        }

        return errorResponse;
    }

    @ExceptionHandler(ResouceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "Bad Request", // Lưu ý: Trong ảnh ghi "Bad Request", nhưng thường 404 là "Not Found"
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "404 Response",
                                            summary = "Handle exception when resource not found",
                                            value = """
                            {
                                "timestamp": "2023-10-19T06:07:35.321+00:00",
                                "status": 404,
                                "path": "/api/v1/...",
                                "error": "Not Found",
                                "message": "{data} not found"
                            }
                            """
                                    )
                            )
                    }
            )
    })
    public ErrorResponse handleResourceNotFoundException(ResouceNotFoundException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError("Not Found");
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden", // Lưu ý: Trong ảnh ghi "Bad Request", nhưng thường 404 là "Not Found"
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "403 Response",
                                            summary = "Handle exception when resource Forbidden",
                                            value = """
                            {
                                "timestamp": "2023-10-19T06:07:35.321+00:00",
                                "status": 403,
                                "path": "/api/v1/...",
                                "error": "Forbidden",
                                "message": "Access is denied"
                            }
                            """
                                    )
                            )
                    }
            )
    })
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError("Forbidden");
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }


    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(CONFLICT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "409 Response",
                                    summary = "Handle exception when input data is conflicted",
                                    value = """
                    {
                        "timestamp": "2023-10-19T06:07:35.321+00:00",
                        "status": 409,
                        "path": "/api/v1/...",
                        "error": "Conflict",
                        "message": "{data} exists, Please try again!"
                    }
                    """
                            ))})
    })

    public ErrorResponse handleDuplicateKeyException(InvalidDataException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(CONFLICT.value());
        errorResponse.setError(CONFLICT.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(
            AuthenticationException e,
            WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(
                HttpStatus.UNAUTHORIZED.value()
        );
        errorResponse.setPath(
                request.getDescription(false)
                        .replace("uri=", "")
        );
        errorResponse.setError(
                HttpStatus.UNAUTHORIZED.getReasonPhrase()
        );
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }

    @Getter
    @Setter
    private class ErrorResponse {
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String message;
    }

}
