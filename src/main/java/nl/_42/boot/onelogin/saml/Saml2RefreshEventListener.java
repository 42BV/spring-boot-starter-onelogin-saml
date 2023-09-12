package nl._42.boot.onelogin.saml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnClass(name = "org.springframework.cloud.endpoint.event.RefreshEvent")
public class Saml2RefreshEventListener {

    private final ApplicationContext applicationContext;
    private final Saml2Properties properties;

    public Saml2RefreshEventListener(ApplicationContext applicationContext, Saml2Properties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @EventListener
    public void handleContextRefresh(org.springframework.cloud.endpoint.event.RefreshEvent event) {
        log.info("Refreshing SAML2 properties after event '{}'...", event.getEvent());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(properties);
    }

}
