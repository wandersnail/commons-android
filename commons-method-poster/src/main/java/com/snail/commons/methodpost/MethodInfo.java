package com.snail.commons.methodpost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

/**
 * date: 2019/8/3 09:28
 * author: zengfansheng
 */
public class MethodInfo {
    @NonNull
    private String name;
    @Nullable
    private Parameter[] parameters;

    public MethodInfo(@NonNull String name, @Nullable Parameter... parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(@Nullable Parameter[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodInfo)) return false;

        MethodInfo that = (MethodInfo) o;

        if (!name.equals(that.name)) return false;
        if (parameters != null) {
            if (((MethodInfo) o).parameters == null) return false;
            return Arrays.equals(parameters, that.parameters);
        } else return ((MethodInfo) o).parameters == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }
    
    public static class Parameter {
        @Nullable
        private Object value;
        @NonNull
        private Class<?> type;

        public Parameter(@NonNull Class<?> type, @Nullable Object value) {
            this.type = type;
            this.value = value;
        }

        @Nullable
        public Object getValue() {
            return value;
        }

        public void setValue(@Nullable Object value) {
            this.value = value;
        }

        @NonNull
        public Class<?> getType() {
            return type;
        }

        public void setType(@NonNull Class<?> type) {
            this.type = type;
        }
    }
}
