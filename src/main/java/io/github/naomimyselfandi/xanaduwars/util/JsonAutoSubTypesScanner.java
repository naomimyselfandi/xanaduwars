package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
class JsonAutoSubTypesScanner implements Jackson2ObjectMapperBuilderCustomizer {

    static final Consumer<ObjectMapper> POST_CONFIGURER = objectMapper -> {
        var root = "io.github.naomimyselfandi.xanaduwars";
        var classGraph = new ClassGraph().acceptPackages(root).enableAnnotationInfo().ignoreClassVisibility();
        try (var scan = classGraph.scan()) {
            for (var type : scan.getClassesWithAnnotation(JsonAutoSubTypes.class).loadClasses()) {
                objectMapper.registerSubtypes(scan.getClassesImplementing(type).loadClasses());
            }
        }
    };

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.postConfigurer(POST_CONFIGURER);
    }

}
