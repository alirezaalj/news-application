package ir.alirezaalijani.news.application.initializers.event;

import ir.alirezaalijani.news.application.initializers.ApplicationInitializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReadyInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationInitializer appStartupInitializer;

    public ApplicationReadyInitializer(@Qualifier("startupInit") ApplicationInitializer appStartupInitializer) {
        this.appStartupInitializer = appStartupInitializer;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        appStartupInitializer.init();
    }
}
