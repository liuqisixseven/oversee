package com.chd.boot.starter.job.annotation;

import com.chd.boot.starter.job.config.XxlJobConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zyf
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ XxlJobConfiguration.class })
public @interface EnableXxlJob {

}
