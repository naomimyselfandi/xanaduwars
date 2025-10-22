package io.github.naomimyselfandi.xanaduwars.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonAutoSubTypes
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "llama")
interface Helper {}
