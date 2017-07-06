package com.terheyden.prefs;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public enum With {
    ;

    private static final ThreadLocal<StringValidator> stringValid = ThreadLocal.withInitial(StringValidator::new);
    private static final ThreadLocal<CollectionValidator> collValid = ThreadLocal.withInitial(CollectionValidator::new);
    private static final ThreadLocal<MapValidator> mapValid = ThreadLocal.withInitial(MapValidator::new);

    public static StringValidator str(String str) {
        return stringValid.get().setStr(str);
    }

    public static CollectionValidator coll(Collection coll) {
        return collValid.get().setColl(coll);
    }

    public static MapValidator map(Map map) {
        return mapValid.get().setMap(map);
    }

    public static class StringValidator {

        private String str;

        private StringValidator setStr(String str) {
            this.str = str;
            return this;
        }

        public boolean isNull() {
            return str == null;
        }

        public boolean isNotNull() {
            return str != null;
        }

        public String ifNull(String useIfNull) {
            return str == null ? useIfNull : str;
        }

        public String ifNull(Supplier<String> useIfNull) {

            if (str != null) {
                return str;
            }

            return useIfNull.get();
        }

        public boolean isEmpty() {
            return str == null || str.isEmpty();
        }

        public boolean isNotEmpty() {
            return str != null && !str.isEmpty();
        }

        public String ifEmpty(String useIfEmpty) {

            if (isEmpty()) {
                return useIfEmpty;
            }

            return str;
        }

        public String ifEmpty(Supplier<String> useIfEmpty) {

            if (isEmpty()) {
                return useIfEmpty.get();
            }

            return str;
        }

        /**
         * A String is blank if it is null, empty, or consists of only whitespace.
         * See also: {@link String#trim()}.
         * @return true if the string is blank
         */
        public boolean isBlank() {
            return isEmpty() || str.trim().isEmpty();
        }

        /**
         * A String is blank if it is null, empty, or consists of only whitespace.
         * See also: {@link String#trim()}.
         * @return true if the string is not null, and has content other than whitespace
         */
        public boolean isNotBlank() {
            return str != null && !str.trim().isEmpty();
        }

        /**
         * A String is blank if it is null, empty, or consists of only whitespace.
         * See also: {@link String#trim()}.
         */
        public String ifBlank(String useIfBlank) {

            if (isBlank()) {
                return useIfBlank;
            }

            return str;
        }

        /**
         * A String is blank if it is null, empty, or consists of only whitespace.
         * See also: {@link String#trim()}.
         * @return true if the string is blank
         */
        public String ifBlank(Supplier<String> useIfBlank) {

            if (isBlank()) {
                return useIfBlank.get();
            }

            return str;
        }

        /**
         * Trim the string, never returning null, even if it started out that way.
         */
        public String trimToEmpty() {
            return isEmpty() ? "" : str.trim();
        }

        /**
         * Trim the string, returning null if it becomes (or starts) empty.
         */
        public String trimToNull() {

            // If it's already null or empty, return.
            if (isEmpty()) {
                return null;
            }

            // Try trimming...
            str = str.trim();

            return str.isEmpty() ? null : str;
        }

    } // end StringValidator class.

    public static class CollectionValidator {

        private Collection<?> coll;

        private CollectionValidator setColl(Collection<?> coll) {
            this.coll = coll;
            return this;
        }

        public boolean isNull() {
            return coll == null;
        }

        public boolean isNotNull() {
            return coll != null;
        }

        public <T extends Collection<?>> T ifNull(T useIfNull) {
            return coll == null ? useIfNull : (T) coll;
        }

        public <T extends Collection<?>> T ifNull(Supplier<T> useIfNull) {

            if (coll != null) {
                return (T) coll;
            }

            return useIfNull.get();
        }

        public boolean isEmpty() {
            return coll == null || coll.isEmpty();
        }

        public boolean isNotEmpty() {
            return coll != null && !coll.isEmpty();
        }

        public <T extends Collection<?>> T ifEmpty(T useIfEmpty) {

            if (isEmpty()) {
                return useIfEmpty;
            }

            return (T) coll;
        }

        public <T extends Collection<?>> T ifEmpty(Supplier<T> useIfEmpty) {

            if (isEmpty()) {
                return useIfEmpty.get();
            }

            return (T) coll;
        }

    } // end CollectionValidator class.

    public static class MapValidator {

        private Map<?, ?> map;

        private MapValidator setMap(Map<?, ?> map) {
            this.map = map;
            return this;
        }

        public boolean isNull() {
            return map == null;
        }

        public boolean isNotNull() {
            return map != null;
        }

        public boolean isEmpty() {
            return map == null || map.isEmpty();
        }

        public boolean isNotEmpty() {
            return map != null && !map.isEmpty();
        }

        public <T extends Map> T ifEmpty(T useIfEmpty) {

            if (isEmpty()) {
                return useIfEmpty;
            }

            return (T) map;
        }

        public <T extends Map> T ifEmpty(Supplier<T> useIfEmpty) {

            if (isEmpty()) {
                return useIfEmpty.get();
            }

            return (T) map;
        }

    } // end MapValidator class.
}
