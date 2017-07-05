package com.terheyden.prefs.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public enum AnnotationFinder {
    ;

    /**
     * Does not return null.
     * @return specified annotations on the specified class obj, if any
     */
    public static List<Annotation> findAnnotatedClass(Object obj, Class<?> annotationToFind) {

        Class<?> objClass = obj.getClass();
        List<Annotation> annotList = new LinkedList<>();

        // Note - don't use declared annotations.
        for (Annotation ann : objClass.getAnnotations()) {

            if (ann.annotationType().equals(annotationToFind)) {
                annotList.add(ann);
            }
        }

        return annotList;
    }

    /**
     * Does not return null.
     * @return methods with the specified annotation attached, never null
     */
    public static List<AnnotationResult<Method>> findAnnotatedMethods(Object obj, Class<?> annotationToFind) {

        Class<?> objClass = obj.getClass();
        List<AnnotationResult<Method>> methList = new LinkedList<>();

        for (Method meth : objClass.getDeclaredMethods()) {
            for (Annotation ann : meth.getDeclaredAnnotations()) {

                if (ann.annotationType().equals(annotationToFind)) {
                    methList.add(new AnnotationResult<>(obj, meth, ann));
                }
            }
        }

        return methList;
    }

    /**
     * Does not return null.
     * @return fields with the specified annotation attached, never null
     */
    public static List<AnnotationResult<Field>> findAnnotatedFields(Object obj, Class<?> annotationToFind) {

        Class<?> objClass = obj.getClass();
        List<AnnotationResult<Field>> fieldList = new LinkedList<>();

        for (Field field : objClass.getDeclaredFields()) {
            for (Annotation ann : field.getDeclaredAnnotations()) {

                if (ann.annotationType().equals(annotationToFind)) {
                    fieldList.add(new AnnotationResult<>(obj, field, ann));
                }
            }
        }

        return fieldList;
    }

    public static class AnnotationResult<T> {
        public final Object obj;
        public final T element;
        public final Annotation annotation;

        public AnnotationResult(Object obj, T element, Annotation annotation) {
            this.obj = obj;
            this.element = element;
            this.annotation = annotation;
        }
    }
}
