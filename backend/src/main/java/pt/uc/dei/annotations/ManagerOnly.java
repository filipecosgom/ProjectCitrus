package pt.uc.dei.annotations;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to indicate that a resource or method requires manager privileges.
 * <p>
 * Can be applied to classes or methods to restrict access to managers only.
 * Used with JAX-RS filters/interceptors for access control.
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface ManagerOnly {}