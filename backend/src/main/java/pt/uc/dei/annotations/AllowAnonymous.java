package pt.uc.dei.annotations;
import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to indicate that a resource or method allows anonymous (unauthenticated) access.
 * <p>
 * Can be applied to classes or methods to bypass authentication requirements.
 * Used with JAX-RS filters/interceptors for access control.
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface AllowAnonymous {}
