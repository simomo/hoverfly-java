package io.specto.hoverfly.junit.dsl.matchers;


import io.specto.hoverfly.junit.core.model.FieldMatcher;

@FunctionalInterface
public interface RequestFieldMatcher {



    FieldMatcher getFieldMatcher();


}
