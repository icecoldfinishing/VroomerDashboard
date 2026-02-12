package etu.sprint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour sécuriser les endpoints API avec un token.
 * Le token doit être envoyé dans le header "Authorization" de la requête.
 * Le BackOffice vérifie que le token existe dans la table "token" en base
 * et que sa date d'expiration n'est pas dépassée.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Token {
}
