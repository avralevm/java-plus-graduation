package ru.practicum.constants;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class Methods {
    public static void copyFields(Object pasteNonNullFields, Object copyNonNullFields) {
        BeanUtils.copyProperties(copyNonNullFields, pasteNonNullFields, getNotNullFields(copyNonNullFields));
    }

    public static String[] getNotNullFields(Object object) {
        BeanWrapper wrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        Set<String> emptyFields = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object value = wrapper.getPropertyValue(propertyDescriptor.getName());
            if (value == null) {
                emptyFields.add(propertyDescriptor.getName());
            }
        }
        return emptyFields.toArray(new String[0]);
    }
}
