package st.networkers.rimor.inject;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireAnnotations {
    Class<? extends Annotation>[] value();
}