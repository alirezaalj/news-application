package ir.alirezaalijani.news.application.initializers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component("startupInit")
public class ApplicationStartupInitializerImpl implements ApplicationInitializer {

    @Override
    public void init() {
        log.info("application startup init {}", LocalDateTime.now());
    }

}
