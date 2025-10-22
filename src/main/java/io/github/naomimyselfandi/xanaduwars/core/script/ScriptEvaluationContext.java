package io.github.naomimyselfandi.xanaduwars.core.script;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.*;
import org.springframework.expression.spel.support.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
abstract class ScriptEvaluationContext<T> implements EvaluationContext {

    private static final TypedValue ROOT_OBJECT = new TypedValue(Undefined.UNDEFINED);

    private static final List<PropertyAccessor> PROPERTY_ACCESSORS = createPropertyAccessors();

    private static final List<ConstructorResolver> CONSTRUCTOR_RESOLVERS = createConstructorResolvers();

    private static final List<MethodResolver> METHOD_RESOLVERS = createMethodResolvers(PROPERTY_ACCESSORS);

    private static final TypeLocator TYPE_LOCATOR = createTypeLocator();

    private static final TypeConverter TYPE_CONVERTER = createTypeConverter();

    private static final TypeComparator TYPE_COMPARATOR = createTypeComparator();

    private static final OperatorOverloader OPERATOR_OVERLOADER = createOperatorOverloader();

    final Map<String, Object> variables = new HashMap<>(); // not thread-safe, but that's okay

    final T parent;

    @Override
    public @Nullable Object lookupVariable(String name) {
        return variables.containsKey(name) ? variables.get(name) : lookupMissingVariable(name);
    }

    abstract @Nullable Object lookupMissingVariable(String name);

    @Override
    public void setVariable(String name, @Nullable Object value) {
        if (name.equals("$")) {
            throw new IllegalArgumentException("Cannot assign to $.");
        } else {
            variables.put(name, value);
        }
    }

    @Override
    public TypedValue getRootObject() {
        return ROOT_OBJECT;
    }

    @Override
    public List<PropertyAccessor> getPropertyAccessors() {
        return PROPERTY_ACCESSORS;
    }

    @Override
    public List<ConstructorResolver> getConstructorResolvers() {
        return CONSTRUCTOR_RESOLVERS;
    }

    @Override
    public List<MethodResolver> getMethodResolvers() {
        return METHOD_RESOLVERS;
    }

    @Override
    public @Nullable BeanResolver getBeanResolver() {
        return null;
    }

    @Override
    public TypeLocator getTypeLocator() {
        return TYPE_LOCATOR;
    }

    @Override
    public TypeConverter getTypeConverter() {
        return TYPE_CONVERTER;
    }

    @Override
    public TypeComparator getTypeComparator() {
        return TYPE_COMPARATOR;
    }

    @Override
    public OperatorOverloader getOperatorOverloader() {
        return OPERATOR_OVERLOADER;
    }

    private static List<PropertyAccessor> createPropertyAccessors() {
        return List.of(
                new VariableAccessor(),
                new ConstantAwarePropertyAccessor(),
                new MapAccessor()
        );
    }

    private static List<ConstructorResolver> createConstructorResolvers() {
        return List.of(new ReflectiveConstructorResolver());
    }

    @SuppressWarnings("SameParameterValue")
    private static List<MethodResolver> createMethodResolvers(List<PropertyAccessor> propertyAccessors) {
        return Stream.concat(
                Stream.of(new ConstantAwareMethodResolver(), new FlowMethodResolver()),
                propertyAccessors.stream().map(FunctionMethodResolver::new)
        ).toList();
    }

    private static TypeLocator createTypeLocator() {
        var typeLocator = new StandardTypeLocator();
        typeLocator.registerImport("java.util");
        typeLocator.registerImport("java.util.function");
        typeLocator.registerImport("java.util.stream");
        typeLocator.registerImport("io.github.naomimyselfandi.xanaduwars.core.message");
        typeLocator.registerImport("io.github.naomimyselfandi.xanaduwars.core.messages");
        typeLocator.registerImport("io.github.naomimyselfandi.xanaduwars.core.model");
        return typeLocator;
    }

    private static TypeConverter createTypeConverter() {
        var conversionService = new OmniBooleanConversionService();
        conversionService.addConverter(new FunctionToProxyConverter());
        return new StandardTypeConverter(conversionService);
    }

    private static TypeComparator createTypeComparator() {
        return new StandardTypeComparator();
    }

    private static OperatorOverloader createOperatorOverloader() {
        return new StandardOperatorOverloader();
    }

}
