package ir.alirezaalijani.news.application.domain.error.exception;


import ir.alirezaalijani.news.application.domain.error.apierror.CustomErrorMessageGenerator;

public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(Class clazz,String message ,String... searchParamsMap) {
        super(CustomErrorMessageGenerator.generateMessage(clazz.getSimpleName(),
                message,
                CustomErrorMessageGenerator.toMap(String.class, String.class, searchParamsMap)
        ));
    }
}
