package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Baz")
record Bar() implements Helper {}
