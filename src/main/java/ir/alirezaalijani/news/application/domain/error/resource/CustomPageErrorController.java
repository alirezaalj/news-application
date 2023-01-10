package ir.alirezaalijani.news.application.domain.error.resource;

import ir.alirezaalijani.news.application.domain.error.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class CustomPageErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,HttpServletResponse response) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String forwardPath = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);

        if (status!=null){
            int parsStatus=400;
            try {
                parsStatus=Integer.parseInt(status.toString());
            }catch (Exception e){
                log.error("Cannot parse error status {}",status);
            }
            if (forwardPath.startsWith("/api")){
                throwFromStatusCode(parsStatus);
            }else {
               return pageErrorStatusCode(parsStatus);
            }
        }
        return "error/error-default";
    }

    private String pageErrorStatusCode(int statusCode){
        return switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND -> "error/error-404-default";
            case INTERNAL_SERVER_ERROR -> "error/error-500-default";
            case FORBIDDEN -> "error/error-403-default";
            case UNAUTHORIZED -> "error/error-401-default";
            default -> "error/error-default";
        };
    }
    private void throwFromStatusCode(int statusCode) {
        switch (HttpStatus.valueOf(statusCode)) {
            case NOT_FOUND -> throw new PathNotFoundException(CustomPageErrorController.class, " : path not found.");
            case UNAUTHORIZED -> throw new AccessDeniedException(CustomPageErrorController.class, " : UNAUTHORIZED.");
            case FORBIDDEN -> throw new ForbiddenException(CustomPageErrorController.class, " : FORBIDDEN.");
            case INTERNAL_SERVER_ERROR ->
                    throw new InternalServerException(CustomPageErrorController.class, " : Server error try now.");
            case TOO_MANY_REQUESTS ->
                    throw new TooManyRequestsException(CustomPageErrorController.class, " : Too Many Requests. Your Blocked!");
            default -> throw new BadRequestException(CustomPageErrorController.class, " : BadRequest.");
        }
    }
}
