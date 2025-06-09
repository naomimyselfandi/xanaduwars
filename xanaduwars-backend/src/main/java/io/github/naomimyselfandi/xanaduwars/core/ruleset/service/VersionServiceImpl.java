package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
record VersionServiceImpl(List<Version> all, List<Version> published) implements VersionService {}
