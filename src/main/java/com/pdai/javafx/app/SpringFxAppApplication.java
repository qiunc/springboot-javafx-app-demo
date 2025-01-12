package com.pdai.javafx.app;

import com.pdai.javafx.app.fx.AbstractFxApplication;
import com.pdai.javafx.app.fx.FxmlView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Arrays;

/**
 * <b>ClassName</b>: SpringFxAppApplication <br/>
 *
 * <b>Description</b>: SpringFxAppApplication <br/>
 *
 * <b>Date</b>: Apr 12, 2019 11:05:23 AM <br/>
 *
 * @author pdai
 * @version Apr 12, 2019
 */
@SpringBootApplication
public class SpringFxAppApplication extends AbstractFxApplication {

    public static void main(String[] args) {
//        SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringFxAppApplication.class);
//        builder.headless(false).run(args);
        run(SpringFxAppApplication.class,
                Arrays.asList(FxmlView.MODULE_DASHBOARD, FxmlView.MODULE_PROFILE, FxmlView.MAIN),
                FxmlView.MAIN, args);
    }
}
