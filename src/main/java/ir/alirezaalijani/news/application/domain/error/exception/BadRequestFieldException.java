package ir.alirezaalijani.news.application.domain.error.exception;

import org.springframework.validation.BindingResult;

public class BadRequestFieldException  extends RuntimeException {

    private BindingResult bindingResult;
    public BadRequestFieldException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
