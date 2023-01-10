package ir.alirezaalijani.news.application.domain.error.exception;

import ir.alirezaalijani.news.application.domain.error.apierror.CustomErrorMessageGenerator;

public class ForbiddenException extends RuntimeException{

    public ForbiddenException(Class clazz, String message , String... searchParamsMap) {
        super(CustomErrorMessageGenerator.generateMessage(clazz.getSimpleName(),
                message,
                CustomErrorMessageGenerator.toMap(String.class, String.class, searchParamsMap)
        ));
    }
}
