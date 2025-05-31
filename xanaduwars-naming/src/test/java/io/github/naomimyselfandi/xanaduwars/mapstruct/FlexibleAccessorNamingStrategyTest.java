package io.github.naomimyselfandi.xanaduwars.mapstruct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlexibleAccessorNamingStrategyTest {

    private FlexibleAccessorNamingStrategy fixture;

    @BeforeEach
    void setup() {
        fixture = new FlexibleAccessorNamingStrategy();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,true
            1,false
            """)
    void isGetterMethod_Get(int arity, boolean expected) {
        var method = method("getFoo", Object.class, TypeKind.DECLARED, arity);
        assertEquals(expected, fixture.isGetterMethod(method));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,true
            1,false
            """)
    void isGetterMethod_Is(int arity, boolean expected) {
        var method = method("isBar", boolean.class, TypeKind.BOOLEAN, arity);
        assertEquals(expected, fixture.isGetterMethod(method));
    }

    @Test
    void isGetterMethod_Add() {
        var method = method("addFoo", Object.class, TypeKind.BOOLEAN, 0);
        assertFalse(fixture.isGetterMethod(method));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0,true
            1,false
            """)
    void isGetterMethod_FluentGetter(int arity, boolean expected) {
        var method = method("foo", Object.class, TypeKind.DECLARED, arity);
        assertEquals(expected, fixture.isGetterMethod(method));
    }

    @Test
    void isGetterMethod_WhenTheMethodReturnsVoid_ThenFalse() {
        var method = method("foo", void.class, TypeKind.VOID, 0);
        assertFalse(fixture.isGetterMethod(method));
    }

    @Test
    void isGetterMethod_WhenTheMethodIsFromALibrary_ThenFalse() {
        var method = method("removeAll", boolean.class, TypeKind.BOOLEAN, 0);
        var element = enclosingType(List.class);
        when(method.getEnclosingElement()).thenReturn(element);
        assertFalse(fixture.isGetterMethod(method));
    }

    @Test
    void getPropertyName_Get() {
        var method = method("getFoo", Object.class, TypeKind.DECLARED, 0);
        assertEquals("foo", fixture.getPropertyName(method));
    }

    @Test
    void getPropertyName_Is() {
        var method = method("isBar", boolean.class, TypeKind.BOOLEAN, 0);
        assertEquals("bar", fixture.getPropertyName(method));
    }

    @Test
    void getPropertyName_FluentGetter() {
        var method = method("foo", Object.class, TypeKind.DECLARED, 0);
        assertEquals("foo", fixture.getPropertyName(method));
    }

    private Name name(String source) {
        var name = mock(Name.class);
        when(name.toString()).thenReturn(source);
        return name;
    }

    private TypeMirror type(Class<?> source, TypeKind kind) {
        var type = mock(TypeMirror.class);
        when(type.toString()).thenReturn(source.getCanonicalName());
        when(type.getKind()).thenReturn(kind);
        return type;
    }

    private Element enclosingType(Class<?> source) {
        var t = type(source, TypeKind.DECLARED);
        var element = mock(Element.class);
        when(element.asType()).thenReturn(t);
        return element;
    }

    private ExecutableElement method(String name, Class<?> returnType, TypeKind returnKind, int arity) {
        var n = name(name);
        var t = type(returnType, returnKind);
        var e = enclosingType(FlexibleAccessorNamingStrategyTest.class);
        var p = IntStream.range(0, arity).mapToObj(_ -> mock(VariableElement.class)).toList();
        var result = mock(ExecutableElement.class);
        when(result.getSimpleName()).thenReturn(n);
        when(result.getReturnType()).thenReturn(t);
        when(result.getEnclosingElement()).thenReturn(e);
        doReturn(p).when(result).getParameters();
        return result;
    }

}
